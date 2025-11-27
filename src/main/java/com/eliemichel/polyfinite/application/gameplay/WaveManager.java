package com.eliemichel.polyfinite.application.gameplay;

import com.eliemichel.polyfinite.domain.enemies.Enemy;
import com.eliemichel.polyfinite.domain.enemies.EnemyWeight;
import com.eliemichel.polyfinite.domain.level.LevelData;
import com.eliemichel.polyfinite.domain.level.SpawnDensity;
import com.eliemichel.polyfinite.domain.enemies.types.Fast;
import com.eliemichel.polyfinite.domain.enemies.types.Regular;
import com.eliemichel.polyfinite.domain.enemies.types.Strong;

import java.util.ArrayList;
import java.util.Random;

public class WaveManager {

    public enum WaveState {
        IDLE,       // Waiting for player to start first wave
        SPAWNING,   // Currently spawning enemies
        WAITING,    // Inter-wave countdown
        PLAYING     // Wave spawned, waiting for enemies to be killed
    }

    private LevelData levelData;
    private ArrayList<Enemy> enemies;
    private int[] spawnTile;
    private ArrayList<int[]> enemyPath;
    private int tileSize;

    private WaveState state;
    private int currentWave;
    private int enemiesSpawnedThisWave;
    private int enemiesToSpawnThisWave;
    private double spawnTimer;
    private double interWaveTimer;
    private double interWaveTimeMax;

    private Random random;

    // Callback for bonus gold when skipping timer
    private Runnable onSkipBonus;

    public WaveManager(LevelData levelData, ArrayList<Enemy> enemies, int[] spawnTile, 
                       ArrayList<int[]> enemyPath, int tileSize) {
        this.levelData = levelData;
        this.enemies = enemies;
        this.spawnTile = spawnTile;
        this.enemyPath = enemyPath;
        this.tileSize = tileSize;

        this.state = WaveState.IDLE;
        this.currentWave = 0;
        this.enemiesSpawnedThisWave = 0;
        this.enemiesToSpawnThisWave = 0;
        this.spawnTimer = 0;
        this.interWaveTimer = 0;
        this.interWaveTimeMax = levelData.getInterWaveTime();

        this.random = new Random();
    }

    public void setOnSkipBonus(Runnable callback) {
        this.onSkipBonus = callback;
    }

    public void update(double deltaTime) {
        switch (state) {
            case IDLE:
                // Do nothing, wait for player to press start
                break;

            case SPAWNING:
                updateSpawning(deltaTime);
                break;

            case WAITING:
                updateWaiting(deltaTime);
                break;

            case PLAYING:
                // Wave is done spawning, check if all enemies are dead
                if (enemies.isEmpty()) {
                    startInterWaveTimer();
                }
                break;
        }
    }

    private void updateSpawning(double deltaTime) {
        spawnTimer += deltaTime;

        double spawnInterval = levelData.getSpawnInterval();

        while (spawnTimer >= spawnInterval && enemiesSpawnedThisWave < enemiesToSpawnThisWave) {
            spawnEnemy();
            enemiesSpawnedThisWave++;
            spawnTimer -= spawnInterval;
        }

        // Check if done spawning
        if (enemiesSpawnedThisWave >= enemiesToSpawnThisWave) {
            state = WaveState.PLAYING;
            System.out.println("Wave " + currentWave + " finished spawning. Waiting for enemies to die.");
        }
    }

    private void updateWaiting(double deltaTime) {
        interWaveTimer -= deltaTime;

        if (interWaveTimer <= 0) {
            startNextWave();
        }
    }

    private void startInterWaveTimer() {
        state = WaveState.WAITING;
        interWaveTimer = interWaveTimeMax;
        System.out.println("Inter-wave timer started: " + interWaveTimeMax + " seconds");
    }

    public void startNextWave() {
        currentWave++;
        enemiesSpawnedThisWave = 0;
        spawnTimer = 0;

        // Calculate enemies for this wave using growth formula
        enemiesToSpawnThisWave = calculateEnemyCount(currentWave);

        state = WaveState.SPAWNING;
        System.out.println("Starting wave " + currentWave + " with " + enemiesToSpawnThisWave + " enemies");
    }

    // Called when player clicks start button
    public void playerStartWave() {
        if (state == WaveState.IDLE) {
            startNextWave();
        } else if (state == WaveState.WAITING) {
            // Player skipped the timer - give bonus
            int bonusGold = calculateSkipBonus();
            System.out.println("Player skipped timer! Bonus gold: " + bonusGold);
            if (onSkipBonus != null) {
                onSkipBonus.run();
            }
            startNextWave();
        }
    }

    private int calculateSkipBonus() {
        // Bonus based on time remaining (more time = more bonus)
        int bonus = (int)(interWaveTimer * 2);
        return Math.max(5, bonus); // minimum 5 gold
    }

    public int getSkipBonusAmount() {
        return calculateSkipBonus();
    }

    // Growth formula: starts at base, approaches max over time
    private int calculateEnemyCount(int wave) {
        int base = levelData.getBaseEnemyCount();
        int max = levelData.getMaxEnemyCount();

        // Smooth curve: at wave 20, you're about 50% of the way to max
        double progress = (double) wave / (wave + 20.0);
        int count = base + (int)((max - base) * progress);

        return Math.min(count, max);
    }

    // Calculate scaled health for enemies based on wave and difficulty
    private int calculateEnemyHealth(int baseHealth) {
        double modifier = levelData.getDifficultyModifier();
        // Health = baseHealth * (modifier ^ waveNumber)
        double scaled = baseHealth * Math.pow(modifier, currentWave);
        return (int) Math.ceil(scaled);
    }

    private void spawnEnemy() {
        if (spawnTile == null) {
            System.out.println("Cannot spawn enemy - no spawn tile!");
            return;
        }

        // Pick enemy type based on weights
        String enemyType = pickWeightedEnemyType();

        Enemy enemy = createEnemy(enemyType);
        if (enemy != null) {
            // Scale health based on wave and difficulty
            int scaledHealth = calculateEnemyHealth(enemy.getMaxHealth());
            enemy.initialize(scaledHealth);

            enemy.setPath(enemyPath);
            enemies.add(enemy);
        }
    }

    private String pickWeightedEnemyType() {
        ArrayList<EnemyWeight> weights = levelData.getEnemyWeights();

        if (weights == null || weights.isEmpty()) {
            return "Regular";
        }

        // Roll a random number 1-100
        int roll = random.nextInt(100) + 1;

        // Find which enemy type the roll falls into
        int cumulative = 0;
        for (int i = 0; i < weights.size(); i++) {
            EnemyWeight ew = weights.get(i);
            cumulative += ew.getWeight();
            if (roll <= cumulative) {
                return ew.getEnemyType();
            }
        }

        // Fallback
        return weights.get(0).getEnemyType();
    }

    private Enemy createEnemy(String enemyType) {
        Enemy enemy = null;

        if (enemyType.equals("Fast")) {
            enemy = new Fast(spawnTile[0], spawnTile[1], tileSize);
        } else if (enemyType.equals("Strong")) {
            // This connects the "Strong" string from the level editor to the actual class
            enemy = new Strong(spawnTile[0], spawnTile[1], tileSize);
        } else {
            // Default to Regular
            enemy = new Regular(spawnTile[0], spawnTile[1], tileSize);
        }

        return enemy;
    }

    // Getters for UI
    public WaveState getState() {
        return state;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public double getInterWaveTimer() {
        return interWaveTimer;
    }

    public double getInterWaveTimeMax() {
        return interWaveTimeMax;
    }

    public int getEnemiesSpawnedThisWave() {
        return enemiesSpawnedThisWave;
    }

    public int getEnemiesToSpawnThisWave() {
        return enemiesToSpawnThisWave;
    }

    public boolean canStartWave() {
        return state == WaveState.IDLE || state == WaveState.WAITING;
    }

    public boolean isSpawning() {
        return state == WaveState.SPAWNING;
    }

    public boolean isWaiting() {
        return state == WaveState.WAITING;
    }

    public boolean isIdle() {
        return state == WaveState.IDLE;
    }

    // For backwards compatibility
    public boolean isWaveActive() {
        return state == WaveState.SPAWNING || state == WaveState.PLAYING;
    }

    // Infinite waves - always return a large number
    public int getTotalWaves() {
        return 999;
    }

    public boolean canStartNextWave() {
        return canStartWave();
    }
}
