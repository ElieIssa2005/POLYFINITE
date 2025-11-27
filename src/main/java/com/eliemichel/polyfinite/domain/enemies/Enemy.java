package com.eliemichel.polyfinite.domain.enemies;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class Enemy {

    // OPTIMIZATION: Static sprite cache shared by ALL enemies
    private static final Map<String, Image> SPRITE_CACHE = new HashMap<>();

    protected double x;
    protected double y;
    protected int health;
    protected int maxHealth;
    protected double speed;
    protected boolean alive;
    protected boolean reachedGoal;
    protected int pathIndex;
    protected ArrayList<int[]> path;
    protected int tileSize;
    protected int goldReward;
    protected Image sprite;  // Reference to cached sprite
    protected String enemyType;
    protected double sizeScale = 0.5;

    protected double laneOffset;
    protected double currentAngle;

    protected double slowPercent;
    protected boolean inFreezingRange;

    protected static final Random random = new Random();

    private double targetX;
    private double targetY;
    private boolean needsRecalculation = true;

    private double renderSize;
    private double renderHalfSize;
    private double healthBarY;
    
    // Track which tower type dealt the killing blow
    private String lastHitByTower = null;

    private static final Color FROZEN_COLOR = Color.rgb(150, 220, 255);
    private static final Color NORMAL_COLOR = Color.rgb(0, 230, 118);
    private static final Color FROZEN_OVERLAY = Color.rgb(150, 220, 255, 0.3);
    private static final Color HEALTH_BG_COLOR = Color.rgb(50, 50, 50);
    private static final Color HEALTH_COLOR = Color.rgb(0, 230, 118);
    private static final Color FREEZE_STROKE_COLOR = Color.rgb(150, 220, 255, 0.6);
    private static final Color FREEZE_PARTICLE_COLOR = Color.rgb(200, 240, 255, 0.8);

    public Enemy(int startRow, int startCol, int tileSize) {
        this.tileSize = tileSize;
        this.alive = true;
        this.reachedGoal = false;
        this.pathIndex = 0;
        this.path = new ArrayList<>();
        this.slowPercent = 0.0;
        this.inFreezingRange = false;

        this.x = startCol * tileSize + tileSize / 2;
        this.y = startRow * tileSize + tileSize / 2;

        this.laneOffset = (random.nextDouble() - 0.5) * tileSize * 0.7;
        this.currentAngle = 0;

        this.renderSize = tileSize * sizeScale;
        this.renderHalfSize = renderSize / 2;
        this.healthBarY = -renderHalfSize - 8;
    }

    // OPTIMIZATION: Load sprite from cache or create once and cache it
    protected void loadSprite(String spritePath) {
        // Check if sprite is already cached
        if (SPRITE_CACHE.containsKey(spritePath)) {
            this.sprite = SPRITE_CACHE.get(spritePath);
            System.out.println("Using cached sprite: " + spritePath);
            return;
        }

        // Load sprite and cache it
        try {
            Image newSprite = new Image(getClass().getResourceAsStream(spritePath));
            SPRITE_CACHE.put(spritePath, newSprite);
            this.sprite = newSprite;
            System.out.println("Loaded and cached sprite: " + spritePath);
        } catch (Exception e) {
            System.out.println("Error loading enemy sprite: " + e.getMessage());
            this.sprite = null;
        }
    }

    public void setPath(ArrayList<int[]> path) {
        this.path = path;

        if (path.size() >= 2) {
            int[] first = path.get(0);
            int[] second = path.get(1);
            double dx = second[1] - first[1];
            double dy = second[0] - first[0];
            this.currentAngle = Math.toDegrees(Math.atan2(dy, dx));
        }
    }

    public void update() {
        if (!alive || path.isEmpty()) {
            return;
        }

        if (!inFreezingRange && slowPercent > 0) {
            slowPercent -= 3.0;
            if (slowPercent < 0) slowPercent = 0;
        }

        inFreezingRange = false;

        if (pathIndex >= path.size()) {
            reachedGoalInternal();
            return;
        }

        if (needsRecalculation) {
            calculateTarget();
            needsRecalculation = false;
        }

        double dx = targetX - x;
        double dy = targetY - y;
        double distSq = dx * dx + dy * dy;

        double actualSpeed = speed * (1.0 - slowPercent / 100.0);
        double speedThreshold = actualSpeed * 2;
        double speedThresholdSq = speedThreshold * speedThreshold;

        if (distSq < speedThresholdSq) {
            pathIndex++;
            needsRecalculation = true;
            if (pathIndex >= path.size()) {
                reachedGoalInternal();
            }
            return;
        }

        double distance = Math.sqrt(distSq);

        if (distance > 0.1) {
            double targetAngle = Math.toDegrees(Math.atan2(dy, dx));

            double angleDiff = targetAngle - currentAngle;
            while (angleDiff > 180) angleDiff -= 360;
            while (angleDiff < -180) angleDiff += 360;

            currentAngle += angleDiff * 0.15;

            double moveX = (dx / distance) * actualSpeed;
            double moveY = (dy / distance) * actualSpeed;

            x += moveX;
            y += moveY;
        }
    }

    private void calculateTarget() {
        int[] currentWaypoint = path.get(pathIndex);

        double baseTargetX = currentWaypoint[1] * tileSize + tileSize / 2;
        double baseTargetY = currentWaypoint[0] * tileSize + tileSize / 2;

        double dirX, dirY;
        if (pathIndex + 1 < path.size()) {
            int[] nextWaypoint = path.get(pathIndex + 1);
            dirX = nextWaypoint[1] - currentWaypoint[1];
            dirY = nextWaypoint[0] - currentWaypoint[0];
        } else {
            dirX = baseTargetX - x;
            dirY = baseTargetY - y;
        }

        double pathAngle = Math.atan2(dirY, dirX);
        double perpAngle = pathAngle + Math.PI / 2;

        if (pathIndex == path.size() - 1) {
            targetX = baseTargetX;
            targetY = baseTargetY;
        } else {
            targetX = baseTargetX + Math.cos(perpAngle) * laneOffset;
            targetY = baseTargetY + Math.sin(perpAngle) * laneOffset;
        }
    }

    public void draw(GraphicsContext gc) {
        if (!alive) {
            return;
        }

        if (slowPercent > 0) {
            drawFreezeEffectOptimized(gc);
        }

        gc.save();
        gc.translate(x, y);
        gc.rotate(currentAngle);

        if (sprite != null) {
            if (slowPercent > 20) {
                gc.setGlobalAlpha(0.7);
                gc.drawImage(sprite, -renderHalfSize, -renderHalfSize, renderSize, renderSize);
                gc.setGlobalAlpha(1.0);

                gc.setFill(FROZEN_OVERLAY);
                gc.fillRect(-renderHalfSize, -renderHalfSize, renderSize, renderSize);
            } else {
                gc.drawImage(sprite, -renderHalfSize, -renderHalfSize, renderSize, renderSize);
            }
        } else {
            gc.setFill(slowPercent > 20 ? FROZEN_COLOR : NORMAL_COLOR);
            gc.fillRect(-renderHalfSize, -renderHalfSize, renderSize, renderSize);
        }

        gc.restore();

        drawHealthBarOptimized(gc);
    }

    private void drawFreezeEffectOptimized(GraphicsContext gc) {
        double intensity = slowPercent / 65.0;

        gc.setStroke(FREEZE_STROKE_COLOR);
        gc.setLineWidth(2);

        double effectSize = renderSize * 0.6;
        gc.strokeOval(x - effectSize, y - effectSize, effectSize * 2, effectSize * 2);

        if (slowPercent > 30) {
            gc.setFill(FREEZE_PARTICLE_COLOR);

            double baseAngle = (x + y) % 360;

            for (int i = 0; i < 3; i++) {
                double angle = (baseAngle + i * 120) % 360;
                double px = x + Math.cos(Math.toRadians(angle)) * renderHalfSize;
                double py = y + Math.sin(Math.toRadians(angle)) * renderHalfSize;
                gc.fillOval(px - 2, py - 2, 4, 4);
            }
        }
    }

    private void drawHealthBarOptimized(GraphicsContext gc) {
        double healthPercent = (double) health / maxHealth;

        double barY = y + healthBarY;

        gc.setFill(HEALTH_BG_COLOR);
        gc.fillRect(x - renderHalfSize, barY, renderSize, 4);

        gc.setFill(HEALTH_COLOR);
        gc.fillRect(x - renderHalfSize, barY, renderSize * healthPercent, 4);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }
    
    public void takeDamage(int damage, String towerType) {
        lastHitByTower = towerType;
        takeDamage(damage);
    }
    
    public String getLastHitByTower() {
        return lastHitByTower;
    }

    private void reachedGoalInternal() {
        alive = false;
        reachedGoal = true;
        System.out.println(enemyType + " enemy reached the goal!");
    }

    public boolean isAlive() {
        return alive;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public boolean hasReachedGoal() {
        return reachedGoal;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public double getSlowPercent() {
        return slowPercent;
    }

    public void setSlowPercent(double percent) {
        this.slowPercent = Math.min(percent, 65.0);
    }

    public void setInFreezingRange(boolean inRange) {
        this.inFreezingRange = inRange;
    }

    // Initialize enemy with scaled health for wave system
    public void initialize(int scaledHealth) {
        this.maxHealth = scaledHealth;
        this.health = scaledHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public abstract String getEnemyType();
}