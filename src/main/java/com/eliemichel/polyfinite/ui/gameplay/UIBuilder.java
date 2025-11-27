package com.eliemichel.polyfinite.ui.gameplay;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class UIBuilder {

    public static VBox createTowerSelectionPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(280);
        panel.setMaxWidth(280);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #00E5FF; " +
                "-fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

        panel.setTranslateX(400);
        panel.setVisible(false);
        StackPane.setAlignment(panel, Pos.CENTER_RIGHT);

        return panel;
    }

    public static HBox createTopBar(Label livesLabel, Label goldLabel, Label waveLabel, Label scoreLabel,
                                    Runnable onPause, int currentWave, int lives, int gold, int score) {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5, 15, 15, 15));
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setStyle("-fx-background-color: transparent;");
        topBar.setPickOnBounds(false);

        HBox leftStats = new HBox(15);
        leftStats.setAlignment(Pos.TOP_LEFT);

        Button pauseBtn = new Button("⏸");
        pauseBtn.setStyle("-fx-font-size: 24px; -fx-background-color: transparent; " +
                "-fx-text-fill: white; -fx-cursor: hand; -fx-padding: 0;");
        pauseBtn.setOnAction(e -> onPause.run());

        Label waveIcon = new Label("🌊");
        waveIcon.setStyle("-fx-font-size: 24px;");

        waveLabel.setText(String.valueOf(currentWave + 1));
        waveLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label healthIcon = new Label("❤️");
        healthIcon.setStyle("-fx-font-size: 24px;");

        livesLabel.setText(String.valueOf(lives));
        livesLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label moneyIcon = new Label("💵");
        moneyIcon.setStyle("-fx-font-size: 24px;");

        goldLabel.setText(String.valueOf(gold));
        goldLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #90EE90;");

        Label scoreIcon = new Label("⭐");
        scoreIcon.setStyle("-fx-font-size: 24px;");

        scoreLabel.setText(String.valueOf(score));
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        leftStats.getChildren().addAll(pauseBtn, waveIcon, waveLabel, healthIcon, livesLabel,
                moneyIcon, goldLabel, scoreIcon, scoreLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox questsBox = createQuestsBox();

        topBar.getChildren().addAll(leftStats, spacer, questsBox);

        for (javafx.scene.Node node : topBar.getChildren()) {
            if (node instanceof Button || node instanceof Label) {
                node.setMouseTransparent(false);
            }
            if (node instanceof HBox) {
                for (javafx.scene.Node child : ((HBox) node).getChildren()) {
                    child.setMouseTransparent(false);
                }
            }
        }

        return topBar;
    }

    private static VBox createQuestsBox() {
        VBox questsBox = new VBox(10);
        questsBox.setPadding(new Insets(10, 15, 10, 15));
        questsBox.setAlignment(Pos.TOP_LEFT);
        questsBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 5; " +
                "-fx-border-color: #00E5FF; -fx-border-width: 1; -fx-border-radius: 5;");
        questsBox.setPrefWidth(250);

        Label questsHeader = new Label("Quests 0/3");
        questsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label quest1 = new Label("☐ Survive 10 waves");
        quest1.setStyle("-fx-font-size: 14px; -fx-text-fill: #AAAAAA;");

        Label quest2 = new Label("☐ Don't lose any lives");
        quest2.setStyle("-fx-font-size: 14px; -fx-text-fill: #AAAAAA;");

        Label quest3 = new Label("☐ Earn 1000 points");
        quest3.setStyle("-fx-font-size: 14px; -fx-text-fill: #AAAAAA;");

        questsBox.getChildren().addAll(questsHeader, quest1, quest2, quest3);
        return questsBox;
    }

    public static HBox createBottomBar(Button startWaveButton, Runnable onStartWave, Runnable onSpeedToggle) {
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setStyle("-fx-background-color: transparent;");
        bottomBar.setPickOnBounds(false);

        startWaveButton.setText("▶ Start Wave");
        startWaveButton.setPrefHeight(50);
        startWaveButton.setPrefWidth(150);
        startWaveButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #2a2a2a; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-border-color: #00E676; -fx-border-width: 2; -fx-border-radius: 5;");

        startWaveButton.setOnAction(e -> onStartWave.run());

        startWaveButton.setOnMouseEntered(e -> {
            if (!startWaveButton.isDisabled()) {
                startWaveButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-border-color: #00E676; -fx-border-width: 3; -fx-border-radius: 5;");
            }
        });

        startWaveButton.setOnMouseExited(e -> {
            if (!startWaveButton.isDisabled()) {
                startWaveButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-background-color: #2a2a2a; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-border-color: #00E676; -fx-border-width: 2; -fx-border-radius: 5;");
            }
        });

        Button speedBtn = new Button("⏩ 2x Speed");
        speedBtn.setPrefHeight(50);
        speedBtn.setPrefWidth(150);
        speedBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #2a2a2a; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-border-color: #B388FF; -fx-border-width: 2; -fx-border-radius: 5;");

        speedBtn.setOnAction(e -> onSpeedToggle.run());

        speedBtn.setOnMouseEntered(e -> {
            speedBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                    "-fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-background-radius: 5; " +
                    "-fx-border-color: #B388FF; -fx-border-width: 3; -fx-border-radius: 5;");
        });

        speedBtn.setOnMouseExited(e -> {
            speedBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                    "-fx-background-color: #2a2a2a; -fx-text-fill: white; -fx-background-radius: 5; " +
                    "-fx-border-color: #B388FF; -fx-border-width: 2; -fx-border-radius: 5;");
        });

        bottomBar.getChildren().addAll(startWaveButton, speedBtn);

        for (javafx.scene.Node node : bottomBar.getChildren()) {
            if (node instanceof Button) {
                node.setMouseTransparent(false);
            }
        }

        return bottomBar;
    }
}