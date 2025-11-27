package com.eliemichel.polyfinite.domain.tiles;

import javafx.scene.paint.Color;

public class SpawnTile extends Tile {

    private boolean connectTop;
    private boolean connectRight;
    private boolean connectBottom;
    private boolean connectLeft;
    private String roadTextureName;

    public SpawnTile(int row, int col) {
        super(row, col);
        this.connectTop = false;
        this.connectRight = false;
        this.connectBottom = false;
        this.connectLeft = false;
        this.roadTextureName = "tile-type-road-oooo"; // Default road texture
    }

    public void setConnections(boolean top, boolean right, boolean bottom, boolean left) {
        this.connectTop = top;
        this.connectRight = right;
        this.connectBottom = bottom;
        this.connectLeft = left;
        updateRoadTextureName();
    }

    private void updateRoadTextureName() {
        String pattern = "";
        pattern += connectTop ? "x" : "o";
        pattern += connectRight ? "x" : "o";
        pattern += connectBottom ? "x" : "o";
        pattern += connectLeft ? "x" : "o";

        this.roadTextureName = "tile-type-road-" + pattern;
    }

    public String getRoadTextureName() {
        return roadTextureName;
    }

    @Override
    public Color getColor() {
        return Color.web("#DC143C"); // Red spawn point (fallback)
    }

    @Override
    public int getTypeId() {
        return 3;
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