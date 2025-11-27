package com.eliemichel.polyfinite.game;

public enum QuestType {
    // Enemy-based (Persistent - cumulative across runs)
    DESTROY_ENEMIES,           // Destroy X enemies
    DESTROY_ENEMY_TYPE,        // Destroy X enemies of specific type (e.g., Regular, Fast, Strong)
    DESTROY_WITH_TOWER,        // Destroy X enemies using specific tower type

    // Spending (Persistent - cumulative across runs)
    SPEND_COINS_TOTAL,         // Spend X paper money total on towers
    SPEND_COINS_ON_TOWER,      // Spend X paper money on specific tower type (build + upgrades)

    // Score (Run-based - best single run)
    GAIN_SCORE,                // Gain X score on this level

    // Construction (Persistent - cumulative across runs)
    BUILD_TOWERS,              // Build X towers of specific type

    // Upgrading (Run-based - achieved in single run)
    UPGRADE_ANY_TO_LEVEL,      // Upgrade any tower to MK level X
    UPGRADE_TOWER_TO_LEVEL;    // Upgrade specific tower type to MK level X

    // Check if this quest type accumulates progress across runs
    public boolean isPersistent() {
        switch (this) {
            case DESTROY_ENEMIES:
            case DESTROY_ENEMY_TYPE:
            case DESTROY_WITH_TOWER:
            case SPEND_COINS_TOTAL:
            case SPEND_COINS_ON_TOWER:
            case BUILD_TOWERS:
                return true;
            case GAIN_SCORE:
            case UPGRADE_ANY_TO_LEVEL:
            case UPGRADE_TOWER_TO_LEVEL:
                return false;
            default:
                return false;
        }
    }
}
