package com.eliemichel.polyfinite.ui.gameplay;

import com.eliemichel.polyfinite.game.QuestManager;
import com.eliemichel.polyfinite.game.towers.BasicTower;
import com.eliemichel.polyfinite.game.towers.SniperTower;
import com.eliemichel.polyfinite.game.towers.CannonTower;
import com.eliemichel.polyfinite.game.towers.FreezingTower;
import com.eliemichel.polyfinite.game.towers.Tower;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class TowerPanelManager {

    private VBox towerSelectionPanel;
    private boolean isPanelVisible = false;
    private Tower selectedTower;
    private int selectedTileRow;
    private int selectedTileCol;
    private String towerTypeToPlace;
    private String selectedTowerType;
    private int gold;
    private ArrayList<Tower> towers;
    private int tileSize;
    private QuestManager questManager;

    private Runnable onGoldUpdate;
    private Runnable onPanelHide;

    public TowerPanelManager(VBox towerSelectionPanel, ArrayList<Tower> towers, int tileSize) {
        this.towerSelectionPanel = towerSelectionPanel;
        this.towers = towers;
        this.tileSize = tileSize;
    }

    public void setOnGoldUpdate(Runnable callback) {
        this.onGoldUpdate = callback;
    }

    public void setOnPanelHide(Runnable callback) {
        this.onPanelHide = callback;
    }

    public void setQuestManager(QuestManager questManager) {
        this.questManager = questManager;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setSelectedTile(int row, int col) {
        this.selectedTileRow = row;
        this.selectedTileCol = col;
    }

    public void setSelectedTower(Tower tower) {
        this.selectedTower = tower;
    }

    public void showPanel() {
        if (isPanelVisible) {
            updatePanel();
            return;
        }

        isPanelVisible = true;
        towerSelectionPanel.setVisible(true);
        updatePanel();

        javafx.animation.TranslateTransition slideIn = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(300), towerSelectionPanel);
        slideIn.setFromX(400);
        slideIn.setToX(0);
        slideIn.play();
    }

    public void hidePanel() {
        if (!isPanelVisible) return;

        isPanelVisible = false;

        javafx.animation.TranslateTransition slideOut = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(300), towerSelectionPanel);
        slideOut.setFromX(0);
        slideOut.setToX(400);
        slideOut.setOnFinished(e -> towerSelectionPanel.setVisible(false));
        slideOut.play();

        if (onPanelHide != null) {
            onPanelHide.run();
        }
    }

    private void updatePanel() {
        towerSelectionPanel.getChildren().clear();

        if (selectedTower != null) {
            showTowerStats();
        } else {
            showBuildOptions();
        }
    }

    private void showBuildOptions() {
        Label titleLabel = new Label("BUILD TOWER");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label tileLabel = new Label("Tile: (" + selectedTileRow + ", " + selectedTileCol + ")");
        tileLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #AAAAAA;");

        towerSelectionPanel.getChildren().addAll(titleLabel, tileLabel, new Label(""));

        // All four tower types
        VBox basicTowerBox = createTowerButton("Basic Tower", 20, "Basic");
        VBox cannonTowerBox = createTowerButton("Cannon Tower", 60, "Cannon");
        VBox freezingTowerBox = createTowerButton("Freezing Tower", 80, "Freezing");
        VBox sniperTowerBox = createTowerButton("Sniper Tower", 80, "Sniper");

        towerSelectionPanel.getChildren().addAll(basicTowerBox, cannonTowerBox, freezingTowerBox, sniperTowerBox);
    }

    private void showTowerStats() {
        String titleText = selectedTower.getTowerName();
        if (selectedTower.getMKLevel() > 0) {
            titleText += " MK." + selectedTower.getMKLevel();
        }

        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #00E5FF;");

        Label xpLabel = new Label("Experience Level " + selectedTower.getExperienceLevel());
        xpLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFD700;");

        double xpProgress = (double) selectedTower.getExperience() / selectedTower.getExperienceToNextLevel();
        String xpText = selectedTower.getExperience() + " / " + selectedTower.getExperienceToNextLevel() + " XP";

        Label xpProgressLabel = new Label(xpText);
        xpProgressLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #AAAAAA;");

        HBox xpBar = new HBox();
        xpBar.setPrefHeight(8);
        xpBar.setMaxWidth(240);
        xpBar.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 4;");

        HBox xpFill = new HBox();
        xpFill.setPrefHeight(8);
        xpFill.setPrefWidth(240 * xpProgress);
        xpFill.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 4;");

        StackPane xpBarContainer = new StackPane(xpBar, xpFill);
        xpBarContainer.setAlignment(Pos.CENTER_LEFT);

        towerSelectionPanel.getChildren().addAll(titleLabel, xpLabel, xpProgressLabel, xpBarContainer, new Label(""));

        VBox statsBox = new VBox(10);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 5;");

        // Check if it's a Freezing tower (special stats display)
        if (selectedTower instanceof FreezingTower) {
            FreezingTower freezingTower = (FreezingTower) selectedTower;

            addStatRow(statsBox, "❄", "Freezing: " + (int)freezingTower.getFreezingPercent() + "%", "#4DD0E1");
            addStatRow(statsBox, "⚡", "Freeze Speed: " + freezingTower.getFreezingSpeed() + "%/sec", "#80DEEA");
            addStatRow(statsBox, "◎", "Range: " + selectedTower.getRange() + " tiles", "#6BCB77");
        } else {
            // Regular towers with damage
            double baseDmg = selectedTower.getDamage() / (1.0 + (0.034 * selectedTower.getExperienceLevel()));
            double bonus = selectedTower.getDamage() - baseDmg;
            String damageText = "Damage: " + (int)selectedTower.getDamage();
            if (bonus > 0) {
                damageText += " (+" + (int)bonus + ")";
            }

            addStatRow(statsBox, "⚔", damageText, "#FF6B6B");
            addStatRow(statsBox, "⚡", "Fire Rate: " + selectedTower.getAttackSpeed() + " /sec", "#FFD93D");
            addStatRow(statsBox, "◎", "Range: " + selectedTower.getRange() + " tiles", "#6BCB77");
            addStatRow(statsBox, "↻", "Rotation: " + (int)selectedTower.getRotationSpeed() + "°/sec", "#4ECDC4");
            addStatRow(statsBox, "➤", "Projectile Speed: " + selectedTower.getProjectileSpeed(), "#A8E6CF");
        }

        towerSelectionPanel.getChildren().add(statsBox);

        if (selectedTower.canUpgrade()) {
            towerSelectionPanel.getChildren().add(new Label(""));

            Button upgradeButton = new Button("UPGRADE TO MK." + (selectedTower.getMKLevel() + 1));
            upgradeButton.setPrefWidth(240);
            upgradeButton.setPrefHeight(40);

            int upgradeCost = selectedTower.getUpgradeCost();
            boolean canAfford = gold >= upgradeCost;

            if (canAfford) {
                upgradeButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-color: #FFD700; -fx-text-fill: black; -fx-background-radius: 5;");
            } else {
                upgradeButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-color: #3a3a3a; -fx-text-fill: #888888; -fx-background-radius: 5;");
            }

            Label costLabel = new Label("Cost: " + upgradeCost + " gold");
            costLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                    (canAfford ? "white" : "#FF6B6B") + ";");

            upgradeButton.setOnAction(e -> {
                if (gold >= upgradeCost) {
                    gold -= upgradeCost;
                    selectedTower.upgrade();
                    if (onGoldUpdate != null) {
                        onGoldUpdate.run();
                    }
                    
                    // Fire quest events for tower upgrade
                    if (questManager != null) {
                        String towerType = selectedTower.getTowerName().replace(" Tower", "");
                        questManager.onTowerUpgraded(towerType, selectedTower.getMKLevel(), upgradeCost);
                    }
                    
                    updatePanel();
                }
            });

            towerSelectionPanel.getChildren().addAll(upgradeButton, costLabel);
        } else {
            Label maxLevelLabel = new Label("MAX LEVEL");
            maxLevelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            towerSelectionPanel.getChildren().add(maxLevelLabel);
        }

        // Only show target priority for non-Freezing towers
        if (!(selectedTower instanceof FreezingTower)) {
            towerSelectionPanel.getChildren().add(new Label(""));
            Label priorityTitle = new Label("TARGET PRIORITY");
            priorityTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
            towerSelectionPanel.getChildren().add(priorityTitle);

            HBox priorityBox = new HBox(8);
            priorityBox.setAlignment(Pos.CENTER);

            String[] priorities = {"First", "Closest", "Strongest"};
            for (String priority : priorities) {
                Button priorityButton = new Button(priority);
                priorityButton.setPrefWidth(80);

                if (selectedTower.getTargetPriority().equals(priority)) {
                    priorityButton.setStyle("-fx-background-color: #00E5FF; -fx-text-fill: black; " +
                            "-fx-font-weight: bold; -fx-background-radius: 3;");
                } else {
                    priorityButton.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white; " +
                            "-fx-background-radius: 3;");
                }

                priorityButton.setOnAction(e -> {
                    selectedTower.setTargetPriority(priority);
                    updatePanel();
                });

                priorityBox.getChildren().add(priorityButton);
            }

            towerSelectionPanel.getChildren().add(priorityBox);
        }
    }

    private void addStatRow(VBox container, String icon, String text, String iconColor) {
        HBox statBox = new HBox(10);
        statBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + iconColor + ";");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        statBox.getChildren().addAll(iconLabel, textLabel);
        container.getChildren().add(statBox);
    }

    private VBox createTowerButton(String name, int cost, String type) {
        VBox towerBox = new VBox(8);
        towerBox.setPadding(new Insets(12));
        towerBox.setAlignment(Pos.CENTER);

        boolean isSelected = type.equals(towerTypeToPlace);
        boolean canAfford = gold >= cost;

        if (isSelected && selectedTowerType != null && selectedTowerType.equals("preview")) {
            towerBox.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 5; " +
                    "-fx-border-color: #FFA500; -fx-border-width: 2; -fx-cursor: hand;");
        } else if (isSelected && selectedTowerType != null && selectedTowerType.equals("confirmed")) {
            towerBox.setStyle("-fx-background-color: #00E676; -fx-background-radius: 5; " +
                    "-fx-border-color: #00C853; -fx-border-width: 2; -fx-cursor: hand;");
        } else if (!canAfford) {
            towerBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 5; " +
                    "-fx-border-color: #FF6B6B; -fx-border-width: 1; -fx-cursor: hand; -fx-opacity: 0.5;");
        } else {
            towerBox.setStyle("-fx-background-color: #3a3a3a; -fx-background-radius: 5; " +
                    "-fx-border-color: #00E5FF; -fx-border-width: 1; -fx-cursor: hand;");
        }

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label costLabel = new Label(cost + " gold");
        costLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (canAfford ? "#FFD700" : "#FF6B6B") + ";");

        towerBox.getChildren().addAll(nameLabel, costLabel);

        towerBox.setOnMouseClicked(e -> {
            handleTowerButtonClick(type, cost);
            e.consume();
        });

        return towerBox;
    }

    private void handleTowerButtonClick(String type, int cost) {
        if (towerTypeToPlace == null || !towerTypeToPlace.equals(type)) {
            towerTypeToPlace = type;
            selectedTowerType = "preview";
            updatePanel();
            System.out.println("Preview mode: " + type);
        } else if (selectedTowerType.equals("preview")) {
            selectedTowerType = "confirmed";

            if (gold >= cost) {
                gold -= cost;
                if (onGoldUpdate != null) {
                    onGoldUpdate.run();
                }

                Tower tower = null;
                if (type.equals("Basic")) {
                    tower = new BasicTower(selectedTileRow, selectedTileCol, tileSize);
                } else if (type.equals("Sniper")) {
                    tower = new SniperTower(selectedTileRow, selectedTileCol, tileSize);
                } else if (type.equals("Cannon")) {
                    tower = new CannonTower(selectedTileRow, selectedTileCol, tileSize);
                } else if (type.equals("Freezing")) {
                    tower = new FreezingTower(selectedTileRow, selectedTileCol, tileSize);
                }

                if (tower != null) {
                    towers.add(tower);
                    System.out.println("Tower placed at (" + selectedTileRow + ", " + selectedTileCol + ")");
                    
                    // Fire quest events for tower built
                    if (questManager != null) {
                        questManager.onTowerBuilt(type, cost);
                    }
                }

                hidePanel();
                resetSelection();
            } else {
                System.out.println("Not enough gold! Need " + cost + ", have " + gold);
            }
        }
    }

    public void resetSelection() {
        selectedTileRow = -1;
        selectedTileCol = -1;
        towerTypeToPlace = null;
        selectedTowerType = null;
        selectedTower = null;
    }

    public String getTowerTypeToPlace() {
        return towerTypeToPlace;
    }

    public int getGold() {
        return gold;
    }
}