package com.eliemichel.polyfinite.utils;

public class AtlasRegion {
    private final String name;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public AtlasRegion(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}