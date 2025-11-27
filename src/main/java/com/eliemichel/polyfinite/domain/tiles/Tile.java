package com.eliemichel.polyfinite.domain.tiles;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class Tile {
    private int row;
    private int col;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public abstract Color getColor();

    public abstract int getTypeId();

    public abstract boolean isWalkable();

    public abstract boolean canPlaceTower();

    protected Image cachedImage;
    public Image getCachedImage() { return cachedImage; }
    public void setCachedImage(Image img) { this.cachedImage = img; }
}