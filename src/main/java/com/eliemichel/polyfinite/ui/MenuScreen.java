package com.eliemichel.polyfinite.ui;

import com.eliemichel.polyfinite.utils.AtlasManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuScreen {

    private Stage stage;
    private SequentialTransition logoCycleAnimation;
    private static MediaPlayer videoPlayer;
    private static Font robotoLight;
    private static Font robotoRegular;

    public MenuScreen(Stage stage) {
        this.stage = stage;
        loadFonts();
        setupVideoPlayer();
    }

    private void loadFonts() {
        if (robotoLight == null) {
            try {
                robotoLight = Font.loadFont("https://fonts.googleapis.com/css2?family=Roboto:wght@300&display=swap", 16);
                if (robotoLight == null) {
                    robotoLight = Font.font("Roboto Light", 16);
                }
            } catch (Exception e) {
                robotoLight = Font.font("Arial", 16);
            }
        }

        if (robotoRegular == null) {
            try {
                robotoRegular = Font.loadFont("https://fonts.googleapis.com/css2?family=Roboto:wght@400&display=swap", 16);
                if (robotoRegular == null) {
                    robotoRegular = Font.font("Roboto", 16);
                }
            } catch (Exception e) {
                robotoRegular = Font.font("Arial", 16);
            }
        }
    }

    private void setupVideoPlayer() {
        if (videoPlayer == null) {
            try {
                String videoPath = getClass().getResource("/Simple_Loading_Screen_Video_Ready.mp4").toExternalForm();
                Media media = new Media(videoPath);
                videoPlayer = new MediaPlayer(media);
                videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            } catch (Exception e) {
                System.err.println("Error loading video: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void show() {
        StackPane root = new StackPane();

        // Video background layer
        MediaView mediaView = new MediaView(videoPlayer);
        mediaView.setPreserveRatio(false);

        // Bind video size to stage size
        mediaView.fitWidthProperty().bind(stage.widthProperty());
        mediaView.fitHeightProperty().bind(stage.heightProperty());

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: rgba(26, 26, 26, 0.7);");

        // Center: Cycling logos + Play button
        VBox centerContent = createCenterContent();

        // Save menu overlay (positioned on top)
        VBox saveMenuOverlay = createSaveMenuOverlay();

        // Use StackPane to overlay save menu on top of center content
        StackPane centerStack = new StackPane();
        centerStack.getChildren().addAll(centerContent, saveMenuOverlay);

        borderPane.setCenter(centerStack);

        // Get the play button from centerContent and add click handler
        VBox logoAndButtonBox = (VBox) centerContent.getChildren().get(0);
        Button playButton = (Button) logoAndButtonBox.getChildren().get(1);
        playButton.setOnAction(e -> {
            // Toggle save menu visibility
            boolean willBeVisible = !saveMenuOverlay.isVisible();
            saveMenuOverlay.setVisible(willBeVisible);

            // Pause video when save menu opens, resume when it closes
            if (willBeVisible) {
                pauseVideo();
            } else {
                resumeVideo();
            }
        });

        // Stack layers: video at bottom, UI on top
        root.getChildren().addAll(mediaView, borderPane);

        // Set the scene background to black
        stage.getScene().setFill(javafx.scene.paint.Color.BLACK);

        // Set opacity to 0 initially for fade-in effect
        root.setOpacity(0);

        // Reuse the existing scene
        stage.getScene().setRoot(root);
        stage.setFullScreen(true);

        // Start playing the video
        videoPlayer.play();

        // Fade in the new content from black
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private VBox createCenterContent() {
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);

        // Create logo container with both logos stacked
        StackPane logoContainer = new StackPane();
        logoContainer.setAlignment(Pos.CENTER);

        // Load both logos
        Image logo1 = new Image(getClass().getResourceAsStream("/POLYFINITE-NBG.png"));
        ImageView logoView1 = new ImageView(logo1);
        logoView1.setPreserveRatio(true);
        logoView1.setFitWidth(450);
        logoView1.setOpacity(1);

        Image logo2 = new Image(getClass().getResourceAsStream("/POLYFINITE-2-NBG.png"));
        ImageView logoView2 = new ImageView(logo2);
        logoView2.setPreserveRatio(true);
        logoView2.setFitWidth(450);
        logoView2.setOpacity(0);

        logoContainer.getChildren().addAll(logoView1, logoView2);

        // Start the cycling animation
        startLogoCycleAnimation(logoView1, logoView2);

        // Play button
        Button playButton = createPlayButton();

        // Put logo and button in a VBox
        VBox logoAndButton = new VBox(50);
        logoAndButton.setAlignment(Pos.CENTER);
        logoAndButton.getChildren().addAll(logoContainer, playButton);

        centerBox.getChildren().add(logoAndButton);
        return centerBox;
    }

    private void startLogoCycleAnimation(ImageView logo1, ImageView logo2) {
        logoCycleAnimation = new SequentialTransition();

        PauseTransition logo1Stay = new PauseTransition(Duration.seconds(2));

        FadeTransition logo1FadeOut = new FadeTransition(Duration.seconds(1.5), logo1);
        logo1FadeOut.setFromValue(1);
        logo1FadeOut.setToValue(0);

        FadeTransition logo2FadeIn = new FadeTransition(Duration.seconds(1.5), logo2);
        logo2FadeIn.setFromValue(0);
        logo2FadeIn.setToValue(1);

        ParallelTransition crossfade1 = new ParallelTransition(logo1FadeOut, logo2FadeIn);

        PauseTransition logo2Stay = new PauseTransition(Duration.seconds(2));

        FadeTransition logo2FadeOut = new FadeTransition(Duration.seconds(1.5), logo2);
        logo2FadeOut.setFromValue(1);
        logo2FadeOut.setToValue(0);

        FadeTransition logo1FadeIn = new FadeTransition(Duration.seconds(1.5), logo1);
        logo1FadeIn.setFromValue(0);
        logo1FadeIn.setToValue(1);

        ParallelTransition crossfade2 = new ParallelTransition(logo2FadeOut, logo1FadeIn);

        logoCycleAnimation.getChildren().addAll(
                logo1Stay,
                crossfade1,
                logo2Stay,
                crossfade2
        );

        logoCycleAnimation.setCycleCount(SequentialTransition.INDEFINITE);
        logoCycleAnimation.play();
    }

    private VBox createSaveMenuOverlay() {
        SaveSelectionMenu saveMenu = new SaveSelectionMenu(stage);
        VBox saveMenuBox = saveMenu.getMenuBox();
        saveMenuBox.setVisible(false);

        saveMenuBox.setTranslateY(280);

        return saveMenuBox;
    }

    private Button createPlayButton() {
        Image buttonImage = AtlasManager.getInstance()
                .getAtlas()
                .getRegion("ui-dialog-background-3");

        ImageView buttonImageView = new ImageView(buttonImage);
        buttonImageView.setFitWidth(220);
        buttonImageView.setFitHeight(64);
        buttonImageView.setPreserveRatio(true);

        // Use icon-triangle-right instead of text
        Image triangleIcon = AtlasManager.getInstance()
                .getAtlas()
                .getRegion("icon-triangle-right");

        ImageView triangleView = new ImageView(triangleIcon);
        triangleView.setFitWidth(32);
        triangleView.setFitHeight(32);
        triangleView.setPreserveRatio(true);

        StackPane buttonContent = new StackPane();
        buttonContent.getChildren().addAll(buttonImageView, triangleView);
        StackPane.setAlignment(triangleView, Pos.CENTER);

        Button playButton = new Button();
        playButton.setGraphic(buttonContent);
        playButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        playButton.setOnMouseEntered(e -> {
            buttonContent.setScaleX(1.05);
            buttonContent.setScaleY(1.05);
        });
        playButton.setOnMouseExited(e -> {
            buttonContent.setScaleX(1.0);
            buttonContent.setScaleY(1.0);
        });

        return playButton;
    }

    public void stopLogoAnimation() {
        if (logoCycleAnimation != null) {
            logoCycleAnimation.stop();
        }
    }

    public void pauseVideo() {
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    public void resumeVideo() {
        if (videoPlayer != null) {
            videoPlayer.play();
        }
    }

    public static Font getRobotoLight() {
        return robotoLight;
    }

    public static Font getRobotoRegular() {
        return robotoRegular;
    }
}