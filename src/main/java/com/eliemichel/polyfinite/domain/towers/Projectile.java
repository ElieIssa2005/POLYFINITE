package com.eliemichel.polyfinite.domain.towers;

import com.eliemichel.polyfinite.domain.towers.types.Tower;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Projectile {

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

    // OPTIMIZATION: Pre-calculate speed squared threshold
    private double speedSquared;

    public Projectile(double x, double y, Enemy target, double damage, double speed, Color color, Tower sourceTower) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
        this.speed = speed;
        this.color = color;
        this.active = true;
        this.sourceTower = sourceTower;

        // OPTIMIZATION: Pre-calculate squared speed for distance comparison
        this.speedSquared = speed * speed;

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        this.angle = Math.toDegrees(Math.atan2(dy, dx));

        try {
            sprite = new Image(getClass().getResourceAsStream("/sprites/projectiles/basic_projectile.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    public void update() {
        if (!active || target == null || !target.isAlive()) {
            active = false;
            return;
        }

        double targetX = target.getX();
        double targetY = target.getY();

        // OPTIMIZATION: Calculate dx, dy once
        double dx = targetX - x;
        double dy = targetY - y;

        // OPTIMIZATION: Use squared distance comparison (no sqrt)
        double distSq = dx * dx + dy * dy;

        // Update angle
        angle = Math.toDegrees(Math.atan2(dy, dx));

        // OPTIMIZATION: Compare squared distances (avoid sqrt)
        if (distSq < speedSquared) {
            // Hit the target
            int healthBefore = target.getHealth();
            
            // Pass tower type for quest tracking
            String towerType = sourceTower != null ? sourceTower.getTowerName().replace(" Tower", "") : null;
            target.takeDamage((int) damage, towerType);

            if (!target.isAlive() && healthBefore > 0 && sourceTower != null) {
                sourceTower.addExperience(10);
            }

            active = false;
        } else {
            // OPTIMIZATION: Only calculate sqrt when actually moving
            double distance = Math.sqrt(distSq);

            // Calculate movement once
            double moveX = (dx / distance) * speed;
            double moveY = (dy / distance) * speed;

            x += moveX;
            y += moveY;
        }
    }

    public void draw(GraphicsContext gc) {
        if (!active) return;

        if (sprite != null) {
            double scale = 0.6;
            double width = sprite.getWidth() * scale;
            double height = sprite.getHeight() * scale;

            gc.save();
            gc.translate(x, y);
            gc.rotate(angle);

            gc.drawImage(sprite,
                    -width / 2,
                    -height / 2,
                    width,
                    height);

            gc.restore();
        } else {
            gc.setFill(color);
            gc.fillOval(x - 4, y - 4, 8, 8);

            gc.setEffect(new javafx.scene.effect.DropShadow(
                    javafx.scene.effect.BlurType.GAUSSIAN,
                    color,
                    6,
                    0.7,
                    0,
                    0
            ));
            gc.fillOval(x - 3, y - 3, 6, 6);
            gc.setEffect(null);
        }
    }

    public boolean isActive() {
        return active;
    }
}