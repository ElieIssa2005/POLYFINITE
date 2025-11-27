package com.eliemichel.polyfinite.domain.quests.types;

import com.eliemichel.polyfinite.domain.progression.Reward;
import com.eliemichel.polyfinite.domain.quests.Quest;
import com.eliemichel.polyfinite.domain.quests.QuestEvent;
import com.eliemichel.polyfinite.domain.quests.QuestType;

/**
 * Quest: Destroy X enemies using a specific tower type
 * Persistent - progress accumulates across runs
 */
public class DestroyWithTowerQuest extends Quest {

    public DestroyWithTowerQuest(String questId, String towerType, int targetValue, Reward reward) {
        super(questId, "Destroy " + targetValue + " enemies using " + towerType + " Tower",
              QuestType.DESTROY_WITH_TOWER, targetValue, towerType, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.ENEMY_KILLED) {
            // Check if killer tower type matches
            if (targetSpecifier != null && targetSpecifier.equals(event.getKillerTowerType())) {
                addProgress(1);
            }
        }
    }
}
