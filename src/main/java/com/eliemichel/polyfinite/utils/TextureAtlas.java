package com.eliemichel.polyfinite.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TextureAtlas {
    private final Image atlasImage;
    private final Map<String, AtlasRegion> regions;

    public TextureAtlas(String atlasPath, String imagePath) {
        this.atlasImage = new Image(getClass().getResourceAsStream(imagePath));
        this.regions = new HashMap<>();
        parseAtlas(atlasPath);
    }

    private void parseAtlas(String atlasPath) {
        try (InputStream is = getClass().getResourceAsStream(atlasPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            String currentRegionName = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("combined.png") || line.startsWith("size:") ||
                        line.startsWith("format:") || line.startsWith("filter:") || line.startsWith("repeat:")) {
                    continue;
                }

                if (!line.startsWith("bounds:") && !line.startsWith("index:")) {
                    currentRegionName = line;
                } else if (line.startsWith("bounds:")) {
                    String[] parts = line.substring(7).split(",");
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    int width = Integer.parseInt(parts[2].trim());
                    int height = Integer.parseInt(parts[3].trim());

                    if (currentRegionName != null) {
                        regions.put(currentRegionName, new AtlasRegion(currentRegionName, x, y, width, height));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image getRegion(String regionName) {
        AtlasRegion region = regions.get(regionName);
        if (region == null) {
            System.err.println("Region not found: " + regionName);
            return null;
        }

        PixelReader reader = atlasImage.getPixelReader();
        return new WritableImage(reader, region.getX(), region.getY(),
                region.getWidth(), region.getHeight());
    }

    public AtlasRegion getRegionInfo(String regionName) {
        return regions.get(regionName);
    }
}