package com.eliemichel.polyfinite.utils;

public class AtlasManager {
    private static AtlasManager instance;
    private final TextureAtlas atlas;

    private AtlasManager() {
        atlas = new TextureAtlas("/textures/combined.atlas", "/textures/combined.png");
    }

    public static AtlasManager getInstance() {
        if (instance == null) {
            instance = new AtlasManager();
        }
        return instance;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }
}