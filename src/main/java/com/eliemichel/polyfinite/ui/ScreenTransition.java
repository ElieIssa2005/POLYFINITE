package com.eliemichel.polyfinite.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ScreenTransition {

    private Stage stage;
    private Pane transitionPane;
    private Rectangle fadeOverlay;

    public ScreenTransition(Stage stage) {
        this.stage = stage;
        createTransitionOverlay();
    }

    private void createTransitionOverlay() {
        transitionPane = new Pane();
        transitionPane.setVisible(false);
        transitionPane.setMouseTransparent(true);

        double screenWidth = stage.getWidth();
        double screenHeight = stage.getHeight();

        if (screenWidth == 0 || screenHeight == 0) {
            screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
            screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        }

        fadeOverlay = new Rectangle(screenWidth, screenHeight);
        fadeOverlay.setFill(Color.BLACK);
        fadeOverlay.setOpacity(0);

        transitionPane.getChildren().add(fadeOverlay);
    }

    public void playTransition(Runnable onTransitionMiddle) {
        transitionPane.setVisible(true);
        transitionPane.toFront();

        // Fade out (to black)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), fadeOverlay);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);

        fadeOut.setOnFinished(e -> {
            // Short pause at black screen
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(event -> {

                // Execute the screen change
                if (onTransitionMiddle != null) {
                    onTransitionMiddle.run();
                }

                // Re-add transition pane to new scene
                javafx.scene.Parent root = stage.getScene().getRoot();
                if (root instanceof StackPane) {
                    ((StackPane) root).getChildren().add(transitionPane);
                    transitionPane.toFront();
                } else if (root instanceof Pane) {
                    ((Pane) root).getChildren().add(transitionPane);
                    transitionPane.toFront();
                }

                // Short pause before fade in
                PauseTransition pauseBeforeFadeIn = new PauseTransition(Duration.millis(100));
                pauseBeforeFadeIn.setOnFinished(fadeInEvent -> {

                    // Fade in (from black)
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), fadeOverlay);
                    fadeIn.setFromValue(1);
                    fadeIn.setToValue(0);

                    fadeIn.setOnFinished(finalEvent -> {
                        transitionPane.setVisible(false);
                    });

                    fadeIn.play();
                });
                pauseBeforeFadeIn.play();
            });
            pause.play();
        });

        fadeOut.play();
    }

    public Pane getTransitionPane() {
        return transitionPane;
    }
}