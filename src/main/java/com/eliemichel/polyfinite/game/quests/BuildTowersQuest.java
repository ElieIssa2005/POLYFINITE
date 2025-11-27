package com.eliemichel.polyfinite.game.quests;

import com.eliemichel.polyfinite.game.*;

/**
 * Quest: Build X towers of a specific type
 * Persistent - progress accumulates across runs
 */
public class BuildTowersQuest extends Quest {

    public BuildTowersQuest(String questId, String towerType, int targetValue, Reward reward) {
        super(questId, "Build " + targetValue + " " + towerType + " Towers",
              QuestType.BUILD_TOWERS, targetValue, towerType, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.TOWER_BUILT) {
            // Check if tower type matches
            if (targetSpecifier != null && targetSpecifier.equals(event.getTowerType())) {
                addProgress(1);
            }
        }
    }
}
