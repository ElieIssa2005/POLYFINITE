package com.eliemichel.polyfinite.domain.tiles;

public enum TileType {
    EMPTY(0, "Empty"),
    ROAD(1, "Road"),
    PLATFORM(2, "Platform"),
    SPAWN(3, "Spawn"),
    GOAL(4, "Goal");

    private final int id;
    private final String name;

    TileType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static TileType fromId(int id) {
        for (TileType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return EMPTY;
    }
}