package com.eliemichel.polyfinite.ui;

import com.eliemichel.polyfinite.game.SaveSlot;
import com.eliemichel.polyfinite.game.PlayerCurrencies;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.eliemichel.polyfinite.game.ResearchManager;

public class SaveSelectionMenu {

    private Stage stage;
    private SaveSlot[] saveSlots;
    private VBox menuBox;

    public SaveSelectionMenu(Stage stage) {
        this.stage = stage;
        this.saveSlots = new SaveSlot[3];

        for (int i = 0; i < 3; i++) {
            saveSlots[i] = new SaveSlot(i + 1);
            saveSlots[i].loadFromDatabase();
        }

        createMenu();
    }

    private void createMenu() {
        menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(20));

        // Standard dark gray rectangle, no glowy colors
        menuBox.setStyle("-fx-background-color: #2a2a2a; " +
                "-fx-background-radius: 3; " +
                "-fx-border-color: #555555; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 3;");

        menuBox.setPrefWidth(300);
        menuBox.setPrefHeight(250);
        menuBox.setMaxWidth(300);
        menuBox.setMaxHeight(250);

        // Title with Roboto Light
        Label titleLabel = new Label("SELECT SAVE SLOT");
        titleLabel.setFont(Font.font("Roboto Light", 16));
        titleLabel.setStyle("-fx-text-fill: white;");
        menuBox.getChildren().add(titleLabel);

        // Create buttons for each save slot
        for (int i = 0; i < 3; i++) {
            Button slotButton = createSaveSlotButton(saveSlots[i]);
            menuBox.getChildren().add(slotButton);
        }
    }

    private Button createSaveSlotButton(SaveSlot saveSlot) {
        Button button = new Button();
        button.setPrefWidth(260);
        button.setPrefHeight(45);

        if (saveSlot.isEmpty()) {
            // Empty slot: black/gray background
            button.setText("SLOT " + saveSlot.getSlotNumber() + " - NEW GAME");
            button.setFont(Font.font("Roboto Light", 14));
            button.setStyle("-fx-background-color: #1a1a1a; " +
                    "-fx-text-fill: #888888; " +
                    "-fx-background-radius: 3; " +
                    "-fx-border-color: #444444; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 3;");
        } else {
            // Occupied slot: white background with level and stars info
            button.setText("SLOT " + saveSlot.getSlotNumber() +
                    " - Lvl " + saveSlot.getCurrentLevel() +
                    " | ⭐" + saveSlot.getTotalStars());
            button.setFont(Font.font("Roboto", 14));
            button.setStyle("-fx-background-color: #ffffff; " +
                    "-fx-text-fill: #000000; " +
                    "-fx-background-radius: 3; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 3;");
        }

        button.setOnAction(e -> {
            ResearchManager.getInstance().loadResearch(saveSlot.getSlotNumber());
            PlayerCurrencies.getInstance().loadCurrencies(saveSlot.getSlotNumber());

            if (saveSlot.isEmpty()) {
                saveSlot.startNewGame();
                System.out.println("Starting new game in slot " + saveSlot.getSlotNumber());
            } else {
                System.out.println("Loading save slot " + saveSlot.getSlotNumber());
            }

            ScreenTransition transition = new ScreenTransition(stage);

            Pane root = (Pane) stage.getScene().getRoot();
            root.getChildren().add(transition.getTransitionPane());

            transition.playTransition(() -> {
                MapScreen mapScreen = new MapScreen(stage, saveSlot);
                mapScreen.show();
            });
        });

        // Hover effects
        button.setOnMouseEntered(e -> {
            if (saveSlot.isEmpty()) {
                button.setStyle("-fx-background-color: #2a2a2a; " +
                        "-fx-text-fill: #aaaaaa; " +
                        "-fx-background-radius: 3; " +
                        "-fx-border-color: #666666; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 3;");
            } else {
                button.setStyle("-fx-background-color: #f5f5f5; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-background-radius: 3; " +
                        "-fx-border-color: #999999; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 3;");
            }
        });

        button.setOnMouseExited(e -> {
            if (saveSlot.isEmpty()) {
                button.setStyle("-fx-background-color: #1a1a1a; " +
                        "-fx-text-fill: #888888; " +
                        "-fx-background-radius: 3; " +
                        "-fx-border-color: #444444; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 3;");
            } else {
                button.setStyle("-fx-background-color: #ffffff; " +
                        "-fx-text-fill: #000000; " +
                        "-fx-background-radius: 3; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 3;");
            }
        });

        return button;
    }

    public void show() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #000000;");

        javafx.scene.layout.StackPane centerStack = new javafx.scene.layout.StackPane();
        centerStack.getChildren().add(menuBox);
        centerStack.setAlignment(Pos.CENTER);

        borderPane.setCenter(centerStack);

        Scene scene = new Scene(borderPane, 1280, 720);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public VBox getMenuBox() {
        return menuBox;
    }
}