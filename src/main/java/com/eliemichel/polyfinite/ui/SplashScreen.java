package com.eliemichel.polyfinite.ui;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen {
    private Stage stage;

    public SplashScreen(Stage stage) {
        this.stage = stage;
    }

    public void show(Runnable onComplete) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        // EM Games logo
        Image emLogo = new Image(getClass().getResourceAsStream("/E-M-Games-logo.jpg"));
        ImageView emLogoView = new ImageView(emLogo);
        emLogoView.setPreserveRatio(true);
        emLogoView.setFitWidth(400);
        emLogoView.setOpacity(0);

        // POLYFINITE Main logo
        Image mainLogo = new Image(getClass().getResourceAsStream("/POLYFINITE-Main.png"));
        ImageView mainLogoView = new ImageView(mainLogo);
        mainLogoView.setPreserveRatio(true);
        mainLogoView.setFitWidth(500);
        mainLogoView.setOpacity(0);

        root.getChildren().addAll(emLogoView, mainLogoView);
        StackPane.setAlignment(emLogoView, Pos.CENTER);
        StackPane.setAlignment(mainLogoView, Pos.CENTER);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Phase 1: EM Games logo fade in, pause, fade out
        FadeTransition emFadeIn = new FadeTransition(Duration.seconds(1.5), emLogoView);
        emFadeIn.setFromValue(0);
        emFadeIn.setToValue(1);

        PauseTransition emPause = new PauseTransition(Duration.seconds(1));

        FadeTransition emFadeOut = new FadeTransition(Duration.seconds(1.5), emLogoView);
        emFadeOut.setFromValue(1);
        emFadeOut.setToValue(0);

        // Phase 2: POLYFINITE Main logo fade in, pause, then fade out (stay on black background)
        FadeTransition mainFadeIn = new FadeTransition(Duration.seconds(1.2), mainLogoView);
        mainFadeIn.setFromValue(0);
        mainFadeIn.setToValue(1);

        PauseTransition mainPause = new PauseTransition(Duration.seconds(1.5));

        // Fade out only the logo, keep black background
        FadeTransition mainFadeOut = new FadeTransition(Duration.seconds(1.2), mainLogoView);
        mainFadeOut.setFromValue(1);
        mainFadeOut.setToValue(0);

        // Chain all animations
        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(
                emFadeIn,
                emPause,
                emFadeOut,
                mainFadeIn,
                mainPause,
                mainFadeOut
        );

        sequence.setOnFinished(e -> onComplete.run());
        sequence.play();
    }
}