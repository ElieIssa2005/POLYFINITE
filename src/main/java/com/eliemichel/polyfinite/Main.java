package com.eliemichel.polyfinite;

import com.eliemichel.polyfinite.ui.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import com.eliemichel.polyfinite.ui.MenuScreen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("POLYFINITE");

        SplashScreen splashScreen = new SplashScreen(primaryStage);
        splashScreen.show(() -> {
            MenuScreen menuScreen = new MenuScreen(primaryStage);
            menuScreen.show();
        });

        primaryStage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}