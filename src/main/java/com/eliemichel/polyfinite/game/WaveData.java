package com.eliemichel.polyfinite.game;

import java.util.ArrayList;

public class WaveData {
    private int waveNumber;
    private ArrayList<EnemySpawn> enemies;
    private double spawnInterval;

    public WaveData(int waveNumber, double spawnInterval) {
        this.waveNumber = waveNumber;
        this.spawnInterval = spawnInterval;
        this.enemies = new ArrayList<>();
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public ArrayList<EnemySpawn> getEnemies() {
        return enemies;
    }

    public double getSpawnInterval() {
        return spawnInterval;
    }

    public void addEnemy(String enemyType, int count) {
        enemies.add(new EnemySpawn(enemyType, count));
    }
}