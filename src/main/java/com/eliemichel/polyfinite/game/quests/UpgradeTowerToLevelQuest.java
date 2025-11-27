package com.eliemichel.polyfinite.game.quests;

import com.eliemichel.polyfinite.game.*;

/**
 * Quest: Upgrade specific tower type to MK level X
 * Run-based - achieved in a single run
 */
public class UpgradeTowerToLevelQuest extends Quest {

    public UpgradeTowerToLevelQuest(String questId, String towerType, int targetLevel, Reward reward) {
        super(questId, "Upgrade " + towerType + " Tower to MK." + targetLevel,
              QuestType.UPGRADE_TOWER_TO_LEVEL, targetLevel, towerType, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.TOWER_UPGRADED) {
            // Check if tower type matches
            if (targetSpecifier != null && targetSpecifier.equals(event.getUpgradedTowerType())) {
                int newLevel = event.getNewMKLevel();
                setProgressIfHigher(newLevel);
            }
        }
    }
}
