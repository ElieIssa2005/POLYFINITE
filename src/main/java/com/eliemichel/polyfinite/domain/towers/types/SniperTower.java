package com.eliemichel.polyfinite.domain.towers.types;

import com.eliemichel.polyfinite.domain.enemies.Enemy;
import com.eliemichel.polyfinite.domain.towers.Projectile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.eliemichel.polyfinite.domain.progression.ResearchManager;

import java.util.ArrayList;
import java.util.Random;

public class SniperTower extends Tower {

    private Image baseImage;
    private Image turretImage;

    // Sniper unique mechanics
    private boolean isAiming;
    private double aimTimer;
    private boolean hasAimed;
    private double aimingSpeed;
    private Enemy lastTarget;

    private static final Random random = new Random();

    public SniperTower(int row, int col, int tileSize) {
        super(row, col, tileSize);

        // Load sprites (SWAPPED - base and turret were backwards!)
        try {
            baseImage = new Image(getClass().getResourceAsStream("/sprites/towers/sniper_tower_turret.png"));
            turretImage = new Image(getClass().getResourceAsStream("/sprites/towers/sniper_tower_base.png"));
            System.out.println("Sniper tower sprites loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error loading sniper tower sprites: " + e.getMessage());
        }

        // Initialize at MK.0 (Upgrade Level 0)
        this.mkLevel = 0;
        updateStatsForMKLevel();

        // Fallback colors
        this.baseColor = Color.rgb(139, 69, 19);
        this.turretColor = Color.rgb(160, 82, 45);

        // Initialize aiming state
        this.isAiming = false;
        this.aimTimer = 0;
        this.hasAimed = false;
        this.aimingSpeed = 100;
        this.lastTarget = null;

        // Start facing UP instead of RIGHT
        this.currentAngle = -90;
        this.targetAngle = -90;
    }

    // --- RESEARCH INTEGRATION ---
    @Override
    public String getTowerID() {
        return "SNIPER";
    }

    @Override
    protected void updateStatsForMKLevel() {
        // Stats from the upgrade table
        double rawRange = 4.5;
        double rawDamage = 61.8;
        double rawSpeed = 0.266;

        switch (mkLevel) {
            case 0: rawRange = 4.5; rawDamage = 61.8; rawSpeed = 0.266; baseRotationSpeed = 45; aimingSpeed = 90; break;
            case 1: rawRange = 4.9; rawDamage = 86; rawSpeed = 0.304; baseRotationSpeed = 49.5; aimingSpeed = 100; break;
            case 2: rawRange = 5.3; rawDamage = 111; rawSpeed = 0.361; baseRotationSpeed = 54; aimingSpeed = 105; break;
            case 3: rawRange = 5.3; rawDamage = 171; rawSpeed = 0.361; baseRotationSpeed = 57.6; aimingSpeed = 110; break;
            case 4: rawRange = 5.7; rawDamage = 241; rawSpeed = 0.418; baseRotationSpeed = 65.7; aimingSpeed = 120; break;
            case 5: rawRange = 6.1; rawDamage = 336; rawSpeed = 0.475; baseRotationSpeed = 65.7; aimingSpeed = 135; break;
            case 6: rawRange = 6.1; rawDamage = 416; rawSpeed = 0.503; baseRotationSpeed = 72.9; aimingSpeed = 135; break;
            case 7: rawRange = 6.5; rawDamage = 578; rawSpeed = 0.503; baseRotationSpeed = 77.4; aimingSpeed = 150; break;
            case 8: rawRange = 6.9; rawDamage = 753; rawSpeed = 0.55; baseRotationSpeed = 77.4; aimingSpeed = 170; break;
            case 9: rawRange = 7.3; rawDamage = 942; rawSpeed = 0.59; baseRotationSpeed = 77.4; aimingSpeed = 180; break;
            case 10: rawRange = 7.8; rawDamage = 1234; rawSpeed = 0.61; baseRotationSpeed = 81; aimingSpeed = 200; break;
        }

        // Apply Research
        ResearchManager rm = ResearchManager.getInstance();

        this.baseRange = rawRange * rm.getStatMultiplier("GLOBAL_RANGE") * rm.getStatMultiplier("SNIPER_RANGE");
        this.baseDamage = rawDamage * rm.getStatMultiplier("GLOBAL_DAMAGE") * rm.getStatMultiplier("SNIPER_DAMAGE");
        this.baseAttackSpeed = rawSpeed * rm.getStatMultiplier("SNIPER_SPEED");

        // Sniper has no projectile speed (hitscan)
        baseProjectileSpeed = 0;

        updateRangeSquared();
    }

    @Override
    public void update(double deltaTime, ArrayList<Enemy> enemies, ArrayList<Projectile> projectiles) {
        timeSinceLastShot += deltaTime;

        // Find target based on priority
        currentTarget = findTarget(enemies);

        // Check if need to re-aim
        if (currentTarget != lastTarget || currentTarget == null ||
                !currentTarget.isAlive() || isTargetOutOfRange()) {
            // Reset aiming if target changed
            isAiming = false;
            hasAimed = false;
            aimTimer = 0;
            lastTarget = currentTarget;
        }

        if (currentTarget != null) {
            // Calculate angle to target
            double dx = currentTarget.getX() - x;
            double dy = currentTarget.getY() - y;
            targetAngle = Math.toDegrees(Math.atan2(dy, dx));

            // Normalize to 0-360 range
            while (targetAngle < 0) targetAngle += 360;
            while (targetAngle >= 360) targetAngle -= 360;
        }

        // Rotate toward target
        rotateTowardTarget(deltaTime);

        // Handle aiming mechanic
        if (currentTarget != null && isFacingTarget() && !hasAimed) {
            isAiming = true;
            aimTimer += deltaTime;

            // Calculate actual aim time based on aiming speed and nearby enemies
            double actualAimingSpeed = calculateActualAimingSpeed(enemies);
            double aimTime = 100.0 / actualAimingSpeed;

            if (aimTimer >= aimTime) {
                hasAimed = true;
                isAiming = false;
            }
        }

        // Shoot if ready, aimed, and facing target
        if (currentTarget != null && hasAimed && canShoot()) {
            shootHitscan();
            timeSinceLastShot = 0;
        }
    }

    private boolean isTargetOutOfRange() {
        if (currentTarget == null) return true;
        double distance = getDistance(currentTarget.getX(), currentTarget.getY());
        return distance > baseRange * tileSize;
    }

    private boolean isFacingTarget() {
        double angleDiff = Math.abs(targetAngle - currentAngle);
        if (angleDiff > 180) angleDiff = 360 - angleDiff;
        return angleDiff < 1.0;
    }

    private double calculateActualAimingSpeed(ArrayList<Enemy> enemies) {
        // Count enemies within 1-tile radius of target
        if (currentTarget == null) return aimingSpeed;

        int nearbyEnemies = 0;
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            double dx = enemy.getX() - currentTarget.getX();
            double dy = enemy.getY() - currentTarget.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= tileSize) {
                nearbyEnemies++;
            }
        }

        // Reduce aiming speed by 4% per nearby enemy (min 15%)
        double reduction = nearbyEnemies * 0.04;
        double actualSpeed = aimingSpeed * (1.0 - reduction);

        // Minimum 15% of base aiming speed
        return Math.max(actualSpeed, aimingSpeed * 0.15);
    }

    private void rotateTowardTarget(double deltaTime) {
        double angleDiff = targetAngle - currentAngle;

        // Normalize angle difference
        while (angleDiff > 180) angleDiff -= 360;
        while (angleDiff < -180) angleDiff += 360;

        // Rotate at rotation speed
        double maxRotation = baseRotationSpeed * deltaTime;

        if (Math.abs(angleDiff) < maxRotation) {
            currentAngle = targetAngle;
        } else {
            currentAngle += Math.signum(angleDiff) * maxRotation;

            // If rotating too slow, might need to re-aim
            if (Math.abs(angleDiff) > 10 && hasAimed) {
                hasAimed = false;
                isAiming = false;
                aimTimer = 0;
            }
        }

        // Keep angle in 0-360 range
        while (currentAngle < 0) currentAngle += 360;
        while (currentAngle >= 360) currentAngle -= 360;
    }

    private boolean canShoot() {
        if (currentTarget == null || !hasAimed) return false;

        // Check if enough time has passed
        double shootInterval = 1.0 / baseAttackSpeed;
        return timeSinceLastShot >= shootInterval;
    }

    private void shootHitscan() {
        if (currentTarget == null) return;

        // Calculate crit chance and multiplier
        double critChance = calculateCritChance();
        double critMultiplier = calculateCritMultiplier();

        // Determine if this shot is a crit
        boolean isCrit = random.nextDouble() * 100 < critChance;

        // Calculate final damage
        double finalDamage = getActualDamage();
        if (isCrit) {
            finalDamage *= (critMultiplier / 100.0);
            System.out.println("CRIT! " + finalDamage + " damage");
        }

        // Deal instant damage (hitscan)
        int healthBefore = currentTarget.getHealth();
        currentTarget.takeDamage((int) finalDamage);

        // Award XP if kill
        if (!currentTarget.isAlive() && healthBefore > 0) {
            addExperience(10);
        }

        // Reset aim after shooting
        hasAimed = false;
        aimTimer = 0;
    }

    private double calculateCritChance() {
        // Base formula: 7.5% + ((XP Level - 1) * 0.65)^0.63 + (XP Level - 1) * 1.15%
        double baseCrit = 7.5;

        if (experienceLevel > 0) {
            double formula = Math.pow((experienceLevel - 1) * 0.65, 0.63);
            double perLevel = (experienceLevel - 1) * 1.15;
            baseCrit += formula + perLevel;
        }

        // ADD RESEARCH BONUS
        // getStatMultiplier returns e.g. 1.05 for 5%. We want the 0.05 part * 100 = 5.0
        double researchBonus = (ResearchManager.getInstance().getStatMultiplier("SNIPER_CRIT") - 1.0) * 100.0;
        baseCrit += researchBonus;

        return Math.min(baseCrit, 100.0);  // Max 100%
    }

    private double calculateCritMultiplier() {
        // Base formula: 200% + (XP Level - 1) * 10 + (XP Level - 1) * 5
        double baseMultiplier = 200.0;

        if (experienceLevel > 0) {
            baseMultiplier += (experienceLevel - 1) * 10;
            baseMultiplier += (experienceLevel - 1) * 5;
        }

        return baseMultiplier;
    }

    private Enemy findTarget(ArrayList<Enemy> enemies) {
        Enemy target = null;

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            double distance = getDistance(enemy.getX(), enemy.getY());
            if (distance > baseRange * tileSize) continue;

            if (target == null) {
                target = enemy;
            } else {
                switch (targetPriority) {
                    case "First":
                    case "Closest":
                        if (distance < getDistance(target.getX(), target.getY())) {
                            target = enemy;
                        }
                        break;
                    case "Strongest":
                        if (enemy.getHealth() > target.getHealth()) {
                            target = enemy;
                        }
                        break;
                }
            }
        }

        return target;
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

        if (baseImage != null && turretImage != null) {
            drawWithSprites(gc);
        } else {
            drawBase(gc);
            drawTurret(gc);
        }

        // Draw aiming indicator
        if (isAiming) {
            drawAimingIndicator(gc);
        }

        drawMKLevelIndicator(gc);
    }

    private void drawWithSprites(GraphicsContext gc) {
        double scale = 0.3;

        // Draw base
        double baseWidth = baseImage.getWidth() * scale;
        double baseHeight = baseImage.getHeight() * scale;

        gc.drawImage(baseImage,
                x - baseWidth / 2,
                y - baseHeight / 2 - 3.5,
                baseWidth,
                baseHeight);

        // Draw turret (rotated) - MUST be perfectly centered for proper rotation
        gc.save();
        gc.translate(x, y);
        gc.rotate(currentAngle);

        double turretWidth = turretImage.getWidth() * scale;
        double turretHeight = turretImage.getHeight() * scale;

        // Draw perfectly centered - this ensures it rotates around its center
        gc.drawImage(turretImage,
                -turretWidth / 2,
                -turretHeight / 2,
                turretWidth,
                turretHeight);

        gc.restore();
    }

    private void drawAimingIndicator(GraphicsContext gc) {
        // Draw narrowing angle to show aiming progress
        double actualAimingSpeed = aimingSpeed;  // Simplified for visual
        double aimTime = 100.0 / actualAimingSpeed;
        double progress = Math.min(aimTimer / aimTime, 1.0);

        // Draw cone that narrows as aiming progresses
        double maxAngle = 15;  // Max cone angle
        double coneAngle = maxAngle * (1.0 - progress);

        gc.save();
        gc.translate(x, y);
        gc.rotate(currentAngle);

        gc.setStroke(Color.rgb(255, 0, 0, 0.5));
        gc.setLineWidth(2);

        double rangePixels = baseRange * tileSize * 0.5;

        // Draw aiming lines
        gc.strokeLine(0, 0, rangePixels, Math.tan(Math.toRadians(coneAngle)) * rangePixels);
        gc.strokeLine(0, 0, rangePixels, -Math.tan(Math.toRadians(coneAngle)) * rangePixels);

        gc.restore();
    }

    private void drawMKLevelIndicator(GraphicsContext gc) {
        if (mkLevel > 0) {
            String mkText = "MK." + mkLevel;
            gc.setFill(Color.rgb(255, 215, 0));  // Gold color
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));

            // Draw above the tower
            double textX = x - 13;
            double textY = y - tileSize * 0.4;

            // Background
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRoundRect(textX - 2, textY - 10, 30, 12, 3, 3);

            // Text
            gc.setFill(Color.rgb(255, 215, 0));
            gc.fillText(mkText, textX, textY);
        }
    }

    @Override
    public String getTowerName() {
        return "Sniper";
    }

    @Override
    public int getCost() {
        return 80;
    }

    @Override
    public int getUpgradeCost() {
        // Upgrade costs from the table
        int[] costs = {0, 75, 117, 190, 420, 610, 875, 1330, 2140, 3630};
        if (mkLevel < costs.length) {
            return costs[mkLevel];
        }
        return 0;
    }

    @Override
    public int getMaxMKLevel() {
        return 10;
    }

    // Additional getters for UI display
    public double getCritChance() {
        return calculateCritChance();
    }

    public double getCritMultiplier() {
        return calculateCritMultiplier();
    }

    public double getAimingSpeed() {
        return aimingSpeed;
    }

    public boolean isCurrentlyAiming() {
        return isAiming;
    }
}