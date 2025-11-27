package com.eliemichel.polyfinite.game;

public enum RewardType {
    GOLD("Gold", "#FFD700"),           // Main research resource
    SCALAR("Scalar", "#4CAF50"),       // Common secondary resource
    VECTOR("Vector", "#2196F3"),       // Rarer
    MATRIX("Matrix", "#9C27B0"),       // Rarer
    TENSOR("Tensor", "#FF5722"),       // Rarer
    INFIAR("Infiar", "#E91E63");       // Rarest

    private String displayName;
    private String color;

    RewardType(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}
