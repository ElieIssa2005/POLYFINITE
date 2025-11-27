package com.eliemichel.polyfinite.game;

public abstract class Quest {
    protected String questId;           // Unique ID for this quest (e.g., "level_1_quest_1")
    protected String description;
    protected QuestType type;
    protected int targetValue;
    protected int currentProgress;
    protected boolean completed;
    protected boolean rewardGranted;
    protected Reward reward;

    // For quests that need a specific target (enemy type, tower type, etc.)
    protected String targetSpecifier;   // e.g., "Regular", "Basic", "Sniper"

    public Quest(String questId, String description, QuestType type, int targetValue, Reward reward) {
        this.questId = questId;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.currentProgress = 0;
        this.completed = false;
        this.rewardGranted = false;
        this.reward = reward;
        this.targetSpecifier = null;
    }

    public Quest(String questId, String description, QuestType type, int targetValue, 
                 String targetSpecifier, Reward reward) {
        this(questId, description, type, targetValue, reward);
        this.targetSpecifier = targetSpecifier;
    }

    // Abstract method - each quest type implements its own progress update logic
    public abstract void updateProgress(QuestEvent event);

    // Check if quest is complete (progress >= target)
    protected void checkCompletion() {
        if (!completed && currentProgress >= targetValue) {
            completed = true;
            System.out.println("Quest completed: " + description);
        }
    }

    // Add progress (never decrease)
    protected void addProgress(int amount) {
        if (amount > 0 && !completed) {
            currentProgress += amount;
            checkCompletion();
        }
    }

    // For run-based quests, set progress if new value is higher
    protected void setProgressIfHigher(int newProgress) {
        if (newProgress > currentProgress && !completed) {
            currentProgress = newProgress;
            checkCompletion();
        }
    }

    // Check if this quest type is persistent across runs
    public boolean isPersistent() {
        return type.isPersistent();
    }

    // Getters
    public String getQuestId() {
        return questId;
    }

    public String getDescription() {
        return description;
    }

    public QuestType getType() {
        return type;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isRewardGranted() {
        return rewardGranted;
    }

    public Reward getReward() {
        return reward;
    }

    public String getTargetSpecifier() {
        return targetSpecifier;
    }

    // Setters for loading from database
    public void setCurrentProgress(int progress) {
        this.currentProgress = progress;
        checkCompletion();
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setRewardGranted(boolean granted) {
        this.rewardGranted = granted;
    }

    // Get progress as percentage (0.0 to 1.0)
    public double getProgressPercent() {
        if (targetValue <= 0) return 1.0;
        return Math.min(1.0, (double) currentProgress / targetValue);
    }

    // Get display string like "15 / 100"
    public String getProgressString() {
        return currentProgress + " / " + targetValue;
    }
}
