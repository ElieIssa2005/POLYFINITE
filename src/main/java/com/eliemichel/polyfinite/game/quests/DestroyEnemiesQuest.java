package com.eliemichel.polyfinite.game.quests;

import com.eliemichel.polyfinite.game.*;

/**
 * Quest: Destroy X enemies (any type)
 * Persistent - progress accumulates across runs
 */
public class DestroyEnemiesQuest extends Quest {

    public DestroyEnemiesQuest(String questId, int targetValue, Reward reward) {
        super(questId, "Destroy " + targetValue + " enemies", 
              QuestType.DESTROY_ENEMIES, targetValue, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.ENEMY_KILLED) {
            addProgress(1);
        }
    }
}
