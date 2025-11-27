package com.eliemichel.polyfinite.game;

import com.eliemichel.polyfinite.game.tiles.*;
import java.util.ArrayList;

public class LevelData {
    private String levelName;
    private int levelNumber;
    private int gridWidth;
    private int gridHeight;
    private Tile[][] grid;
    private ArrayList<WaveData> waves;
    private double mapNodeX;
    private double mapNodeY;

    // Wave milestone star rewards
    private ArrayList<WaveMilestone> waveMilestones;

    // Wave system settings
    private ArrayList<EnemyWeight> enemyWeights;
    private SpawnDensity spawnDensity;
    private double customSpawnInterval; // used when density is CUSTOM
    private double difficultyModifier; // 1.0 = 100%, 1.15 = 115% (harder)
    private int baseEnemyCount; // starting enemies per wave
    private int maxEnemyCount; // cap on enemies per wave
    private double interWaveTime; // seconds between waves

    // Quest system
    private ArrayList<QuestDefinition> questDefinitions;
    private double goldDropChance; // chance for enemies to drop meta-currency gold (0.0 to 1.0)

    public LevelData() {
        this.waves = new ArrayList<>();
        this.gridWidth = 25;
        this.gridHeight = 15;
        this.grid = new Tile[gridHeight][gridWidth];

        // Initialize with empty tiles
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                grid[row][col] = new EmptyTile(row, col);
            }
        }

        // Default wave settings
        this.enemyWeights = new ArrayList<>();
        this.enemyWeights.add(new EnemyWeight("Regular", 80));
        this.enemyWeights.add(new EnemyWeight("Fast", 20));
        this.spawnDensity = SpawnDensity.MEDIUM;
        this.customSpawnInterval = 0.5;
        this.difficultyModifier = 1.0;
        this.baseEnemyCount = 10;
        this.maxEnemyCount = 100;
        this.interWaveTime = 10.0;

        // Default quest settings
        this.questDefinitions = new ArrayList<>();
        this.goldDropChance = 0.05; // 5% default

        // Default milestone settings
        this.waveMilestones = new ArrayList<>();
    }

    public LevelData(int gridWidth, int gridHeight) {
        this.waves = new ArrayList<>();
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.grid = new Tile[gridHeight][gridWidth];

        // Initialize with empty tiles
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                grid[row][col] = new EmptyTile(row, col);
            }
        }

        // Default wave settings
        this.enemyWeights = new ArrayList<>();
        this.enemyWeights.add(new EnemyWeight("Regular", 80));
        this.enemyWeights.add(new EnemyWeight("Fast", 20));
        this.spawnDensity = SpawnDensity.MEDIUM;
        this.customSpawnInterval = 0.5;
        this.difficultyModifier = 1.0;
        this.baseEnemyCount = 10;
        this.maxEnemyCount = 100;
        this.interWaveTime = 10.0;

        // Default quest settings
        this.questDefinitions = new ArrayList<>();
        this.goldDropChance = 0.05; // 5% default

        // Default milestone settings
        this.waveMilestones = new ArrayList<>();
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public void setTile(int row, int col, Tile tile) {
        if (row >= 0 && row < gridHeight && col >= 0 && col < gridWidth) {
            grid[row][col] = tile;
        }
    }

    public Tile getTile(int row, int col) {
        if (row >= 0 && row < gridHeight && col >= 0 && col < gridWidth) {
            return grid[row][col];
        }
        return null;
    }

    public ArrayList<WaveData> getWaves() {
        return waves;
    }

    public double getMapNodeX() {
        return mapNodeX;
    }

    public void setMapNodeX(double mapNodeX) {
        this.mapNodeX = mapNodeX;
    }

    public double getMapNodeY() {
        return mapNodeY;
    }

    public void setMapNodeY(double mapNodeY) {
        this.mapNodeY = mapNodeY;
    }

    // Wave system getters and setters
    public ArrayList<EnemyWeight> getEnemyWeights() {
        return enemyWeights;
    }

    public void setEnemyWeights(ArrayList<EnemyWeight> enemyWeights) {
        this.enemyWeights = enemyWeights;
    }

    public SpawnDensity getSpawnDensity() {
        return spawnDensity;
    }

    public void setSpawnDensity(SpawnDensity spawnDensity) {
        this.spawnDensity = spawnDensity;
    }

    public double getCustomSpawnInterval() {
        return customSpawnInterval;
    }

    public void setCustomSpawnInterval(double customSpawnInterval) {
        this.customSpawnInterval = customSpawnInterval;
    }

    public double getDifficultyModifier() {
        return difficultyModifier;
    }

    public void setDifficultyModifier(double difficultyModifier) {
        this.difficultyModifier = difficultyModifier;
    }

    public int getBaseEnemyCount() {
        return baseEnemyCount;
    }

    public void setBaseEnemyCount(int baseEnemyCount) {
        this.baseEnemyCount = baseEnemyCount;
    }

    public int getMaxEnemyCount() {
        return maxEnemyCount;
    }

    public void setMaxEnemyCount(int maxEnemyCount) {
        this.maxEnemyCount = maxEnemyCount;
    }

    public double getInterWaveTime() {
        return interWaveTime;
    }

    public void setInterWaveTime(double interWaveTime) {
        this.interWaveTime = interWaveTime;
    }

    // Helper to get spawn interval based on density
    public double getSpawnInterval() {
        if (spawnDensity == SpawnDensity.CUSTOM) {
            return customSpawnInterval;
        }
        return spawnDensity.getInterval();
    }

    // Quest system getters and setters
    public ArrayList<QuestDefinition> getQuestDefinitions() {
        return questDefinitions;
    }

    public void setQuestDefinitions(ArrayList<QuestDefinition> questDefinitions) {
        this.questDefinitions = questDefinitions;
    }

    public void addQuestDefinition(QuestDefinition quest) {
        this.questDefinitions.add(quest);
    }

    public double getGoldDropChance() {
        return goldDropChance;
    }

    public void setGoldDropChance(double goldDropChance) {
        this.goldDropChance = goldDropChance;
    }

    // Milestones
    public ArrayList<WaveMilestone> getWaveMilestones() {
        return waveMilestones;
    }

    public void setWaveMilestones(ArrayList<WaveMilestone> waveMilestones) {
        if (waveMilestones == null) {
            this.waveMilestones = new ArrayList<>();
            return;
        }
        this.waveMilestones = new ArrayList<>(waveMilestones);
    }

    public void addWaveMilestone(WaveMilestone milestone) {
        if (this.waveMilestones == null) {
            this.waveMilestones = new ArrayList<>();
        }
        this.waveMilestones.add(milestone);
    }

    public static ArrayList<WaveMilestone> createDefaultMilestones() {
        ArrayList<WaveMilestone> defaults = new ArrayList<>();
        defaults.add(new WaveMilestone(10, 1));
        defaults.add(new WaveMilestone(20, 1));
        defaults.add(new WaveMilestone(30, 1));
        return defaults;
    }

    public int getMaxStars() {
        int total = 0;
        if (waveMilestones != null) {
            for (WaveMilestone milestone : waveMilestones) {
                total += milestone.getStarsReward();
            }
        }
        return total;
    }
}
