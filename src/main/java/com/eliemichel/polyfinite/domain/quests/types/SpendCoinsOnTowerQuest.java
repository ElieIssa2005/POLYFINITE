package com.eliemichel.polyfinite.domain.quests.types;

import com.eliemichel.polyfinite.domain.progression.Reward;
import com.eliemichel.polyfinite.domain.quests.Quest;
import com.eliemichel.polyfinite.domain.quests.QuestEvent;
import com.eliemichel.polyfinite.domain.quests.QuestType;

/**
 * Quest: Spend X paper money on a specific tower type (building + upgrades)
 * Persistent - progress accumulates across runs
 */
public class SpendCoinsOnTowerQuest extends Quest {

    public SpendCoinsOnTowerQuest(String questId, String towerType, int targetValue, Reward reward) {
        super(questId, "Spend " + targetValue + " paper money on " + towerType + " Towers",
              QuestType.SPEND_COINS_ON_TOWER, targetValue, towerType, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.COINS_SPENT) {
            // Check if spent on matching tower type
            String spentOn = event.getSpentOnTowerType();
            if (targetSpecifier != null && targetSpecifier.equals(spentOn)) {
                addProgress(event.getCoinsSpent());
            }
        }
    }
}
