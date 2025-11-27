package com.eliemichel.polyfinite.domain.quests;

/**
 * Carries information about game events for quest progress updates.
 * Different event types use different fields.
 */
public class QuestEvent {

    public enum EventType {
        ENEMY_KILLED,
        TOWER_BUILT,
        TOWER_UPGRADED,
        COINS_SPENT,
        SCORE_CHANGED
    }

    private EventType eventType;

    // For ENEMY_KILLED
    private String enemyType;       // "Regular", "Fast", "Strong"
    private String killerTowerType; // "Basic", "Sniper", "Cannon", "Freezing"

    // For TOWER_BUILT
    private String towerType;       // "Basic", "Sniper", "Cannon", "Freezing"
    private int buildCost;

    // For TOWER_UPGRADED
    private String upgradedTowerType;
    private int newMKLevel;
    private int upgradeCost;

    // For COINS_SPENT
    private int coinsSpent;
    private String spentOnTowerType; // null if general spending

    // For SCORE_CHANGED
    private int currentScore;

    // Private constructor - use static factory methods
    private QuestEvent(EventType type) {
        this.eventType = type;
    }

    // Factory method for enemy killed
    public static QuestEvent enemyKilled(String enemyType, String killerTowerType) {
        QuestEvent event = new QuestEvent(EventType.ENEMY_KILLED);
        event.enemyType = enemyType;
        event.killerTowerType = killerTowerType;
        return event;
    }

    // Factory method for tower built
    public static QuestEvent towerBuilt(String towerType, int cost) {
        QuestEvent event = new QuestEvent(EventType.TOWER_BUILT);
        event.towerType = towerType;
        event.buildCost = cost;
        return event;
    }

    // Factory method for tower upgraded
    public static QuestEvent towerUpgraded(String towerType, int newLevel, int cost) {
        QuestEvent event = new QuestEvent(EventType.TOWER_UPGRADED);
        event.upgradedTowerType = towerType;
        event.newMKLevel = newLevel;
        event.upgradeCost = cost;
        return event;
    }

    // Factory method for coins spent
    public static QuestEvent coinsSpent(int amount, String onTowerType) {
        QuestEvent event = new QuestEvent(EventType.COINS_SPENT);
        event.coinsSpent = amount;
        event.spentOnTowerType = onTowerType;
        return event;
    }

    // Factory method for score changed
    public static QuestEvent scoreChanged(int currentScore) {
        QuestEvent event = new QuestEvent(EventType.SCORE_CHANGED);
        event.currentScore = currentScore;
        return event;
    }

    // Getters
    public EventType getEventType() {
        return eventType;
    }

    public String getEnemyType() {
        return enemyType;
    }

    public String getKillerTowerType() {
        return killerTowerType;
    }

    public String getTowerType() {
        return towerType;
    }

    public int getBuildCost() {
        return buildCost;
    }

    public String getUpgradedTowerType() {
        return upgradedTowerType;
    }

    public int getNewMKLevel() {
        return newMKLevel;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public int getCoinsSpent() {
        return coinsSpent;
    }

    public String getSpentOnTowerType() {
        return spentOnTowerType;
    }

    public int getCurrentScore() {
        return currentScore;
    }
}
