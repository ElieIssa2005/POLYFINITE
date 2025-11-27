package com.eliemichel.polyfinite.game;

public enum SpawnDensity {
    LOW("Low", 1.0),
    MEDIUM("Medium", 0.75),
    HIGH("High", 0.5),
    EXTREME("Extreme", 0.25),
    CUSTOM("Custom", -1.0);

    private String displayName;
    private double interval;

    SpawnDensity(String displayName, double interval) {
        this.displayName = displayName;
        this.interval = interval;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getInterval() {
        return interval;
    }

    public static SpawnDensity fromString(String name) {
        for (SpawnDensity density : values()) {
            if (density.name().equalsIgnoreCase(name)) {
                return density;
            }
        }
        return MEDIUM;
    }
}
