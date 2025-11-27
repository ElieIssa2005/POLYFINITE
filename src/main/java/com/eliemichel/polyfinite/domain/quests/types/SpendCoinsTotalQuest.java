package com.eliemichel.polyfinite.domain.quests.types;

import com.eliemichel.polyfinite.domain.progression.Reward;
import com.eliemichel.polyfinite.domain.quests.Quest;
import com.eliemichel.polyfinite.domain.quests.QuestEvent;
import com.eliemichel.polyfinite.domain.quests.QuestType;

/**
 * Quest: Spend X paper money total on towers (building + upgrades)
 * Persistent - progress accumulates across runs
 */
public class SpendCoinsTotalQuest extends Quest {

    public SpendCoinsTotalQuest(String questId, int targetValue, Reward reward) {
        super(questId, "Spend " + targetValue + " paper money on towers",
              QuestType.SPEND_COINS_TOTAL, targetValue, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.COINS_SPENT) {
            addProgress(event.getCoinsSpent());
        }
    }
}
