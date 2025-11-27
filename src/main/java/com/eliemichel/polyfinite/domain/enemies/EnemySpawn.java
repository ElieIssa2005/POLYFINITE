package com.eliemichel.polyfinite.domain.enemies;

public class EnemySpawn {
    private String enemyType;
    private int count;

    public EnemySpawn(String enemyType, int count) {
        this.enemyType = enemyType;
        this.count = count;
    }

    public String getEnemyType() {
        return enemyType;
    }

    public int getCount() {
        return count;
    }
}