package com.eliemichel.polyfinite.domain.tiles;

import javafx.scene.paint.Color;

public class PlatformTile extends Tile {

    private String textureName;

    public PlatformTile(int row, int col) {
        super(row, col);
        this.textureName = "tile-type-platform-shade-0"; // Default platform texture
    }

    public String getTextureName() {
        return textureName;
    }

    @Override
    public Color getColor() {
        return Color.web("#654321"); // Dark brown platform (fallback)
    }

    @Override
    public int getTypeId() {
        return 2;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public boolean canPlaceTower() {
        return true;
    }
}