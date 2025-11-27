package com.eliemichel.polyfinite.game;

import com.eliemichel.polyfinite.database.DBConnectMySQL;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Manages all quests for the current level.
 * Handles progress tracking, completion detection, and reward granting.
 */
public class QuestManager {

    private ArrayList<Quest> quests; // Active quests for this run
    private ArrayList<Quest> allQuests;
    private int levelNumber;
    private int saveSlotId;
    private PlayerCurrencies playerCurrencies;

    // Callback for when a quest is completed
    private Runnable onQuestCompleted;
    private Runnable onQuestProgressChanged;

    public QuestManager(int levelNumber, int saveSlotId) {
        this.quests = new ArrayList<>();
        this.allQuests = new ArrayList<>();
        this.levelNumber = levelNumber;
        this.saveSlotId = saveSlotId;
        this.playerCurrencies = PlayerCurrencies.getInstance();
    }

    // Initialize quests from level data
    public void initializeQuests(ArrayList<QuestDefinition> questDefinitions) {
        quests.clear();
        allQuests.clear();

        for (int i = 0; i < questDefinitions.size(); i++) {
            QuestDefinition def = questDefinitions.get(i);
            Quest quest = def.createQuest(levelNumber, i);
            if (quest != null) {
                allQuests.add(quest);
            }
        }

        // Load saved progress from database
        loadQuestProgress();
        selectActiveQuests();

        System.out.println("Initialized " + quests.size() + " active quests for level " + levelNumber);
    }

    // Load quest progress from database
    private void loadQuestProgress() {
        DBConnectMySQL db = new DBConnectMySQL();

        if (!db.isConnected()) {
            System.out.println("Cannot load quest progress - database not connected");
            return;
        }

        try {
            String sql = "SELECT quest_id, current_progress, completed, reward_granted " +
                         "FROM quest_progress WHERE save_slot_id = " + saveSlotId + 
                         " AND level_number = " + levelNumber;

            ResultSet rs = db.getStatement().executeQuery(sql);

            while (rs.next()) {
                String questId = rs.getString("quest_id");
                int progress = rs.getInt("current_progress");
                boolean completed = rs.getBoolean("completed");
                boolean rewardGranted = rs.getBoolean("reward_granted");

                // Find matching quest and restore progress
                for (Quest quest : allQuests) {
                    if (quest.getQuestId().equals(questId)) {
                        quest.setCurrentProgress(progress);
                        quest.setCompleted(completed);
                        quest.setRewardGranted(rewardGranted);
                        break;
                    }
                }
            }

            db.closeConnection();
            System.out.println("Loaded quest progress for level " + levelNumber);

        } catch (Exception e) {
            System.out.println("Error loading quest progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectActiveQuests() {
        quests.clear();

        for (Quest quest : allQuests) {
            if (!quest.isCompleted()) {
                quests.add(quest);
            }
            if (quests.size() >= 3) {
                break; // Only 3 active quests per run
            }
        }
    }

    // Save quest progress to database
    public void saveQuestProgress() {
        DBConnectMySQL db = new DBConnectMySQL();

        if (!db.isConnected()) {
            System.out.println("Cannot save quest progress - database not connected");
            return;
        }

        try {
            for (Quest quest : allQuests) {
                // Check if record exists
                String checkSql = "SELECT id FROM quest_progress WHERE save_slot_id = " + saveSlotId +
                                 " AND level_number = " + levelNumber +
                                 " AND quest_id = '" + quest.getQuestId() + "'";

                ResultSet rs = db.getStatement().executeQuery(checkSql);

                if (rs.next()) {
                    // Update existing record
                    String updateSql = "UPDATE quest_progress SET " +
                                      "current_progress = " + quest.getCurrentProgress() + ", " +
                                      "completed = " + (quest.isCompleted() ? 1 : 0) + ", " +
                                      "reward_granted = " + (quest.isRewardGranted() ? 1 : 0) + " " +
                                      "WHERE save_slot_id = " + saveSlotId +
                                      " AND level_number = " + levelNumber +
                                      " AND quest_id = '" + quest.getQuestId() + "'";
                    db.getStatement().executeUpdate(updateSql);
                } else {
                    // Insert new record
                    String insertSql = "INSERT INTO quest_progress " +
                                      "(save_slot_id, level_number, quest_id, current_progress, completed, reward_granted) " +
                                      "VALUES (" + saveSlotId + ", " + levelNumber + ", '" + quest.getQuestId() + "', " +
                                      quest.getCurrentProgress() + ", " + (quest.isCompleted() ? 1 : 0) + ", " +
                                      (quest.isRewardGranted() ? 1 : 0) + ")";
                    db.getStatement().executeUpdate(insertSql);
                }
            }

            db.closeConnection();

        } catch (Exception e) {
            System.out.println("Error saving quest progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== EVENT HANDLERS ====================

    // Call when an enemy is killed
    public void onEnemyKilled(String enemyType, String killerTowerType) {
        QuestEvent event = QuestEvent.enemyKilled(enemyType, killerTowerType);
        processEvent(event);
    }

    // Call when a tower is built
    public void onTowerBuilt(String towerType, int cost) {
        QuestEvent buildEvent = QuestEvent.towerBuilt(towerType, cost);
        processEvent(buildEvent);

        // Also fire coins spent event
        QuestEvent spentEvent = QuestEvent.coinsSpent(cost, towerType);
        processEvent(spentEvent);
    }

    // Call when a tower is upgraded
    public void onTowerUpgraded(String towerType, int newMKLevel, int cost) {
        QuestEvent upgradeEvent = QuestEvent.towerUpgraded(towerType, newMKLevel, cost);
        processEvent(upgradeEvent);

        // Also fire coins spent event
        QuestEvent spentEvent = QuestEvent.coinsSpent(cost, towerType);
        processEvent(spentEvent);
    }

    // Call when score changes
    public void onScoreChanged(int currentScore) {
        QuestEvent event = QuestEvent.scoreChanged(currentScore);
        processEvent(event);
    }

    // Process an event through all quests
    private void processEvent(QuestEvent event) {
        for (Quest quest : quests) {
            if (!quest.isCompleted()) {
                int beforeProgress = quest.getCurrentProgress();
                boolean wasCompleted = quest.isCompleted();
                quest.updateProgress(event);

                // Check if quest just completed
                if (!wasCompleted && quest.isCompleted()) {
                    handleQuestCompletion(quest);
                }

                if ((quest.getCurrentProgress() != beforeProgress || quest.isCompleted() != wasCompleted)
                        && onQuestProgressChanged != null) {
                    onQuestProgressChanged.run();
                }
            }
        }
    }

    // Handle quest completion - grant reward
    private void handleQuestCompletion(Quest quest) {
        if (quest.isRewardGranted()) {
            return; // Already granted
        }

        // Grant the reward
        Reward reward = quest.getReward();
        if (reward != null) {
            playerCurrencies.addCurrency(reward.getType(), reward.getAmount());
            quest.setRewardGranted(true);

            System.out.println("Quest completed! Reward: " + reward.getDisplayString());
        }

        // Save progress immediately on completion
        saveQuestProgress();

        // Notify UI if callback is set
        if (onQuestCompleted != null) {
            onQuestCompleted.run();
        }

        if (onQuestProgressChanged != null) {
            onQuestProgressChanged.run();
        }
    }

    // ==================== GETTERS & UTILITIES ====================

    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public ArrayList<Quest> getAllQuests() {
        return allQuests;
    }

    public Quest getQuest(int index) {
        if (index >= 0 && index < quests.size()) {
            return quests.get(index);
        }
        return null;
    }

    public int getQuestCount() {
        return quests.size();
    }

    public int getCompletedQuestCount() {
        int count = 0;
        for (Quest quest : quests) {
            if (quest.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public int getStarsEarned() {
        // Stars are no longer granted from quests. Milestone stars are handled in GameplayScreen/SaveSlot.
        return 0;
    }

    public boolean allQuestsCompleted() {
        for (Quest quest : quests) {
            if (!quest.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public void setOnQuestCompleted(Runnable callback) {
        this.onQuestCompleted = callback;
    }

    public void setOnQuestProgressChanged(Runnable onQuestProgressChanged) {
        this.onQuestProgressChanged = onQuestProgressChanged;
    }

    // Called when level ends - save final progress
    public void onLevelEnd() {
        saveQuestProgress();
    }
}
