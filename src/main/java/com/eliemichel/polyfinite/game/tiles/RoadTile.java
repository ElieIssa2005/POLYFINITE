package com.eliemichel.polyfinite.game.tiles;

import javafx.scene.paint.Color;

public class RoadTile extends Tile {

    private boolean connectTop;
    private boolean connectRight;
    private boolean connectBottom;
    private boolean connectLeft;
    private String textureName;

    public RoadTile(int row, int col) {
        super(row, col);
        this.connectTop = false;
        this.connectRight = false;
        this.connectBottom = false;
        this.connectLeft = false;
        this.textureName = "tile-type-road-oooo"; // Default no connections
    }

    public void setConnections(boolean top, boolean right, boolean bottom, boolean left) {
        this.connectTop = top;
        this.connectRight = right;
        this.connectBottom = bottom;
        this.connectLeft = left;
        updateTextureName();
    }

    private void updateTextureName() {
        String pattern = "";
        pattern += connectTop ? "x" : "o";
        pattern += connectRight ? "x" : "o";
        pattern += connectBottom ? "x" : "o";
        pattern += connectLeft ? "x" : "o";

        this.textureName = "tile-type-road-" + pattern;
    }

    public String getTextureName() {
        return textureName;
    }

    @Override
    public Color getColor() {
        return Color.web("#8B7355"); // Brown road (fallback)
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public boolean canPlaceTower() {
        return false;
    }
}