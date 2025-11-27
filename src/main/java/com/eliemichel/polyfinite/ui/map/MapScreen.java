package com.eliemichel.polyfinite.ui.map;

import com.eliemichel.polyfinite.database.DBConnectMySQL;
import com.eliemichel.polyfinite.domain.player.SaveSlot;
import com.eliemichel.polyfinite.domain.level.WaveMilestone;
import com.eliemichel.polyfinite.utils.AtlasManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.eliemichel.polyfinite.screens.ResearchScreen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class MapScreen {

    private Stage stage;
    private SaveSlot currentSave;
    private ArrayList<ChapterInfo> chapters;
    private HashMap<Integer, LevelProgressData> levelProgressMap;

    public MapScreen(Stage stage, SaveSlot currentSave) {
        this.stage = stage;
        this.currentSave = currentSave;
        this.chapters = new ArrayList<>();
        this.levelProgressMap = new HashMap<>();

        loadChaptersAndLevels();
        loadLevelProgress();
    }

    private void loadChaptersAndLevels() {
        ArrayList<ChapterData> chapterDataList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("chapters.txt"));
            String line;
            ChapterData currentChapter = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CHAPTER:")) {
                    int chapterNum = Integer.parseInt(line.substring(8).trim());
                    currentChapter = new ChapterData(chapterNum, "");
                } else if (line.startsWith("NAME:") && currentChapter != null) {
                    currentChapter.name = line.substring(5).trim();
                    chapterDataList.add(currentChapter);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error loading chapters: " + e.getMessage());
        }

        ArrayList<LevelData> levelDataList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("levels.txt"));
            String line;
            LevelData currentLevel = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("LEVEL:")) {
                    int levelNum = Integer.parseInt(line.substring(6).trim());
                    currentLevel = new LevelData(levelNum);
                } else if (line.startsWith("NAME:") && currentLevel != null) {
                    currentLevel.name = line.substring(5).trim();
                } else if (line.startsWith("CHAPTER:") && currentLevel != null) {
                    currentLevel.chapterNumber = Integer.parseInt(line.substring(8).trim());
                } else if (line.startsWith("FILE:") && currentLevel != null) {
                    currentLevel.filename = line.substring(5).trim();
                    levelDataList.add(currentLevel);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error loading levels: " + e.getMessage());
        }

        for (ChapterData chapterData : chapterDataList) {
            ChapterInfo chapter = new ChapterInfo(chapterData.number, chapterData.name);

            for (LevelData levelData : levelDataList) {
                if (levelData.chapterNumber == chapterData.number) {
                    chapter.levels.add(levelData);
                }
            }

            chapter.levels.sort((a, b) -> Integer.compare(a.number, b.number));
            chapters.add(chapter);
        }

        chapters.sort((a, b) -> Integer.compare(a.number, b.number));

        System.out.println("Loaded " + chapters.size() + " chapters with levels");
    }

    private void loadLevelProgress() {
        levelProgressMap.clear();

        DBConnectMySQL connector = new DBConnectMySQL();
        if (!connector.isConnected()) {
            System.out.println("Cannot load level progress - database not connected");
            return;
        }

        try {
            String sql = "SELECT level_number, best_wave, best_score, stars_earned, quest_1_completed, quest_2_completed, quest_3_completed " +
                    "FROM level_progress WHERE save_slot_id = " + currentSave.getSlotNumber();
            ResultSet rs = connector.getStatement().executeQuery(sql);

            while (rs.next()) {
                int levelNumber = rs.getInt("level_number");
                int bestWave = rs.getInt("best_wave");
                int bestScore = rs.getInt("best_score");
                int stars = rs.getInt("stars_earned");
                boolean q1 = rs.getBoolean("quest_1_completed");
                boolean q2 = rs.getBoolean("quest_2_completed");
                boolean q3 = rs.getBoolean("quest_3_completed");

                levelProgressMap.put(levelNumber, new LevelProgressData(bestWave, bestScore, stars, q1, q2, q3));
            }

            connector.closeConnection();
            System.out.println("Loaded progress for " + levelProgressMap.size() + " levels");

        } catch (Exception e) {
            System.out.println("Error loading level progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isLevelCompleted(int levelNumber) {
        LevelProgressData progress = levelProgressMap.get(levelNumber);
        return progress != null && progress.starsEarned > 0;
    }

    private boolean isLevelUnlocked(int levelNumber) {
        if (levelNumber == 1) {
            return true;
        }
        return isLevelCompleted(levelNumber - 1);
    }

    private int getTotalStarsEarned() {
        int total = 0;
        for (LevelProgressData progress : levelProgressMap.values()) {
            total = total + progress.starsEarned;
        }
        return total;
    }

    public void show() {
        BorderPane borderPane = new BorderPane();

        Stop[] stops = new Stop[] {
                new Stop(0, Color.rgb(45, 45, 45)),
                new Stop(1, Color.rgb(35, 35, 35))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0.3, 0.3, true, CycleMethod.NO_CYCLE, stops);
        Background background = new Background(new BackgroundFill(gradient, null, null));
        borderPane.setBackground(background);

        HBox topBar = createTopBar();
        borderPane.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        VBox mapContent = createMapContent();
        scrollPane.setContent(mapContent);

        borderPane.setCenter(scrollPane);

        HBox bottomBar = createBottomBar();
        borderPane.setBottom(bottomBar);

        StackPane root = new StackPane();
        root.getChildren().add(borderPane);

        stage.getScene().setFill(javafx.scene.paint.Color.BLACK);
        root.setOpacity(0);

        stage.getScene().setRoot(root);
        stage.setFullScreen(true);

        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private VBox createMapContent() {
        VBox mapBox = new VBox(40);
        mapBox.setPadding(new Insets(40));
        mapBox.setAlignment(Pos.TOP_LEFT);
        mapBox.setStyle("-fx-background-color: transparent;");

        if (chapters.isEmpty()) {
            Label emptyLabel = new Label("No chapters or levels found!\n\nUse the Level Editor to create content.");
            emptyLabel.setFont(Font.font("Roboto Light", 24));
            emptyLabel.setStyle("-fx-text-fill: #888888; -fx-text-alignment: center;");
            emptyLabel.setAlignment(Pos.CENTER);
            mapBox.getChildren().add(emptyLabel);
            return mapBox;
        }

        for (ChapterInfo chapter : chapters) {
            VBox stageSection = createStageSection(chapter);
            mapBox.getChildren().add(stageSection);
        }

        return mapBox;
    }

    private VBox createStageSection(ChapterInfo chapter) {
        VBox stageBox = new VBox(20);
        stageBox.setAlignment(Pos.TOP_LEFT);

        Label stageLabel = new Label("Stage " + chapter.number + " " + chapter.name);
        stageLabel.setFont(Font.font("Roboto", 28));
        stageLabel.setStyle("-fx-text-fill: white;");

        HBox levelsBox = new HBox(15);
        levelsBox.setAlignment(Pos.CENTER_LEFT);

        for (LevelData level : chapter.levels) {
            StackPane levelCard = createLevelCard(level, chapter.number);
            levelsBox.getChildren().add(levelCard);
        }

        stageBox.getChildren().addAll(stageLabel, levelsBox);
        return stageBox;
    }

    private StackPane createLevelCard(LevelData level, int chapterNumber) {
        StackPane card = new StackPane();
        card.setPrefWidth(180);
        card.setPrefHeight(180);

        boolean unlocked = isLevelUnlocked(level.number);
        LevelProgressData progress = levelProgressMap.get(level.number);
        boolean completed = progress != null && progress.starsEarned > 0;
        boolean hasProgress = progress != null;

        Pane contentPane = new Pane();
        contentPane.setPrefSize(180, 180);

        if (unlocked) {
            contentPane.setStyle("-fx-background-color: rgba(26, 26, 26, 0.6); " +
                    "-fx-border-color: #00E5FF; -fx-border-width: 2; " +
                    "-fx-background-radius: 5; -fx-border-radius: 5; " +
                    "-fx-cursor: hand;");
        } else {
            contentPane.setStyle("-fx-background-color: #0a0a0a; " +
                    "-fx-border-color: #444444; -fx-border-width: 2; " +
                    "-fx-background-radius: 5; -fx-border-radius: 5;");
        }

        Label chapterLevelLabel = new Label(chapterNumber + "." + level.number);
        chapterLevelLabel.setFont(Font.font("Roboto", 18));
        chapterLevelLabel.setStyle("-fx-text-fill: white;");
        chapterLevelLabel.setLayoutX(10);
        chapterLevelLabel.setLayoutY(150);

        ArrayList<WaveMilestone> milestones = loadWaveMilestones(level.filename);
        int maxStars = 3;
        if (unlocked && !completed) {
            HBox starsBox = createStarsDisplay(0, maxStars);
            starsBox.setLayoutX(115);
            starsBox.setLayoutY(145);
            contentPane.getChildren().add(starsBox);
            if (hasProgress && progress != null) {
                attachProgressBadges(contentPane, progress.bestWave, progress.bestScore);
            }
        } else if (completed || hasProgress) {
            int stars = progress != null ? progress.starsEarned : 0;
            int wave = progress != null ? progress.bestWave : 0;
            int score = progress != null ? progress.bestScore : 0;

            HBox starsBox = createStarsDisplay(stars, maxStars);
            starsBox.setLayoutX(115);
            starsBox.setLayoutY(145);

            attachProgressBadges(contentPane, wave, score);
            contentPane.getChildren().add(starsBox);
        } else {
            int totalStars = getTotalStarsEarned();
            int requiredStars = level.number * 2;

            Image lockIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-lock-vertical");
            ImageView lockView = new ImageView(lockIcon);
            lockView.setFitWidth(48);
            lockView.setFitHeight(48);
            lockView.setPreserveRatio(true);
            lockView.setLayoutX(66);
            lockView.setLayoutY(60);

            HBox requirementBox = new HBox(5);
            requirementBox.setAlignment(Pos.CENTER);

            Label requirementLabel = new Label(totalStars + "/" + requiredStars);
            requirementLabel.setFont(Font.font("Roboto", 16));
            requirementLabel.setStyle("-fx-text-fill: #888888;");

            Image starStackIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-star-stack");
            ImageView starStackView = new ImageView(starStackIcon);
            starStackView.setFitWidth(20);
            starStackView.setFitHeight(20);
            starStackView.setPreserveRatio(true);

            requirementBox.getChildren().addAll(requirementLabel, starStackView);
            requirementBox.setLayoutX(55);
            requirementBox.setLayoutY(115);

            contentPane.getChildren().addAll(lockView, requirementBox);
        }

        contentPane.getChildren().add(chapterLevelLabel);
        card.getChildren().add(contentPane);

        if (unlocked) {
            card.setOnMouseClicked(e -> playLevel(level));

            card.setOnMouseEntered(e -> {
                contentPane.setStyle("-fx-background-color: rgba(50, 50, 50, 0.8); " +
                        "-fx-border-color: #00E676; -fx-border-width: 3; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5; " +
                        "-fx-cursor: hand;");
            });

            card.setOnMouseExited(e -> {
                contentPane.setStyle("-fx-background-color: rgba(26, 26, 26, 0.6); " +
                        "-fx-border-color: #00E5FF; -fx-border-width: 2; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5; " +
                        "-fx-cursor: hand;");
            });
        }

        return card;
    }

    private HBox createStarsDisplay(int earnedStars, int maxStars) {
        HBox starsBox = new HBox(3);
        starsBox.setAlignment(Pos.CENTER);

        int totalStars = Math.max(1, maxStars > 0 ? maxStars : 3);
        for (int i = 1; i <= totalStars; i++) {
            Image starIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-star");
            ImageView starView = new ImageView(starIcon);
            starView.setFitWidth(16);
            starView.setFitHeight(16);
            starView.setPreserveRatio(true);

            if (i <= earnedStars) {
                javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
                colorAdjust.setHue(0.25);
                colorAdjust.setSaturation(0.6);
                colorAdjust.setBrightness(0.1);
                starView.setEffect(colorAdjust);
            } else {
                starView.setOpacity(0.3);
            }

            starsBox.getChildren().add(starView);
        }

        return starsBox;
    }

    private String formatNumber(int number) {
        if (number >= 1000000) {
            return (number / 1000000) + "." + ((number % 1000000) / 100000) + "M";
        } else if (number >= 1000) {
            return (number / 1000) + "." + ((number % 1000) / 100) + "K";
        }
        return String.valueOf(number);
    }

    private void attachProgressBadges(Pane contentPane, int wave, int score) {
        HBox waveBox = new HBox(5);
        waveBox.setAlignment(Pos.CENTER);

        Image waveIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-wave");
        ImageView waveView = new ImageView(waveIcon);
        waveView.setFitWidth(16);
        waveView.setFitHeight(16);
        waveView.setPreserveRatio(true);

        Label waveLabel = new Label(String.valueOf(wave));
        waveLabel.setFont(Font.font("Roboto Light", 14));
        waveLabel.setStyle("-fx-text-fill: white;");

        waveBox.getChildren().addAll(waveView, waveLabel);
        waveBox.setLayoutX(115);
        waveBox.setLayoutY(120);

        Label scoreLabel = new Label(formatNumber(score));
        scoreLabel.setFont(Font.font("Roboto Light", 14));
        scoreLabel.setStyle("-fx-text-fill: white;");
        scoreLabel.setLayoutX(115);
        scoreLabel.setLayoutY(95);

        contentPane.getChildren().addAll(waveBox, scoreLabel);
    }

    private ArrayList<WaveMilestone> loadWaveMilestones(String filename) {
        ArrayList<WaveMilestone> milestones = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("WAVE_MILESTONE:")) {
                    String[] parts = line.substring(15).split(":");
                    if (parts.length >= 1) {
                        try {
                            milestones.add(new WaveMilestone(Integer.parseInt(parts[0].trim())));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                } else if (line.equals("GRID:")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading milestones for " + filename + ": " + e.getMessage());
        }

        return WaveMilestone.normalize(milestones);
    }

    private void playLevel(LevelData level) {
        System.out.println("Playing level " + level.number + ": " + level.name);

        com.eliemichel.polyfinite.domain.level.LevelInfo levelInfo = new com.eliemichel.polyfinite.domain.level.LevelInfo(level.number, level.name);
        levelInfo.setUnlocked(true);

        ArrayList<WaveMilestone> milestones = loadWaveMilestones(level.filename);
        levelInfo.setWaveMilestones(milestones);

        LevelProgressData progress = levelProgressMap.get(level.number);
        if (progress != null) {
            levelInfo.updateProgress(progress.bestWave, progress.bestScore, progress.starsEarned);
        }

        GameplayScreen gameplayScreen = new GameplayScreen(stage, levelInfo, currentSave);
        gameplayScreen.show();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: transparent;");

        HBox leftSide = new HBox(15);
        leftSide.setAlignment(Pos.CENTER_LEFT);

        Image infinityIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-infinity");
        ImageView iconView = new ImageView(infinityIcon);
        iconView.setFitWidth(32);
        iconView.setFitHeight(32);
        iconView.setPreserveRatio(true);

        Label titleLabel = new Label("Select Level");
        titleLabel.setFont(Font.font("Roboto Light", 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        leftSide.getChildren().addAll(iconView, titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane moneyBox = new StackPane();
        moneyBox.setPrefSize(150, 50);

        Image forwardButton = AtlasManager.getInstance().getAtlas().getRegion("ui-forward-button");
        ImageView buttonBg = new ImageView(forwardButton);
        buttonBg.setFitWidth(150);
        buttonBg.setFitHeight(50);
        buttonBg.setPreserveRatio(false);
        buttonBg.setScaleX(-1);
        buttonBg.setScaleY(-1);

        // TINT GOLD
        javafx.scene.effect.ColorAdjust goldTint = new javafx.scene.effect.ColorAdjust();
        goldTint.setHue(-0.1);
        goldTint.setSaturation(0.5);
        goldTint.setBrightness(0.2);
        buttonBg.setEffect(goldTint);

        Image coinIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-coins");
        ImageView coinView = new ImageView(coinIcon);
        coinView.setFitWidth(24);
        coinView.setFitHeight(24);
        coinView.setPreserveRatio(true);

        Label moneyAmount = new Label(String.valueOf(currentSave.getGold()));
        moneyAmount.setFont(Font.font("Roboto", 20));
        moneyAmount.setStyle("-fx-text-fill: #FFD700;");

        HBox moneyContent = new HBox(10);
        moneyContent.setAlignment(Pos.CENTER);
        moneyContent.getChildren().addAll(coinView, moneyAmount);

        moneyBox.getChildren().addAll(buttonBg, moneyContent);

        topBar.getChildren().addAll(leftSide, spacer, moneyBox);
        return topBar;
    }

    private HBox createBottomBar() {
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setStyle("-fx-background-color: transparent;");

        StackPane backBox = new StackPane();
        backBox.setPrefSize(120, 50);
        backBox.setStyle("-fx-cursor: hand;");

        Image backButtonBg = AtlasManager.getInstance().getAtlas().getRegion("ui-back-button");
        ImageView backBgView = new ImageView(backButtonBg);
        backBgView.setFitWidth(120);
        backBgView.setFitHeight(50);
        backBgView.setPreserveRatio(false);

        // TINT DARK GRAY
        javafx.scene.effect.ColorAdjust darkGrayTint = new javafx.scene.effect.ColorAdjust();
        darkGrayTint.setSaturation(-1.0);
        darkGrayTint.setBrightness(-0.5);
        backBgView.setEffect(darkGrayTint);

        Image triangleLeft = AtlasManager.getInstance().getAtlas().getRegion("icon-triangle-left");
        ImageView triangleView = new ImageView(triangleLeft);
        triangleView.setFitWidth(20);
        triangleView.setFitHeight(20);
        triangleView.setPreserveRatio(true);

        Label backText = new Label("Back");
        backText.setFont(Font.font("Roboto Light", 18));
        backText.setStyle("-fx-text-fill: white;");

        HBox backContent = new HBox(8);
        backContent.setAlignment(Pos.CENTER);
        backContent.getChildren().addAll(triangleView, backText);

        backBox.getChildren().addAll(backBgView, backContent);

        backBox.setOnMouseClicked(e -> {
            javafx.scene.Parent root = stage.getScene().getRoot();
            ScreenTransition transition = new ScreenTransition(stage);

            if (root instanceof javafx.scene.layout.Pane) {
                ((javafx.scene.layout.Pane) root).getChildren().add(transition.getTransitionPane());
            }

            transition.playTransition(() -> {
                MenuScreen menuScreen = new MenuScreen(stage);
                menuScreen.show();
            });
        });

        // RE-TINT LIGHTER GRAY ON HOVER
        backBox.setOnMouseEntered(e -> {
            javafx.scene.effect.ColorAdjust lighterGrayTint = new javafx.scene.effect.ColorAdjust();
            lighterGrayTint.setSaturation(-1.0);
            lighterGrayTint.setBrightness(-0.3);
            backBgView.setEffect(lighterGrayTint);
        });

        backBox.setOnMouseExited(e -> {
            javafx.scene.effect.ColorAdjust darkGrayTintBack = new javafx.scene.effect.ColorAdjust();
            darkGrayTintBack.setSaturation(-1.0);
            darkGrayTintBack.setBrightness(-0.5);
            backBgView.setEffect(darkGrayTintBack);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane researchBox = new StackPane();
        researchBox.setPrefSize(180, 55);
        researchBox.setStyle("-fx-cursor: hand;");

        Image researchBg = AtlasManager.getInstance().getAtlas().getRegion("item-cell-b-shape");
        ImageView researchBgView = new ImageView(researchBg);
        researchBgView.setFitWidth(180);
        researchBgView.setFitHeight(55);
        researchBgView.setPreserveRatio(false);

        // TINT DARK GRAY
        javafx.scene.effect.ColorAdjust researchDarkTint = new javafx.scene.effect.ColorAdjust();
        researchDarkTint.setSaturation(-1.0);
        researchDarkTint.setBrightness(-0.5);
        researchBgView.setEffect(researchDarkTint);

        Image researchIcon = AtlasManager.getInstance().getAtlas().getRegion("icon-research");
        ImageView researchIconView = new ImageView(researchIcon);
        researchIconView.setFitWidth(28);
        researchIconView.setFitHeight(28);
        researchIconView.setPreserveRatio(true);

        Label researchText = new Label("RESEARCH");
        researchText.setFont(Font.font("Roboto", 16));
        researchText.setStyle("-fx-text-fill: white;");

        HBox researchContent = new HBox(10);
        researchContent.setAlignment(Pos.CENTER);
        researchContent.getChildren().addAll(researchIconView, researchText);

        researchBox.getChildren().addAll(researchBgView, researchContent);

        researchBox.setOnMouseClicked(e -> openResearchScreen());

        // RE-TINT LIGHTER GRAY ON HOVER
        researchBox.setOnMouseEntered(e -> {
            javafx.scene.effect.ColorAdjust lighterGrayTint = new javafx.scene.effect.ColorAdjust();
            lighterGrayTint.setSaturation(-1.0);
            lighterGrayTint.setBrightness(-0.3);
            researchBgView.setEffect(lighterGrayTint);
        });

        researchBox.setOnMouseExited(e -> {
            javafx.scene.effect.ColorAdjust darkGrayTintBack = new javafx.scene.effect.ColorAdjust();
            darkGrayTintBack.setSaturation(-1.0);
            darkGrayTintBack.setBrightness(-0.5);
            researchBgView.setEffect(darkGrayTintBack);
        });

        bottomBar.getChildren().addAll(backBox, spacer, researchBox);
        return bottomBar;
    }

    private void openResearchScreen() {
        javafx.scene.Parent root = stage.getScene().getRoot();
        ScreenTransition transition = new ScreenTransition(stage);

        if (root instanceof javafx.scene.layout.Pane) {
            ((javafx.scene.layout.Pane) root).getChildren().add(transition.getTransitionPane());
        }

        transition.playTransition(() -> {
            ResearchScreen researchScreen = new ResearchScreen(stage, currentSave);
            researchScreen.show();
        });
    }
}

class ChapterData {
    int number;
    String name;

    ChapterData(int number, String name) {
        this.number = number;
        this.name = name;
    }
}

class LevelData {
    int number;
    String name;
    int chapterNumber;
    String filename;

    LevelData(int number) {
        this.number = number;
    }
}

class ChapterInfo {
    int number;
    String name;
    ArrayList<LevelData> levels;

    ChapterInfo(int number, String name) {
        this.number = number;
        this.name = name;
        this.levels = new ArrayList<>();
    }
}

class LevelProgressData {
    int bestWave;
    int bestScore;
    int starsEarned;
    boolean quest1;
    boolean quest2;
    boolean quest3;

    LevelProgressData(int wave, int score, int stars, boolean q1, boolean q2, boolean q3) {
        this.bestWave = wave;
        this.bestScore = score;
        this.starsEarned = stars;
        this.quest1 = q1;
        this.quest2 = q2;
        this.quest3 = q3;
    }
}