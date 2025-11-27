package com.eliemichel.polyfinite.domain.quests.types;

import com.eliemichel.polyfinite.domain.progression.Reward;
import com.eliemichel.polyfinite.domain.quests.Quest;
import com.eliemichel.polyfinite.domain.quests.QuestEvent;
import com.eliemichel.polyfinite.domain.quests.QuestType;

/**
 * Quest: Gain X score on this level
 * Run-based - tracks best single run score, not cumulative
 */
public class GainScoreQuest extends Quest {

    public GainScoreQuest(String questId, int targetValue, Reward reward) {
        super(questId, "Gain " + targetValue + " score",
              QuestType.GAIN_SCORE, targetValue, reward);
    }

    @Override
    public void updateProgress(QuestEvent event) {
        if (event.getEventType() == QuestEvent.EventType.SCORE_CHANGED) {
            // For score, we track the current score (not cumulative)
            // If this run's score is higher than saved progress, update it
            setProgressIfHigher(event.getCurrentScore());
        }
    }

    // Called at start of each run to reset run-based progress
    public void resetForNewRun() {
        // Don't reset if already completed
        if (!completed) {
            // For score quest, we keep the highest score achieved
            // So we don't reset currentProgress here
            // The setProgressIfHigher will handle updating only if better
        }
    }
}
