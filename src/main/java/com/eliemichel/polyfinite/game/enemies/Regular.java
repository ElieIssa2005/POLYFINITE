package com.eliemichel.polyfinite.game.enemies;

import com.eliemichel.polyfinite.game.Enemy;

public class Regular extends Enemy {

    public Regular(int startRow, int startCol, int tileSize) {
        super(startRow, startCol, tileSize);

        this.enemyType = "Regular";
        this.health = 50;
        this.maxHealth = 50;
        this.speed = 0.3;
        this.goldReward = 3;
        this.sizeScale = 0.67;

        loadSprite("/sprites/enemies/Regular.png");
    }

    @Override
    public String getEnemyType() {
        return enemyType;
    }
}