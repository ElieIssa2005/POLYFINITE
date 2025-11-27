package com.eliemichel.polyfinite.domain.enemies.types;

import com.eliemichel.polyfinite.domain.enemies.Enemy;

public class Fast extends Enemy {

    public Fast(int startRow, int startCol, int tileSize) {
        super(startRow, startCol, tileSize);

        this.enemyType = "Fast";
        this.health = 50;
        this.maxHealth = 50;
        this.speed = 0.5;
        this.goldReward = 5;
        this.sizeScale = 0.67;

        loadSprite("/sprites/enemies/Fastbig.png");
    }

    @Override
    public String getEnemyType() {
        return enemyType;
    }
}