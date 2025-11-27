package com.eliemichel.polyfinite.ui;

import com.eliemichel.polyfinite.game.SaveSlot;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EndLevelScreen {
    private final Stage stage;
    private final SaveSlot currentSave;
    private final int finalWave;
    private final int totalWaves;
    private final int finalScore;
    private StackPane overlay;

    public EndLevelScreen(Stage stage, SaveSlot currentSave, int finalWave, int totalWaves, int finalScore) {
        this.stage = stage;
        this.currentSave = currentSave;
        this.finalWave = finalWave;
        this.totalWaves = totalWaves;
        this.finalScore = finalScore;
        createOverlay();
    }

    private void createOverlay() {
        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(500);

        Label titleLabel = new Label("BASE DESTROYED");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #FF4444;");

        Label waveLabel = new Label("Wave Reached: " + finalWave + " / " + totalWaves);
        waveLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        Label scoreLabel = new Label("Final Score: " + finalScore);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        Button returnButton = new Button("RETURN TO LEVEL SELECT");
        returnButton.setPrefWidth(250);
        returnButton.setPrefHeight(50);
        returnButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: white; -fx-text-fill: black; " +
                "-fx-background-radius: 5;");

        returnButton.setOnAction(e -> {
            MapScreen mapScreen = new MapScreen(stage, currentSave);
            mapScreen.show();
        });

        contentBox.getChildren().addAll(titleLabel, waveLabel, scoreLabel, returnButton);
        overlay.getChildren().add(contentBox);
    }

    public StackPane getOverlay() {
        return overlay;
    }
}