package com.eliemichel.polyfinite.game;

import java.util.ArrayList;

public class LevelInfo {
    private int levelNumber;
    private String levelName;
    private int starsEarned;
    private int maxStars;
    private int bestWave;
    private int highScore;
    private ArrayList<Quest> quests;
    private ArrayList<String> enemyTypes;
    private ArrayList<WaveMilestone> waveMilestones;
    private int milestonesCompleted;
    private boolean unlocked;

    public LevelInfo(int levelNumber, String levelName) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.starsEarned = 0;
        this.bestWave = 0;
        this.highScore = 0;
        this.quests = new ArrayList<>();
        this.enemyTypes = new ArrayList<>();
        this.waveMilestones = new ArrayList<>();
        this.maxStars = calculateMaxStars();
        this.milestonesCompleted = 0;
        this.unlocked = false;
    }

    public void addQuest(Quest quest) {
        quests.add(quest);
    }

    public void addEnemyType(String enemyType) {
        enemyTypes.add(enemyType);
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getStarsEarned() {
        return starsEarned;
    }

    public void setStarsEarned(int starsEarned) {
        this.starsEarned = starsEarned;
    }

    public int getMaxStars() {
        return maxStars;
    }

    public void setMaxStars(int maxStars) {
        this.maxStars = maxStars;
    }

    public int getBestWave() {
        return bestWave;
    }

    public void setBestWave(int bestWave) {
        this.bestWave = bestWave;
        refreshMilestoneCompletion();
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public ArrayList<String> getEnemyTypes() {
        return enemyTypes;
    }

    public ArrayList<WaveMilestone> getWaveMilestones() { return waveMilestones; }

    public void setWaveMilestones(ArrayList<WaveMilestone> waveMilestones) {
        this.waveMilestones = waveMilestones == null ? new ArrayList<>() : new ArrayList<>(waveMilestones);
        this.maxStars = calculateMaxStars();
        refreshMilestoneCompletion();
    }

    public void updateProgress(int bestWave, int highScore, int starsEarned) {
        this.bestWave = Math.max(this.bestWave, bestWave);
        this.highScore = Math.max(this.highScore, highScore);
        int earnedFromWave = 0;
        if (waveMilestones != null) {
            for (WaveMilestone milestone : waveMilestones) {
                if (milestone.isReached(this.bestWave)) {
                    earnedFromWave += milestone.getStarsReward();
                }
            }
        }

        this.starsEarned = Math.min(Math.max(this.starsEarned, Math.max(starsEarned, earnedFromWave)), calculateMaxStars());
        refreshMilestoneCompletion();
    }

    public int getMilestonesCompleted() { return milestonesCompleted; }

    public void setMilestonesCompleted(int milestonesCompleted) { this.milestonesCompleted = milestonesCompleted; }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public void refreshMilestoneCompletion() {
        if (waveMilestones == null || waveMilestones.isEmpty()) {
            milestonesCompleted = 0;
            maxStars = 0;
            return;
        }

        int completed = 0;
        for (WaveMilestone milestone : waveMilestones) {
            if (milestone.isReached(bestWave)) {
                completed++;
            }
        }
        milestonesCompleted = completed;
        maxStars = calculateMaxStars();
    }

    private int calculateMaxStars() {
        int total = 0;
        if (waveMilestones != null) {
            for (WaveMilestone milestone : waveMilestones) {
                total += milestone.getStarsReward();
            }
        }
        return total;
    }
}