package com.eliemichel.polyfinite.domain.towers;

import com.eliemichel.polyfinite.domain.towers.types.Tower;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ExplosiveProjectile {

    private double x;
    private double y;
    private Enemy target;
    private double damage;
    private double speed;
    private Color color;
    private boolean active;
    private double angle;
    private Image sprite;
    private Tower sourceTower;
    private double explosionRange;

    public ExplosiveProjectile(double x, double y, Enemy target, double damage, double speed,
                               Color color, Tower sourceTower, double explosionRange) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
        this.speed = speed;
        this.color = color;
        this.active = true;
        this.sourceTower = sourceTower;
        this.explosionRange = explosionRange;

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        this.angle = Math.toDegrees(Math.atan2(dy, dx));

        try {
            sprite = new Image(getClass().getResourceAsStream("/sprites/projectiles/cannon_projectile.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    public void update(ArrayList<Enemy> allEnemies) {
        if (!active || target == null || !target.isAlive()) {
            active = false;
            return;
        }

        double targetX = target.getX();
        double targetY = target.getY();

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        angle = Math.toDegrees(Math.atan2(dy, dx));

        if (distance < speed) {
            explode(allEnemies);
            active = false;
        } else {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    private void explode(ArrayList<Enemy> allEnemies) {
        double impactX = target.getX();
        double impactY = target.getY();

        // Arc system: 20 arcs of 18 degrees each
        double[] arcDamage = new double[20];
        for (int i = 0; i < 20; i++) {
            arcDamage[i] = damage;
        }

        // Process each enemy in explosion range
        for (Enemy enemy : allEnemies) {
            if (!enemy.isAlive()) continue;

            double dx = enemy.getX() - impactX;
            double dy = enemy.getY() - impactY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > explosionRange * 40) continue; // Convert to pixels

            // Calculate which arc this enemy is in
            double angleToEnemy = Math.toDegrees(Math.atan2(dy, dx));
            while (angleToEnemy < 0) angleToEnemy += 360;
            int arcIndex = (int) (angleToEnemy / 18) % 20;

            // Distance multiplier: 1 - distance * 0.8 / explosion radius
            double distanceMultiplier = 1.0 - (distance * 0.8 / (explosionRange * 40));
            distanceMultiplier = Math.max(0, distanceMultiplier);

            // Calculate damage for this enemy
            double finalDamage = arcDamage[arcIndex] * distanceMultiplier;

            if (finalDamage >= damage * 0.05) {
                int healthBefore = enemy.getHealth();
                enemy.takeDamage((int) finalDamage);

                // Award XP if kill
                if (!enemy.isAlive() && healthBefore > 0 && sourceTower != null) {
                    sourceTower.addExperience(10);
                }

                // Reduce arc damage (66% for current arc, 83% for adjacent)
                arcDamage[arcIndex] *= 0.66;
                int leftArc = (arcIndex - 1 + 20) % 20;
                int rightArc = (arcIndex + 1) % 20;
                arcDamage[leftArc] *= 0.83;
                arcDamage[rightArc] *= 0.83;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        if (!active) return;

        if (sprite != null) {
            double scale = 0.7;
            double width = sprite.getWidth() * scale;
            double height = sprite.getHeight() * scale;

            gc.save();
            gc.translate(x, y);
            gc.rotate(angle);

            gc.drawImage(sprite, -width / 2, -height / 2, width, height);

            gc.restore();
        } else {
            gc.setFill(Color.rgb(255, 140, 0));
            gc.fillOval(x - 5, y - 5, 10, 10);

            gc.setEffect(new javafx.scene.effect.DropShadow(
                    javafx.scene.effect.BlurType.GAUSSIAN,
                    Color.rgb(255, 100, 0),
                    8, 0.8, 0, 0
            ));
            gc.fillOval(x - 4, y - 4, 8, 8);
            gc.setEffect(null);
        }
    }

    public boolean isActive() {
        return active;
    }
}