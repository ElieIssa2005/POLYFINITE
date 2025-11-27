package com.eliemichel.polyfinite.game.quests;

import com.eliemichel.polyfinite.game.*;

/**
 * Quest: Upgrade any tower to MK level X
 * Run-based - achieved in a single run
 */
public class UpgradeAnyToLevelQuest extends Quest {

    public UpgradeAnyToLevelQuest(String questId, int targetLevel, Reward reward) {
        super(questId, "Upgrade any tower to MK." + targetLevel,
              QuestType.UPGRADE_ANY_TO_LEVEL, targetLevel, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.TOWER_UPGRADED) {
            // Check if the new level meets or exceeds target
            int newLevel = event.getNewMKLevel();
            setProgressIfHigher(newLevel);
        }
    }
}
