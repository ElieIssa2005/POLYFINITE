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
    private int[] waveMilestones;
    private int milestonesCompleted;
    private boolean unlocked;

    public LevelInfo(int levelNumber, String levelName) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.starsEarned = 0;
        this.maxStars = 3;
        this.bestWave = 0;
        this.highScore = 0;
        this.quests = new ArrayList<>();
        this.enemyTypes = new ArrayList<>();
        this.waveMilestones = new int[]{5, 10, 20};
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

    public int getBestWave() {
        return bestWave;
    }

    public void setBestWave(int bestWave) {
        this.bestWave = bestWave;
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

    public int[] getWaveMilestones() {
        return waveMilestones;
    }

    public int getMilestonesCompleted() {
        return milestonesCompleted;
    }

    public void setMilestonesCompleted(int milestonesCompleted) {
        this.milestonesCompleted = milestonesCompleted;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}