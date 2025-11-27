package com.eliemichel.polyfinite.domain.enemies;

public class EnemyWeight {
    private String enemyType;
    private int weight; // percentage (0-100)

    public EnemyWeight(String enemyType, int weight) {
        this.enemyType = enemyType;
        this.weight = weight;
    }

    public String getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(String enemyType) {
        this.enemyType = enemyType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
