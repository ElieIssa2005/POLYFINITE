package com.eliemichel.polyfinite.game.quests;

import com.eliemichel.polyfinite.game.*;

/**
 * Quest: Destroy X enemies of a specific type (e.g., Regular, Fast, Strong)
 * Persistent - progress accumulates across runs
 */
public class DestroyEnemyTypeQuest extends Quest {

    public DestroyEnemyTypeQuest(String questId, String enemyType, int targetValue, Reward reward) {
        super(questId, "Destroy " + targetValue + " " + enemyType + " enemies",
              QuestType.DESTROY_ENEMY_TYPE, targetValue, enemyType, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.ENEMY_KILLED) {
            // Check if enemy type matches
            if (targetSpecifier != null && targetSpecifier.equals(event.getEnemyType())) {
                addProgress(1);
            }
        }
    }
}
