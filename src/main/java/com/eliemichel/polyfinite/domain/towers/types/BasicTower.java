package com.eliemichel.polyfinite.domain.towers.types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.eliemichel.polyfinite.domain.progression.ResearchManager;

public class BasicTower extends Tower {

    private Image baseImage;
    private Image turretImage;

    private static final double[][] STATS_TABLE = {
            {2.0, 8.5, 1.25, 90, 2.8},    // MK.0
            {2.2, 10.9, 1.4, 90, 3.0},    // MK.1
            {2.3, 14.1, 1.4, 110, 3.2},   // MK.2
            {2.3, 18.0, 1.55, 120, 3.3},  // MK.3
            {2.5, 23.8, 1.55, 120, 3.5},  // MK.4
            {2.5, 32.9, 1.7, 135, 3.5},   // MK.5
            {2.6, 41.6, 1.7, 145, 3.7},   // MK.6
            {2.7, 57.5, 1.95, 145, 3.8},  // MK.7
            {2.7, 77.8, 2.05, 170, 3.9},  // MK.8
            {2.9, 102.6, 2.05, 180, 4.0}, // MK.9
            {3.0, 131.0, 2.3, 180, 4.2}   // MK.10
    };

    private static final int[] UPGRADE_COSTS = {
            20, 27, 44, 64, 101, 176, 320, 580, 1030, 1770
    };

    public BasicTower(int row, int col, int tileSize) {
        super(row, col, tileSize);

        try {
            baseImage = new Image(getClass().getResourceAsStream("/sprites/towers/basic_tower_base.png"));
            turretImage = new Image(getClass().getResourceAsStream("/sprites/towers/basic_tower_turret.png"));
            System.out.println("Basic tower sprites loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error loading tower sprites: " + e.getMessage());
        }

        this.baseColor = Color.rgb(0, 229, 255);
        this.turretColor = Color.rgb(0, 200, 220);

        updateStatsForMKLevel();

        this.currentAngle = -90;
        this.targetAngle = -90;
    }

    // --- RESEARCH INTEGRATION ---
    @Override
    public String getTowerID() {
        return "BASIC";
    }

    @Override
    protected void updateStatsForMKLevel() {
        // 1. Get Base Stats from your Table
        double[] stats = STATS_TABLE[mkLevel];
        double rawRange = stats[0];
        double rawDamage = stats[1];
        double rawSpeed = stats[2];

        // 2. Apply Research Multipliers
        ResearchManager rm = ResearchManager.getInstance();

        // Range: Base * Global Range * Basic Range
        this.baseRange = rawRange * rm.getStatMultiplier("GLOBAL_RANGE") * rm.getStatMultiplier("BASIC_RANGE");

        // Damage: Base * Global Damage * Basic Damage
        this.baseDamage = rawDamage * rm.getStatMultiplier("GLOBAL_DAMAGE") * rm.getStatMultiplier("BASIC_DAMAGE");

        // Speed: Base * Basic Speed
        this.baseAttackSpeed = rawSpeed * rm.getStatMultiplier("BASIC_SPEED");

        this.baseRotationSpeed = stats[3];
        this.baseProjectileSpeed = stats[4];

        // OPTIMIZATION: Update squared range after changing baseRange
        updateRangeSquared();
    }

    @Override
    public void draw(GraphicsContext gc, boolean showRange) {
        if (showRange) {
            drawRange(gc);
        }

        if (baseImage != null && turretImage != null) {
            drawWithSprites(gc);
        } else {
            drawBase(gc);
            drawTurret(gc);
        }

        drawMKLevelIndicator(gc);
    }

    private void drawWithSprites(GraphicsContext gc) {
        double scale = 0.2;

        double baseWidth = baseImage.getWidth() * scale;
        double baseHeight = baseImage.getHeight() * scale;
        double turretWidth = turretImage.getWidth() * scale;
        double turretHeight = turretImage.getHeight() * scale;

        gc.drawImage(baseImage,
                x - baseWidth / 2,
                y - baseHeight / 2,
                baseWidth,
                baseHeight);

        gc.save();
        gc.translate(x, y);
        gc.rotate(currentAngle);

        gc.drawImage(turretImage,
                -turretWidth / 2,
                -turretHeight / 2,
                turretWidth,
                turretHeight);

        gc.restore();
    }

    private void drawMKLevelIndicator(GraphicsContext gc) {
        if (mkLevel > 0) {
            String mkText = "MK." + mkLevel;
            gc.setFill(Color.rgb(255, 215, 0));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));

            double textX = x - 13;
            double textY = y - tileSize * 0.4;

            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRoundRect(textX - 2, textY - 10, 30, 12, 3, 3);

            gc.setFill(Color.rgb(255, 215, 0));
            gc.fillText(mkText, textX, textY);
        }
    }

    @Override
    public String getTowerName() {
        return "Basic Tower";
    }

    @Override
    public int getCost() {
        return 48;
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
}