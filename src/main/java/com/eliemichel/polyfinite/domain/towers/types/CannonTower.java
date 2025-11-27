package com.eliemichel.polyfinite.domain.towers.types;

import com.eliemichel.polyfinite.domain.enemies.Enemy;
import com.eliemichel.polyfinite.domain.towers.ExplosiveProjectile;
import com.eliemichel.polyfinite.domain.towers.Projectile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.eliemichel.polyfinite.domain.progression.ResearchManager;

import java.util.ArrayList;

public class CannonTower extends Tower {

    private Image baseImage;
    private Image turretImage;
    private ArrayList<ExplosiveProjectile> explosiveProjectiles;

    // Stats from table: Range, Damage, AttackSpeed, RotationSpeed, ProjectileSpeed
    private static final double[][] STATS_TABLE = {
            {1.7, 16.3, 0.6, 40, 2.2},    // MK.0
            {1.9, 26.6, 0.7, 50, 2.4},    // MK.1
            {2.0, 35.5, 0.7, 60, 2.6},    // MK.2
            {2.15, 47, 0.85, 70, 2.6},    // MK.3
            {2.15, 62.8, 1.0, 80, 2.7},   // MK.4
            {2.3, 81, 1.1, 80, 2.9},      // MK.5
            {2.45, 112, 1.1, 90, 3.0},    // MK.6
            {2.6, 147, 1.25, 100, 3.1},   // MK.7
            {2.9, 155, 1.35, 100, 3.2},   // MK.8
            {3.2, 204, 1.35, 110, 3.2},   // MK.9
            {3.2, 246, 1.4, 120, 3.2}     // MK.10
    };

    private static final int[] UPGRADE_COSTS = {
            42, 63, 115, 210, 300, 420, 850, 1200, 1950, 3000
    };

    public CannonTower(int row, int col, int tileSize) {
        super(row, col, tileSize);

        this.explosiveProjectiles = new ArrayList<>();

        try {
            baseImage = new Image(getClass().getResourceAsStream("/sprites/towers/cannon_tower_base.png"));
            turretImage = new Image(getClass().getResourceAsStream("/sprites/towers/cannon_tower_turret.png"));
            System.out.println("Cannon tower sprites loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error loading cannon tower sprites: " + e.getMessage());
        }

        this.baseColor = Color.rgb(255, 140, 0);
        this.turretColor = Color.rgb(200, 100, 0);

        updateStatsForMKLevel();
    }

    // --- RESEARCH INTEGRATION ---
    @Override
    public String getTowerID() {
        return "CANNON"; // Links to "CANNON_DAMAGE", "CANNON_AOE"
    }

    @Override
    protected void updateStatsForMKLevel() {
        double[] stats = STATS_TABLE[mkLevel];
        double rawRange = stats[0];
        double rawDamage = stats[1];

        ResearchManager rm = ResearchManager.getInstance();

        // Range: Base * Global Range (Cannon usually doesn't have specific range upgrades in your tree, but global applies)
        this.baseRange = rawRange * rm.getStatMultiplier("GLOBAL_RANGE");

        // Damage: Base * Global Damage * Cannon Damage
        this.baseDamage = rawDamage * rm.getStatMultiplier("GLOBAL_DAMAGE") * rm.getStatMultiplier("CANNON_DAMAGE");

        this.baseAttackSpeed = stats[2];
        this.baseRotationSpeed = stats[3];
        this.baseProjectileSpeed = stats[4];

        updateRangeSquared();
    }

    public double getExplosionRange() {
        // Base logic: 0.6 base + 0.03 per XP level
        double range = 0.6 + (0.03 * experienceLevel);

        // Apply Research: "CANNON_AOE"
        double aoeMult = ResearchManager.getInstance().getStatMultiplier("CANNON_AOE");
        range *= aoeMult;

        return Math.min(range, 4.0); // Increased cap slightly to account for research
    }

    @Override
    public void update(double deltaTime, ArrayList<Enemy> enemies, ArrayList<Projectile> projectiles) {
        timeSinceLastShot += deltaTime;

        // Update explosive projectiles
        explosiveProjectiles.removeIf(p -> !p.isActive());
        for (ExplosiveProjectile proj : explosiveProjectiles) {
            proj.update(enemies);
        }

        // Find and shoot at targets
        currentTarget = findTarget(enemies);

        if (currentTarget != null) {
            double dx = currentTarget.getX() - x;
            double dy = currentTarget.getY() - y;
            targetAngle = Math.toDegrees(Math.atan2(dy, dx));

            while (targetAngle < 0) targetAngle += 360;
            while (targetAngle >= 360) targetAngle -= 360;
        }

        rotateTowardTarget(deltaTime);

        if (currentTarget != null && canShoot()) {
            shootExplosive();
            timeSinceLastShot = 0;
        }
    }

    private void rotateTowardTarget(double deltaTime) {
        double angleDiff = targetAngle - currentAngle;

        while (angleDiff > 180) angleDiff -= 360;
        while (angleDiff < -180) angleDiff += 360;

        double maxRotation = baseRotationSpeed * deltaTime;

        if (Math.abs(angleDiff) < maxRotation) {
            currentAngle = targetAngle;
        } else {
            currentAngle += Math.signum(angleDiff) * maxRotation;
        }

        while (currentAngle < 0) currentAngle += 360;
        while (currentAngle >= 360) currentAngle -= 360;
    }

    private boolean canShoot() {
        if (currentTarget == null) return false;

        double shootInterval = 1.0 / baseAttackSpeed;
        if (timeSinceLastShot < shootInterval) return false;

        double angleDiff = Math.abs(targetAngle - currentAngle);
        if (angleDiff > 180) angleDiff = 360 - angleDiff;

        return angleDiff < 1.0;
    }

    private void shootExplosive() {
        if (currentTarget == null) return;

        ExplosiveProjectile proj = new ExplosiveProjectile(
                x, y,
                currentTarget,
                getActualDamage(),
                baseProjectileSpeed,
                Color.rgb(255, 140, 0),
                this,
                getExplosionRange()
        );

        explosiveProjectiles.add(proj);
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
            drawExplosionRange(gc);
        }

        // Draw explosive projectiles
        for (ExplosiveProjectile proj : explosiveProjectiles) {
            proj.draw(gc);
        }

        if (baseImage != null && turretImage != null) {
            drawWithSprites(gc);
        } else {
            drawBase(gc);
            drawTurret(gc);
        }

        drawMKLevelIndicator(gc);
    }

    private void drawExplosionRange(GraphicsContext gc) {
        double explosionPixels = getExplosionRange() * tileSize;
        gc.setStroke(Color.rgb(255, 140, 0, 0.3));
        gc.setLineWidth(2);
        gc.setLineDashes(5, 5);
        gc.strokeOval(x - explosionPixels, y - explosionPixels, explosionPixels * 2, explosionPixels * 2);
        gc.setLineDashes();
    }

    private void drawWithSprites(GraphicsContext gc) {
        double scale = 0.065;

        double baseWidth = baseImage.getWidth() * scale;
        double baseHeight = baseImage.getHeight() * scale;
        double turretWidth = turretImage.getWidth() * scale;
        double turretHeight = turretImage.getHeight() * scale;

        gc.drawImage(baseImage, x - baseWidth / 2, y - baseHeight / 2, baseWidth, baseHeight);

        gc.save();
        gc.translate(x, y);
        gc.rotate(currentAngle);
        gc.drawImage(turretImage, -turretWidth / 2, -turretHeight / 2, turretWidth, turretHeight);
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
        return "Cannon";
    }

    @Override
    public int getCost() {
        return 60;
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