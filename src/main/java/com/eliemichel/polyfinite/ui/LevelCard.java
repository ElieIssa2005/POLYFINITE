package com.eliemichel.polyfinite.ui;

import com.eliemichel.polyfinite.game.SaveSlot;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.eliemichel.polyfinite.game.LevelInfo;
import com.eliemichel.polyfinite.game.Quest;

public class LevelCard {

    private VBox card;
    private LevelInfo levelInfo;
    private SaveSlot currentSave;

    public LevelCard(LevelInfo levelInfo, SaveSlot currentSave) {
        this.levelInfo = levelInfo;
        this.currentSave = currentSave;
        this.card = createCard();
    }

    private VBox createCard() {
        VBox cardBox = new VBox(8);
        cardBox.setPrefWidth(300);
        cardBox.setPrefHeight(400);
        cardBox.setPadding(new Insets(15));
        cardBox.setStyle("-fx-background-color: #1a1a1a; " +
                "-fx-border-color: #00E5FF; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 3; " +
                "-fx-background-radius: 3; " +
                "-fx-effect: dropshadow(gaussian, #00E5FF, 6, 0.4, 0, 0);");

        if (!levelInfo.isUnlocked()) {
            cardBox.setStyle("-fx-background-color: #0a0a0a; " +
                    "-fx-border-color: #333333; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 3; " +
                    "-fx-background-radius: 3;");

            Label lockedLabel = new Label("🔒 LOCKED");
            lockedLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #444444;");

            VBox lockedBox = new VBox(lockedLabel);
            lockedBox.setAlignment(Pos.CENTER);
            lockedBox.setPrefHeight(400);
            cardBox.getChildren().add(lockedBox);

            return cardBox;
        }

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label levelLabel = new Label("LEVEL " + levelInfo.getLevelNumber() + ": " + levelInfo.getLevelName().toUpperCase());
        levelLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #00E5FF;");

        HBox starsBox = createStarsDisplay();

        HBox.setHgrow(levelLabel, javafx.scene.layout.Priority.ALWAYS);
        headerBox.getChildren().addAll(levelLabel, starsBox);

        VBox recordsBox = new VBox(5);
        recordsBox.setPadding(new Insets(5, 0, 5, 0));
        recordsBox.setStyle("-fx-background-color: #0a0a0a; -fx-background-radius: 3; -fx-padding: 8; " +
                "-fx-border-color: #00E5FF; -fx-border-width: 1; -fx-border-radius: 3;");

        Label recordsTitle = new Label("📊 PERSONAL RECORDS");
        recordsTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #00E5FF;");

        Label waveLabel = new Label("Best Wave: " + levelInfo.getBestWave());
        waveLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #AAAAAA;");

        Label scoreLabel = new Label("High Score: " + levelInfo.getHighScore());
        scoreLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #AAAAAA;");

        recordsBox.getChildren().addAll(recordsTitle, waveLabel, scoreLabel);

        HBox enemyBox = new HBox(8);
        enemyBox.setAlignment(Pos.CENTER_LEFT);
        enemyBox.setPadding(new Insets(5, 0, 5, 0));

        Label enemyLabel = new Label("👾 Enemies:");
        enemyLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #B388FF;");

        HBox enemyIcons = new HBox(5);
        for (String enemy : levelInfo.getEnemyTypes()) {
            Label icon = new Label(getEnemyIcon(enemy));
            icon.setStyle("-fx-font-size: 16px;");
            enemyIcons.getChildren().add(icon);
        }

        enemyBox.getChildren().addAll(enemyLabel, enemyIcons);

        VBox milestonesBox = new VBox(5);
        milestonesBox.setPadding(new Insets(5, 0, 5, 0));

        Label milestonesTitle = new Label("📈 WAVE MILESTONES");
        milestonesTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #B388FF;");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(270);
        progressBar.setProgress((double) levelInfo.getMilestonesCompleted() / levelInfo.getWaveMilestones().length);
        progressBar.setStyle("-fx-accent: #00E676;");

        HBox milestonesLabels = new HBox(10);
        for (int milestone : levelInfo.getWaveMilestones()) {
            Label ml = new Label("W" + milestone);
            ml.setStyle("-fx-font-size: 9px; -fx-text-fill: #666666;");
            milestonesLabels.getChildren().add(ml);
        }

        milestonesBox.getChildren().addAll(milestonesTitle, progressBar, milestonesLabels);

        VBox questsBox = new VBox(3);
        questsBox.setPadding(new Insets(5, 0, 5, 0));
        questsBox.setStyle("-fx-background-color: #0a0a0a; -fx-background-radius: 3; -fx-padding: 8; " +
                "-fx-border-color: #B388FF; -fx-border-width: 1; -fx-border-radius: 3;");

        Label questsTitle = new Label("🎯 QUESTS (" + getCompletedQuestsCount() + "/" + levelInfo.getQuests().size() + ")");
        questsTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #B388FF;");

        questsBox.getChildren().add(questsTitle);

        for (Quest quest : levelInfo.getQuests()) {
            String icon = quest.isCompleted() ? "✅" : "❌";
            Label questLabel = new Label(icon + " " + quest.getDescription());
            questLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #AAAAAA;");
            questsBox.getChildren().add(questLabel);
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button wavesButton = new Button("▶️ WAVES");
        wavesButton.setPrefWidth(120);
        wavesButton.setPrefHeight(35);
        wavesButton.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E5FF; -fx-text-fill: black; " +
                "-fx-background-radius: 3; " +
                "-fx-effect: dropshadow(gaussian, #00E5FF, 6, 0.6, 0, 0);");

        wavesButton.setOnAction(e -> {
            System.out.println("Show waves for Level " + levelInfo.getLevelNumber());
        });

        Button playButton = new Button("🎮 PLAY");
        playButton.setPrefWidth(120);
        playButton.setPrefHeight(35);
        playButton.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E676; -fx-text-fill: black; " +
                "-fx-background-radius: 3; " +
                "-fx-effect: dropshadow(gaussian, #00E676, 6, 0.6, 0, 0);");

        playButton.setOnAction(e -> {
            System.out.println("Loading Level " + levelInfo.getLevelNumber());

            javafx.stage.Stage stage = (javafx.stage.Stage) playButton.getScene().getWindow();

            javafx.scene.Parent root = stage.getScene().getRoot();
            ScreenTransition transition = new ScreenTransition(stage);

            if (root instanceof javafx.scene.layout.Pane) {
                ((javafx.scene.layout.Pane) root).getChildren().add(transition.getTransitionPane());
            }

            transition.playTransition(() -> {
                GameplayScreen gameplayScreen = new GameplayScreen(stage, levelInfo, currentSave);
                gameplayScreen.show();
            });
        });

        buttonBox.getChildren().addAll(wavesButton, playButton);

        cardBox.getChildren().addAll(
                headerBox,
                recordsBox,
                enemyBox,
                milestonesBox,
                questsBox,
                buttonBox
        );

        return cardBox;
    }

    private HBox createStarsDisplay() {
        HBox starsBox = new HBox(3);
        starsBox.setAlignment(Pos.CENTER_RIGHT);

        for (int i = 0; i < levelInfo.getMaxStars(); i++) {
            Label star = new Label(i < levelInfo.getStarsEarned() ? "⭐" : "☆");
            star.setStyle("-fx-font-size: 14px; " +
                    (i < levelInfo.getStarsEarned() ? "-fx-effect: dropshadow(gaussian, #FFD700, 5, 0.8, 0, 0);" : ""));
            starsBox.getChildren().add(star);
        }

        return starsBox;
    }

    private int getCompletedQuestsCount() {
        int count = 0;
        for (Quest quest : levelInfo.getQuests()) {
            if (quest.isCompleted()) count++;
        }
        return count;
    }

    private String getEnemyIcon(String enemyType) {
        switch (enemyType.toLowerCase()) {
            case "regular": return "🟩";
            case "fast": return "🔵";
            case "strong": return "🔴";
            case "armored": return "⬛";
            case "toxic": return "🟣";
            case "icy": return "💎";
            case "fighter": return "🔶";
            default: return "⬜";
        }
    }

    public VBox getCard() {
        return card;
    }
}