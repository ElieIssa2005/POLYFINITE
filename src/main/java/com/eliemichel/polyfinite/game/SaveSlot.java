package com.eliemichel.polyfinite.game;

import com.eliemichel.polyfinite.database.DBConnectMySQL;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.eliemichel.polyfinite.game.LevelData;

public class SaveSlot {

    private int slotNumber;
    private boolean isEmpty;
    private int totalStars;
    private int currentLevel;
    private int gold;
    private String playerName;

    public SaveSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.isEmpty = true;
        this.totalStars = 0;
        this.currentLevel = 1;
        this.playerName = "Player " + slotNumber;
        this.gold = 20;
    }

    public void loadFromDatabase() {
        DBConnectMySQL connector = new DBConnectMySQL();

        if (!connector.isConnected()) {
            System.out.println("Cannot load save slot - database not connected");
            return;
        }

        try {
            String sql = "SELECT * FROM save_slots WHERE slot_number = " + slotNumber;
            ResultSet rs = connector.getStatement().executeQuery(sql);

            if (rs.next()) {
                this.playerName = rs.getString("player_name");
                this.totalStars = rs.getInt("total_stars");
                this.currentLevel = rs.getInt("current_level");
                this.gold = rs.getInt("gold");

                if (this.totalStars > 0 || this.currentLevel > 1) {
                    this.isEmpty = false;
                } else {
                    this.isEmpty = true;
                }

                System.out.println("Loaded slot " + slotNumber + ": " + totalStars + " stars, level " + currentLevel);
            }

            connector.closeConnection();

        } catch (Exception e) {
            System.out.println("Error loading save slot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startNewGame() {
        this.isEmpty = false;
        this.totalStars = 0;
        this.currentLevel = 1;
        this.gold = 20;

        DBConnectMySQL connector = new DBConnectMySQL();

        if (!connector.isConnected()) {
            return;
        }

        try {
            String sql = "UPDATE save_slots SET total_stars = 0, current_level = 1, gold = 500 WHERE slot_number = " + slotNumber;
            connector.getStatement().executeUpdate(sql);
            connector.closeConnection();
            System.out.println("Started new game in slot " + slotNumber);
        } catch (Exception e) {
            System.out.println("Error starting new game: " + e.getMessage());
        }
    }

    public void saveLevelProgress(int levelNumber, int wave, int score, boolean q1, boolean q2, boolean q3) {
        saveLevelProgress(levelNumber, wave, score, 0, q1, q2, q3, null);
    }

    public void saveLevelProgress(int levelNumber, int wave, int score, int milestoneStarsEarned,
                                  ArrayList<Quest> activeQuests, ArrayList<WaveMilestone> waveMilestones) {
        boolean q1 = activeQuests != null && activeQuests.size() > 0 && activeQuests.get(0).isCompleted();
        boolean q2 = activeQuests != null && activeQuests.size() > 1 && activeQuests.get(1).isCompleted();
        boolean q3 = activeQuests != null && activeQuests.size() > 2 && activeQuests.get(2).isCompleted();

        saveLevelProgress(levelNumber, wave, score, milestoneStarsEarned, q1, q2, q3, waveMilestones);
    }

    private void saveLevelProgress(int levelNumber, int wave, int score, int milestoneStarsEarned,
                                   boolean q1, boolean q2, boolean q3, ArrayList<WaveMilestone> waveMilestones) {
        DBConnectMySQL connector = new DBConnectMySQL();

        if (!connector.isConnected()) {
            System.out.println("Cannot save progress - database not connected");
            return;
        }

        try {
            int milestoneStars = calculateStarsForWave(wave, waveMilestones);
            int starsEarned = Math.max(milestoneStarsEarned, milestoneStars);

            String upsertSql = "INSERT INTO level_progress " +
                    "(save_slot_id, level_number, best_wave, best_score, quest_1_completed, quest_2_completed, quest_3_completed, stars_earned, times_played) " +
                    "VALUES (" + slotNumber + ", " + levelNumber + ", " + wave + ", " + score + ", " +
                    (q1 ? 1 : 0) + ", " + (q2 ? 1 : 0) + ", " + (q3 ? 1 : 0) + ", " + starsEarned + ", 1) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "best_wave = GREATEST(best_wave, VALUES(best_wave)), " +
                    "best_score = GREATEST(best_score, VALUES(best_score)), " +
                    "quest_1_completed = quest_1_completed OR VALUES(quest_1_completed), " +
                    "quest_2_completed = quest_2_completed OR VALUES(quest_2_completed), " +
                    "quest_3_completed = quest_3_completed OR VALUES(quest_3_completed), " +
                    "stars_earned = GREATEST(stars_earned, VALUES(stars_earned)), " +
                    "times_played = times_played + 1";

            connector.getStatement().executeUpdate(upsertSql);
            System.out.println("Saved level progress: " + starsEarned + " stars");

            String totalStarsSql = "SELECT SUM(stars_earned) as total FROM level_progress WHERE save_slot_id = " + slotNumber;
            ResultSet totalRs = connector.getStatement().executeQuery(totalStarsSql);

            if (totalRs.next()) {
                int newTotalStars = totalRs.getInt("total");

                String updateSlotSql = "UPDATE save_slots SET total_stars = " + newTotalStars + " WHERE slot_number = " + slotNumber;
                connector.getStatement().executeUpdate(updateSlotSql);

                this.totalStars = newTotalStars;
                this.isEmpty = false;

                System.out.println("Total stars: " + newTotalStars);
            }

            connector.closeConnection();

        } catch (Exception e) {
            System.out.println("Error saving level progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(int totalStars) {
        this.totalStars = totalStars;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGold() { return gold; }

    private int calculateStarsForWave(int bestWave, ArrayList<WaveMilestone> waveMilestones) {
        if (waveMilestones == null) {
            waveMilestones = new ArrayList<>();
        }

        int stars = 0;
        for (WaveMilestone milestone : waveMilestones) {
            if (milestone.isReached(bestWave)) {
                stars += milestone.getStarsReward();
            }
        }
        return stars;
    }
}