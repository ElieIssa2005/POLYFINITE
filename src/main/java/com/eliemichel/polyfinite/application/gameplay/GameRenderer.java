package com.eliemichel.polyfinite.application.gameplay;

import com.eliemichel.polyfinite.domain.enemies.Enemy;
import com.eliemichel.polyfinite.domain.level.LevelData;
import com.eliemichel.polyfinite.domain.towers.Projectile;
import com.eliemichel.polyfinite.domain.tiles.*;
import com.eliemichel.polyfinite.domain.towers.types.Tower;
import com.eliemichel.polyfinite.utils.AtlasManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameRenderer {

    private LevelData levelData;
    private int tileSize;
    private double canvasOffsetX;
    private double canvasOffsetY;



    // OPTIMIZATION: Pre-allocate Color objects
    private static final Color EMPTY_TILE_COLOR = Color.rgb(10, 10, 10);
    private static final Color ROAD_FALLBACK_COLOR = Color.rgb(60, 40, 30);
    private static final Color PLATFORM_FALLBACK_COLOR = Color.rgb(0, 100, 80);
    private static final Color PLATFORM_OVERLAY_COLOR = Color.rgb(128, 128, 128, 0.3);
    private static final Color SPAWN_FALLBACK_COLOR = Color.rgb(150, 30, 30);
    private static final Color GOAL_FALLBACK_COLOR = Color.rgb(30, 30, 150);
    private static final Color GOAL_TINT_COLOR = Color.rgb(200, 230, 255);
    private static final Color DEFAULT_TILE_COLOR = Color.rgb(20, 20, 20);
    private static final Color SELECTION_HIGHLIGHT_COLOR = Color.rgb(0, 229, 255);
    private static final Color RANGE_PREVIEW_COLOR = Color.rgb(0, 229, 255, 0.5);


    private Image spawnOverlayImage;
    private Image spawnPortalImage;
    private Image spawnGlowImage;
    private Image targetHollowImage;
    private Image targetBaseImage;

    public GameRenderer(LevelData levelData, int tileSize, double canvasOffsetX, double canvasOffsetY) {
        this.levelData = levelData;
        this.tileSize = tileSize;
        this.canvasOffsetX = canvasOffsetX;
        this.canvasOffsetY = canvasOffsetY;

        // OPTIMIZATION: Cache all tile images ONCE
        cacheAllTileImages();
        loadSharedImages();
    }

    private void cacheAllTileImages() {
        for (int row = 0; row < levelData.getGridHeight(); row++) {
            for (int col = 0; col < levelData.getGridWidth(); col++) {
                Tile tile = levelData.getTile(row, col);

                try {
                    if (tile instanceof RoadTile) {
                        RoadTile roadTile = (RoadTile) tile;
                        Image img = AtlasManager.getInstance().getAtlas().getRegion(roadTile.getTextureName());
                        tile.setCachedImage(img);

                    } else if (tile instanceof PlatformTile) {
                        PlatformTile platformTile = (PlatformTile) tile;
                        Image img = AtlasManager.getInstance().getAtlas().getRegion(platformTile.getTextureName());
                        tile.setCachedImage(img);

                    } else if (tile instanceof SpawnTile) {
                        SpawnTile spawnTile = (SpawnTile) tile;
                        Image img = AtlasManager.getInstance().getAtlas().getRegion(spawnTile.getRoadTextureName());
                        tile.setCachedImage(img);

                    } else if (tile instanceof GoalTile) {
                        GoalTile goalTile = (GoalTile) tile;
                        Image img = AtlasManager.getInstance().getAtlas().getRegion(goalTile.getRoadTextureName());
                        tile.setCachedImage(img);
                    }
                } catch (Exception e) {
                    tile.setCachedImage(null);
                }
            }
        }
        System.out.println("All tile images cached successfully");
    }

    private void loadSharedImages() {
        try {
            spawnOverlayImage = AtlasManager.getInstance().getAtlas().getRegion("tile-type-spawn-overlay");
            spawnPortalImage = AtlasManager.getInstance().getAtlas().getRegion("tile-type-spawn-portal");
            spawnGlowImage = AtlasManager.getInstance().getAtlas().getRegion("tile-type-spawn-glow");
            targetHollowImage = AtlasManager.getInstance().getAtlas().getRegion("tile-type-target-hollow");
            targetBaseImage = AtlasManager.getInstance().getAtlas().getRegion("tile-type-target-base");
        } catch (Exception e) {
            System.out.println("Error loading shared images: " + e.getMessage());
        }
    }


    public void render(GraphicsContext gc, ArrayList<Tower> towers, ArrayList<Projectile> projectiles, ArrayList<Enemy> enemies,
                       Tower selectedTower, int selectedTileRow, int selectedTileCol, String towerToPlace) {
        gc.setImageSmoothing(false);
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.save();
        gc.translate(canvasOffsetX, canvasOffsetY);

        drawLevel(gc);

        for (Tower tower : towers) {
            boolean showRange = tower == selectedTower;
            tower.draw(gc, showRange);
        }

        for (Projectile projectile : projectiles) {
            projectile.draw(gc);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(gc);
        }

        drawRangePreview(gc, towerToPlace, selectedTileRow, selectedTileCol);
        drawSelectedTileHighlight(gc, selectedTileRow, selectedTileCol);

        gc.restore();
    }

    private void drawLevel(GraphicsContext gc) {
        for (int row = 0; row < levelData.getGridHeight(); row++) {
            for (int col = 0; col < levelData.getGridWidth(); col++) {
                Tile tile = levelData.getTile(row, col);
                double x = col * tileSize;
                double y = row * tileSize;

                if (tile instanceof EmptyTile) {
                    gc.setFill(EMPTY_TILE_COLOR);  // ✅ Use cached Color
                    gc.fillRect(x, y, tileSize, tileSize);

                } else if (tile instanceof RoadTile) {
                    Image roadTexture = tile.getCachedImage();  // ✅ Use cached Image
                    if (roadTexture != null) {
                        gc.drawImage(roadTexture, x, y, tileSize, tileSize);
                    } else {
                        gc.setFill(ROAD_FALLBACK_COLOR);  // ✅ Use cached Color
                        gc.fillRect(x, y, tileSize, tileSize);
                    }

                } else if (tile instanceof PlatformTile) {
                    Image platformTexture = tile.getCachedImage();  // ✅ Use cached Image
                    if (platformTexture != null) {
                        double scaleFactor = 0.9;
                        double scaledSize = tileSize * scaleFactor;
                        double offsetX = (tileSize - scaledSize) / 2;
                        double offsetY = (tileSize - scaledSize) / 2;

                        gc.drawImage(platformTexture, x + offsetX, y + offsetY, scaledSize, scaledSize);
                        gc.setFill(PLATFORM_OVERLAY_COLOR);  // ✅ Use cached Color
                        gc.fillRect(x + offsetX, y + offsetY, scaledSize, scaledSize);
                    } else {
                        gc.setFill(PLATFORM_FALLBACK_COLOR);  // ✅ Use cached Color
                        gc.fillRect(x, y, tileSize, tileSize);
                    }

                } else if (tile instanceof SpawnTile) {
                    Image roadTexture = tile.getCachedImage();  // ✅ Use cached Image
                    if (roadTexture != null && spawnOverlayImage != null) {
                        gc.drawImage(roadTexture, x, y, tileSize, tileSize);
                        gc.drawImage(spawnOverlayImage, x, y, tileSize, tileSize);
                        gc.drawImage(spawnPortalImage, x, y, tileSize, tileSize);
                        gc.drawImage(spawnGlowImage, x, y, tileSize, tileSize);
                    } else {
                        gc.setFill(SPAWN_FALLBACK_COLOR);  // ✅ Use cached Color
                        gc.fillRect(x, y, tileSize, tileSize);
                    }

                } else if (tile instanceof GoalTile) {
                    Image roadTexture = tile.getCachedImage();  // ✅ Use cached Image
                    if (roadTexture != null && targetHollowImage != null) {
                        gc.drawImage(roadTexture, x, y, tileSize, tileSize);
                        gc.drawImage(targetHollowImage, x, y, tileSize, tileSize);

                        double scaleFactor = 0.7;
                        double scaledSize = tileSize * scaleFactor;
                        double offsetX = (tileSize - scaledSize) / 2;
                        double offsetY = (tileSize - scaledSize) / 2;

                        gc.drawImage(targetBaseImage, x + offsetX, y + offsetY, scaledSize, scaledSize);

                        gc.save();
                        gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.MULTIPLY);
                        gc.setFill(GOAL_TINT_COLOR);  // ✅ Use cached Color
                        gc.fillRect(x + offsetX, y + offsetY, scaledSize, scaledSize);
                        gc.restore();
                    } else {
                        gc.setFill(GOAL_FALLBACK_COLOR);  // ✅ Use cached Color
                        gc.fillRect(x, y, tileSize, tileSize);
                    }
                } else {
                    gc.setFill(DEFAULT_TILE_COLOR);  // ✅ Use cached Color
                    gc.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    }

    private void drawSelectedTileHighlight(GraphicsContext gc, int selectedTileRow, int selectedTileCol) {
        if (selectedTileRow != -1 && selectedTileCol != -1) {
            double x = selectedTileCol * tileSize;
            double y = selectedTileRow * tileSize;

            gc.setStroke(SELECTION_HIGHLIGHT_COLOR);  // ✅ Use cached Color
            gc.setLineWidth(3);
            gc.strokeRect(x, y, tileSize, tileSize);
        }
    }

    private void drawRangePreview(GraphicsContext gc, String towerToPlace, int selectedTileRow, int selectedTileCol) {
        if (towerToPlace == null || selectedTileRow == -1) return;

        double range = 0;
        if (towerToPlace.equals("Basic")) {
            range = 3.5;
        } else if (towerToPlace.equals("Sniper")) {
            range = 4.5;
        }

        double centerX = selectedTileCol * tileSize + tileSize / 2;
        double centerY = selectedTileRow * tileSize + tileSize / 2;
        double rangePixels = range * tileSize;

        gc.setStroke(RANGE_PREVIEW_COLOR);  // ✅ Use cached Color
        gc.setLineWidth(2);
        gc.strokeOval(centerX - rangePixels, centerY - rangePixels, rangePixels * 2, rangePixels * 2);
    }
}