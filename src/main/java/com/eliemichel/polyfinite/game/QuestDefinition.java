package com.eliemichel.polyfinite.game;

/**
 * Data class for defining a quest in the level creator.
 * Used to serialize/deserialize quest definitions in level files.
 */
public class QuestDefinition {
    private QuestType type;
    private int targetValue;
    private String targetSpecifier;  // enemy type, tower type, etc.
    private RewardType rewardType;
    private int rewardAmount;

    public QuestDefinition() {
        // Default constructor for loading
    }

    public QuestDefinition(QuestType type, int targetValue, RewardType rewardType, int rewardAmount) {
        this.type = type;
        this.targetValue = targetValue;
        this.targetSpecifier = null;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
    }

    public QuestDefinition(QuestType type, int targetValue, String targetSpecifier, 
                          RewardType rewardType, int rewardAmount) {
        this.type = type;
        this.targetValue = targetValue;
        this.targetSpecifier = targetSpecifier;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
    }

    // Create a Quest object from this definition
    public Quest createQuest(int levelNumber, int questIndex) {
        String questId = "level_" + levelNumber + "_quest_" + questIndex;
        Reward reward = new Reward(rewardType, rewardAmount);

        switch (type) {
            case DESTROY_ENEMIES:
                return new com.eliemichel.polyfinite.game.quests.DestroyEnemiesQuest(
                    questId, targetValue, reward);

            case DESTROY_ENEMY_TYPE:
                return new com.eliemichel.polyfinite.game.quests.DestroyEnemyTypeQuest(
                    questId, targetSpecifier, targetValue, reward);

            case DESTROY_WITH_TOWER:
                return new com.eliemichel.polyfinite.game.quests.DestroyWithTowerQuest(
                    questId, targetSpecifier, targetValue, reward);

            case SPEND_COINS_TOTAL:
                return new com.eliemichel.polyfinite.game.quests.SpendCoinsTotalQuest(
                    questId, targetValue, reward);

            case SPEND_COINS_ON_TOWER:
                return new com.eliemichel.polyfinite.game.quests.SpendCoinsOnTowerQuest(
                    questId, targetSpecifier, targetValue, reward);

            case GAIN_SCORE:
                return new com.eliemichel.polyfinite.game.quests.GainScoreQuest(
                    questId, targetValue, reward);

            case BUILD_TOWERS:
                return new com.eliemichel.polyfinite.game.quests.BuildTowersQuest(
                    questId, targetSpecifier, targetValue, reward);

            case UPGRADE_ANY_TO_LEVEL:
                return new com.eliemichel.polyfinite.game.quests.UpgradeAnyToLevelQuest(
                    questId, targetValue, reward);

            case UPGRADE_TOWER_TO_LEVEL:
                return new com.eliemichel.polyfinite.game.quests.UpgradeTowerToLevelQuest(
                    questId, targetSpecifier, targetValue, reward);

            default:
                return null;
        }
    }

    // Serialize to string for level file
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.name());
        sb.append(":");
        sb.append(targetValue);
        sb.append(":");
        sb.append(targetSpecifier != null ? targetSpecifier : "NONE");
        sb.append(":");
        sb.append(rewardType.name());
        sb.append(":");
        sb.append(rewardAmount);
        return sb.toString();
    }

    // Deserialize from string
    public static QuestDefinition fromFileString(String line) {
        String[] parts = line.split(":");
        if (parts.length != 5) {
            System.out.println("Invalid quest definition: " + line);
            return null;
        }

        try {
            QuestType type = QuestType.valueOf(parts[0]);
            int targetValue = Integer.parseInt(parts[1]);
            String specifier = parts[2].equals("NONE") ? null : parts[2];
            RewardType rewardType = RewardType.valueOf(parts[3]);
            int rewardAmount = Integer.parseInt(parts[4]);

            return new QuestDefinition(type, targetValue, specifier, rewardType, rewardAmount);
        } catch (Exception e) {
            System.out.println("Error parsing quest definition: " + e.getMessage());
            return null;
        }
    }

    // Generate description for display
    public String getDescription() {
        switch (type) {
            case DESTROY_ENEMIES:
                return "Destroy " + targetValue + " enemies";
            case DESTROY_ENEMY_TYPE:
                return "Destroy " + targetValue + " " + targetSpecifier + " enemies";
            case DESTROY_WITH_TOWER:
                return "Destroy " + targetValue + " enemies using " + targetSpecifier + " Tower";
            case SPEND_COINS_TOTAL:
                return "Spend " + targetValue + " paper money on towers";
            case SPEND_COINS_ON_TOWER:
                return "Spend " + targetValue + " paper money on " + targetSpecifier + " Towers";
            case GAIN_SCORE:
                return "Gain " + targetValue + " score";
            case BUILD_TOWERS:
                return "Build " + targetValue + " " + targetSpecifier + " Towers";
            case UPGRADE_ANY_TO_LEVEL:
                return "Upgrade any tower to MK." + targetValue;
            case UPGRADE_TOWER_TO_LEVEL:
                return "Upgrade " + targetSpecifier + " Tower to MK." + targetValue;
            default:
                return "Unknown quest";
        }
    }

    // Getters and setters
    public QuestType getType() { return type; }
    public void setType(QuestType type) { this.type = type; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public String getTargetSpecifier() { return targetSpecifier; }
    public void setTargetSpecifier(String targetSpecifier) { this.targetSpecifier = targetSpecifier; }

    public RewardType getRewardType() { return rewardType; }
    public void setRewardType(RewardType rewardType) { this.rewardType = rewardType; }

    public int getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(int rewardAmount) { this.rewardAmount = rewardAmount; }
}
