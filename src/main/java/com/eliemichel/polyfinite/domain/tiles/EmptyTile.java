package com.eliemichel.polyfinite.domain.tiles;

import javafx.scene.paint.Color;

public class EmptyTile extends Tile {

    public EmptyTile(int row, int col) {
        super(row, col);
    }

    @Override
    public Color getColor() {
        return Color.web("#2d5016"); // Dark green (grass)
    }

    @Override
    public int getTypeId() {
        return 0;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public boolean canPlaceTower() {
        return false;
    }
}