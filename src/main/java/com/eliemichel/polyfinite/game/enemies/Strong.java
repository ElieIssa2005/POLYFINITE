package com.eliemichel.polyfinite.game.enemies;

import com.eliemichel.polyfinite.game.Enemy;

public class Strong extends Enemy {

    public Strong(int startRow, int startCol, int tileSize) {
        super(startRow, startCol, tileSize);

        this.enemyType = "Strong";
        this.health = 250;
        this.maxHealth = 250;
        this.speed = 0.18;
        this.goldReward = 10;
        this.sizeScale = 0.67;

        loadSprite("/sprites/enemies/Strong.png");
    }

    @Override
    public String getEnemyType() {
        return enemyType;
    }
}