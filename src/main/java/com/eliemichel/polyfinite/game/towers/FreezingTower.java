package com.eliemichel.polyfinite.game.towers;

import com.eliemichel.polyfinite.game.Enemy;
import com.eliemichel.polyfinite.game.Projectile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.eliemichel.polyfinite.game.ResearchManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FreezingTower extends Tower {

    private Image baseImage;
    private Image turretImage;

    // Track which enemies have been slowed (for XP)
    private HashMap<Enemy, Double> enemiesInRange;

    // Freezing-specific stats
    private double freezingPercent;
    private double freezingSpeed;

    // Stats table: Range, FreezingPercent, FreezingSpeed
    private static final double[][] STATS_TABLE = {
            {2.2, 30, 6.0},     // MK.0
            {2.4, 35, 7.0},     // MK.1
            {2.5, 40, 7.5},     // MK.2
            {2.7, 45, 8.0},     // MK.3
            {2.8, 50, 8.5},     // MK.4
            {3.0, 52, 9.0},     // MK.5
            {3.1, 55, 9.5},     // MK.6
            {3.3, 58, 10.0},    // MK.7
            {3.4, 60, 10.5},    // MK.8
            {3.6, 62, 11.0},    // MK.9
            {3.8, 65, 12.0}     // MK.10 (caps at 65%)
    };

    private static final int[] UPGRADE_COSTS = {
            55, 80, 140, 250, 360, 520, 1050, 1500, 2400, 3700
    };

    public FreezingTower(int row, int col, int tileSize) {
        super(row, col, tileSize);

        this.enemiesInRange = new HashMap<>();

        try {
            baseImage = new Image(getClass().getResourceAsStream("/sprites/towers/freezing_tower_base.png"));
            turretImage = new Image(getClass().getResourceAsStream("/sprites/towers/freezing_tower_turret.png"));
            System.out.println("Freezing tower sprites loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error loading freezing tower sprites: " + e.getMessage());
        }

        this.baseColor = Color.rgb(100, 200, 255);
        this.turretColor = Color.rgb(150, 220, 255);

        updateStatsForMKLevel();
    }

    // --- RESEARCH INTEGRATION ---
    @Override
    public String getTowerID() {
        return "FREEZE"; // Links to "FREEZE_RANGE", "FREEZE_DURATION"
    }

    @Override
    protected void updateStatsForMKLevel() {
        double[] stats = STATS_TABLE[mkLevel];
        double rawRange = stats[0];
        this.freezingPercent = stats[1];
        double rawSpeed = stats[2];

        ResearchManager rm = ResearchManager.getInstance();

        // Range: Base * Global * Freeze Range
        this.baseRange = rawRange * rm.getStatMultiplier("GLOBAL_RANGE") * rm.getStatMultiplier("FREEZE_RANGE");

        // Speed: Base * Freeze Duration (Mapped to application speed)
        this.freezingSpeed = rawSpeed * rm.getStatMultiplier("FREEZE_DURATION");

        // Freezing doesn't use damage, attack speed, etc.
        this.baseDamage = 0;
        this.baseAttackSpeed = 0;
        this.baseRotationSpeed = 0;
        this.baseProjectileSpeed = 0;

        updateRangeSquared();
    }

    @Override
    public void update(double deltaTime, ArrayList<Enemy> enemies, ArrayList<Projectile> projectiles) {
        // No projectiles for freezing tower
        // Process all enemies in range

        ArrayList<Enemy> currentEnemies = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            double distance = getDistance(enemy.getX(), enemy.getY());

            if (distance <= baseRange * tileSize) {
                currentEnemies.add(enemy);

                // Apply freezing effect
                applyFreezing(enemy, deltaTime);

                // Track for XP
                if (!enemiesInRange.containsKey(enemy)) {
                    enemiesInRange.put(enemy, 0.0);
                }

                // Increment time enemy has been in range
                double timeInRange = enemiesInRange.get(enemy);
                timeInRange += deltaTime;
                enemiesInRange.put(enemy, timeInRange);

                // Award XP every 1 second an enemy is slowed
                if (timeInRange >= 1.0) {
                    addExperience(2);
                    enemiesInRange.put(enemy, 0.0);
                }
            }
        }

        // Remove enemies that left range
        enemiesInRange.keySet().removeIf(enemy -> !currentEnemies.contains(enemy) || !enemy.isAlive());
    }

    private void applyFreezing(Enemy enemy, double deltaTime) {
        // Get current slow effect on enemy
        double currentSlow = enemy.getSlowPercent();

        // Calculate new slow percent
        double slowIncrease = freezingSpeed * deltaTime;
        double newSlow = Math.min(currentSlow + slowIncrease, freezingPercent);

        // Apply slow
        enemy.setSlowPercent(newSlow);
        enemy.setInFreezingRange(true);
    }

    private double getDistance(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void draw(GraphicsContext gc, boolean showRange) {
        if (showRange) {
            drawRange(gc);
        }

        // Draw freezing effect (animated circles)
        drawFreezingEffect(gc);

        if (baseImage != null && turretImage != null) {
            drawWithSprites(gc);
        } else {
            drawBase(gc);
            drawTurret(gc);
        }

        drawMKLevelIndicator(gc);
    }

    private void drawFreezingEffect(GraphicsContext gc) {
        // Draw animated icy effect
        double rangePixels = baseRange * tileSize;

        // Pulsing effect
        double pulse = Math.sin(System.currentTimeMillis() / 500.0) * 0.1 + 0.9;

        gc.setStroke(Color.rgb(150, 220, 255, 0.3 * pulse));
        gc.setLineWidth(3);
        gc.strokeOval(x - rangePixels * pulse, y - rangePixels * pulse,
                rangePixels * 2 * pulse, rangePixels * 2 * pulse);

        // Inner circle
        gc.setStroke(Color.rgb(200, 240, 255, 0.2 * pulse));
        gc.setLineWidth(2);
        gc.strokeOval(x - rangePixels * 0.6 * pulse, y - rangePixels * 0.6 * pulse,
                rangePixels * 1.2 * pulse, rangePixels * 1.2 * pulse);
    }

    private void drawWithSprites(GraphicsContext gc) {
        double scale = 0.065;

        double baseWidth = baseImage.getWidth() * scale;
        double baseHeight = baseImage.getHeight() * scale;
        double turretWidth = turretImage.getWidth() * scale;
        double turretHeight = turretImage.getHeight() * scale;

        gc.drawImage(baseImage, x - baseWidth / 2, y - baseHeight / 2, baseWidth, baseHeight);

        // Turret rotates slowly for visual effect
        double rotation = (System.currentTimeMillis() / 50.0) % 360;

        gc.save();
        gc.translate(x, y);
        gc.rotate(rotation);
        gc.drawImage(turretImage, -turretWidth / 2, -turretHeight / 2, turretWidth, turretHeight);
        gc.restore();
    }

    @Override
    protected void drawBase(GraphicsContext gc) {
        double size = tileSize * 0.7;

        // Icy gradient effect
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(Color.rgb(150, 220, 255, 0.6));
        glow.setRadius(15);
        gc.setEffect(glow);

        gc.setFill(baseColor);
        gc.fillOval(x - size / 2, y - size / 2, size, size);

        gc.setEffect(null);
    }

    @Override
    protected void drawTurret(GraphicsContext gc) {
        // Draw snowflake-like pattern instead of barrel
        double rotation = (System.currentTimeMillis() / 50.0) % 360;

        gc.save();
        gc.translate(x, y);
        gc.rotate(rotation);

        gc.setStroke(turretColor);
        gc.setLineWidth(3);

        // Draw 6 spokes
        for (int i = 0; i < 6; i++) {
            double angle = i * 60;
            gc.save();
            gc.rotate(angle);
            gc.strokeLine(0, 0, tileSize * 0.3, 0);
            gc.restore();
        }

        gc.restore();
    }

    private void drawMKLevelIndicator(GraphicsContext gc) {
        if (mkLevel > 0) {
            String mkText = "MK." + mkLevel;
            gc.setFill(Color.rgb(150, 220, 255));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));

            double textX = x - 13;
            double textY = y - tileSize * 0.4;

            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRoundRect(textX - 2, textY - 10, 30, 12, 3, 3);

            gc.setFill(Color.rgb(150, 220, 255));
            gc.fillText(mkText, textX, textY);
        }
    }

    @Override
    public String getTowerName() {
        return "Freezing";
    }

    @Override
    public int getCost() {
        return 80;
    }

    @Override
    public int getUpgradeCost() {
        if (mkLevel >= UPGRADE_COSTS.length) {
            return 0;
        }
        return UPGRADE_COSTS[mkLevel];
    }

    @Override
    public int getMaxMKLevel() {
        return 10;
    }

    // Getter methods for freezing stats
    public double getFreezingPercent() {
        return freezingPercent;
    }

    public double getFreezingSpeed() {
        return freezingSpeed;
    }
}