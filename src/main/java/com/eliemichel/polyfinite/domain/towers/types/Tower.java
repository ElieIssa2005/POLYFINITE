package com.eliemichel.polyfinite.domain.towers.types;

import com.eliemichel.polyfinite.domain.enemies.Enemy;
import com.eliemichel.polyfinite.domain.towers.Projectile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.eliemichel.polyfinite.domain.progression.ResearchManager;

import java.util.ArrayList;

public abstract class Tower {

    protected int row;
    protected int col;
    protected double x;
    protected double y;
    protected int tileSize;

    protected double baseRange;
    protected double baseDamage;
    protected double baseAttackSpeed;
    protected double baseRotationSpeed;
    protected double baseProjectileSpeed;

    // New Stats
    protected double baseCritChance = 0.0;
    protected double baseCritDamage = 1.5;

    protected int mkLevel;
    protected int experienceLevel;
    protected int experience;
    protected int experienceToNextLevel;

    protected double currentAngle;
    protected double targetAngle;
    protected Enemy currentTarget;
    protected double timeSinceLastShot;
    protected String targetPriority;

    protected Color baseColor;
    protected Color turretColor;

    protected double rangeSquared;

    public Tower(int row, int col, int tileSize) {
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.x = col * tileSize + tileSize / 2;
        this.y = row * tileSize + tileSize / 2;

        this.currentAngle = 0;
        this.targetAngle = 0;
        this.timeSinceLastShot = 0;
        this.targetPriority = "First";

        this.mkLevel = 0;
        this.experienceLevel = 0;
        this.experience = 0;
        this.experienceToNextLevel = 100;
    }

    // --- ABSTRACT METHOD FOR RESEARCH ---
    public abstract String getTowerID();

    protected void updateRangeSquared() {
        double globalMult = ResearchManager.getInstance().getStatMultiplier("GLOBAL_RANGE");
        double towerMult = ResearchManager.getInstance().getStatMultiplier(getTowerID() + "_RANGE");

        double finalRange = baseRange * globalMult * towerMult;

        double rangePixels = finalRange * tileSize;
        this.rangeSquared = rangePixels * rangePixels;
    }

    public void addExperience(int xp) {
        double xpMult = ResearchManager.getInstance().getStatMultiplier("GLOBAL_XP");
        int modifiedXp = (int) (xp * xpMult);

        experience += modifiedXp;

        while (experience >= experienceToNextLevel) {
            experience -= experienceToNextLevel;
            experienceLevel++;
            experienceToNextLevel = calculateXPForNextLevel();
            System.out.println(getTowerName() + " leveled up to Experience Level " + experienceLevel);
        }
    }

    private int calculateXPForNextLevel() {
        return 100 + (experienceLevel * 50);
    }

    public double getActualDamage() {
        double levelMult = 1.0 + (0.034 * experienceLevel);
        double globalResearch = ResearchManager.getInstance().getStatMultiplier("GLOBAL_DAMAGE");
        double towerResearch = ResearchManager.getInstance().getStatMultiplier(getTowerID() + "_DAMAGE");

        return baseDamage * levelMult * globalResearch * towerResearch;
    }

    public double getActualAttackSpeed() {
        double towerResearch = ResearchManager.getInstance().getStatMultiplier(getTowerID() + "_SPEED");
        return baseAttackSpeed * towerResearch;
    }

    public boolean canUpgrade() {
        return mkLevel < getMaxMKLevel();
    }

    public void upgrade() {
        if (canUpgrade()) {
            mkLevel++;
            updateStatsForMKLevel();
            updateRangeSquared();
            System.out.println(getTowerName() + " upgraded to MK." + mkLevel);
        }
    }

    public void update(double deltaTime, ArrayList<Enemy> enemies, ArrayList<Projectile> projectiles) {
        timeSinceLastShot += deltaTime;

        if (currentTarget == null || !currentTarget.isAlive() || !isInRange(currentTarget)) {
            currentTarget = findTarget(enemies);
        }

        if (currentTarget != null) {
            double dx = currentTarget.getX() - x;
            double dy = currentTarget.getY() - y;
            targetAngle = Math.toDegrees(Math.atan2(dy, dx));

            while (targetAngle < 0) targetAngle += 360;
            while (targetAngle >= 360) targetAngle -= 360;
        }

        rotateTowardTarget(deltaTime);

        if (currentTarget != null && canShoot()) {
            shoot(projectiles);
            timeSinceLastShot = 0;
        }
    }

    private boolean isInRange(Enemy enemy) {
        double dx = enemy.getX() - x;
        double dy = enemy.getY() - y;
        double distSq = dx * dx + dy * dy;
        return distSq <= rangeSquared;
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

        double shootInterval = 1.0 / getActualAttackSpeed();
        if (timeSinceLastShot < shootInterval) return false;

        double angleDiff = Math.abs(targetAngle - currentAngle);
        if (angleDiff > 180) angleDiff = 360 - angleDiff;

        return angleDiff < 5.0;
    }

    private void shoot(ArrayList<Projectile> projectiles) {
        if (currentTarget == null) return;

        double damage = getActualDamage();

        // Apply Crit if any
        double critChance = baseCritChance + (ResearchManager.getInstance().getStatMultiplier(getTowerID() + "_CRIT") - 1.0);
        if (Math.random() < critChance) {
            damage *= baseCritDamage;
        }

        Projectile projectile = new Projectile(
                x, y,
                currentTarget,
                damage,
                baseProjectileSpeed,
                turretColor,
                this
        );

        projectiles.add(projectile);
    }

    private Enemy findTarget(ArrayList<Enemy> enemies) {
        Enemy target = null;
        double targetDistSq = 0;

        if (enemies.isEmpty()) return null;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) continue;

            double dx = enemy.getX() - x;
            double dy = enemy.getY() - y;
            double maxDist = Math.sqrt(rangeSquared);
            if (Math.abs(dx) > maxDist || Math.abs(dy) > maxDist) continue;

            double distSq = dx * dx + dy * dy;
            if (distSq > rangeSquared) continue;

            if (target == null) {
                target = enemy;
                targetDistSq = distSq;
            } else {
                if (targetPriority.equals("First") && distSq < targetDistSq) {
                    target = enemy;
                    targetDistSq = distSq;
                } else if (targetPriority.equals("Strongest") && enemy.getHealth() > target.getHealth()) {
                    target = enemy;
                    targetDistSq = distSq;
                } else if (targetPriority.equals("Closest") && distSq < targetDistSq) {
                    target = enemy;
                    targetDistSq = distSq;
                }
            }
        }
        return target;
    }

    public abstract void draw(GraphicsContext gc, boolean showRange);

    protected void drawBase(GraphicsContext gc) {
        double size = tileSize * 0.7;
        gc.setFill(baseColor);
        gc.fillOval(x - size / 2, y - size / 2, size, size);
    }

    protected void drawTurret(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(currentAngle);
        gc.setFill(turretColor);
        gc.fillRect(0, -tileSize * 0.1, tileSize * 0.5, tileSize * 0.2);
        gc.restore();
    }

    public void drawRange(GraphicsContext gc) {
        double r = Math.sqrt(rangeSquared);
        gc.setStroke(Color.rgb(0, 229, 255, 0.3));
        gc.setLineWidth(2);
        gc.strokeOval(x - r, y - r, r * 2, r * 2);
    }

    // =========================================================================
    // GETTERS & SETTERS (CRITICAL FOR UI)
    // =========================================================================

    public int getRow() { return row; }
    public int getCol() { return col; }
    public double getX() { return x; }
    public double getY() { return y; }

    // These are the methods TowerPanelManager was looking for:
    public int getMKLevel() { return mkLevel; }
    public int getExperienceLevel() { return experienceLevel; }
    public int getExperience() { return experience; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }

    public double getRange() { return Math.sqrt(rangeSquared) / tileSize; } // Return range in tiles
    public double getDamage() { return getActualDamage(); }
    public double getAttackSpeed() { return getActualAttackSpeed(); }
    public double getRotationSpeed() { return baseRotationSpeed; }
    public double getProjectileSpeed() { return baseProjectileSpeed; }

    public String getTargetPriority() { return targetPriority; }
    public void setTargetPriority(String priority) { this.targetPriority = priority; }

    public abstract String getTowerName();
    public abstract int getCost();
    public abstract int getUpgradeCost();
    public abstract int getMaxMKLevel();
    protected abstract void updateStatsForMKLevel();
}