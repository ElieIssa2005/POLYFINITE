package com.eliemichel.polyfinite.editor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class LevelEditor extends Application {

    private Stage primaryStage;
    private ArrayList<ChapterData> chapters;
    private ArrayList<LevelMetadata> levels;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.chapters = new ArrayList<>();
        this.levels = new ArrayList<>();

        loadChaptersAndLevels();
        showMainMenu();
    }

    private void loadChaptersAndLevels() {
        // Load chapters
        try {
            File chaptersFile = new File("chapters.txt");
            if (chaptersFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(chaptersFile));
                String line;
                ChapterData currentChapter = null;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("CHAPTER:")) {
                        int chapterNum = Integer.parseInt(line.substring(8).trim());
                        currentChapter = new ChapterData(chapterNum, "");
                    } else if (line.startsWith("NAME:") && currentChapter != null) {
                        currentChapter.name = line.substring(5).trim();
                        chapters.add(currentChapter);
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("No chapters file found or error loading: " + e.getMessage());
        }

        // Load levels metadata
        try {
            File levelsFile = new File("levels.txt");
            if (levelsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(levelsFile));
                String line;
                LevelMetadata currentLevel = null;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("LEVEL:")) {
                        int levelNum = Integer.parseInt(line.substring(6).trim());
                        currentLevel = new LevelMetadata(levelNum);
                    } else if (line.startsWith("NAME:") && currentLevel != null) {
                        currentLevel.name = line.substring(5).trim();
                    } else if (line.startsWith("CHAPTER:") && currentLevel != null) {
                        currentLevel.chapterNumber = Integer.parseInt(line.substring(8).trim());
                    } else if (line.startsWith("FILE:") && currentLevel != null) {
                        currentLevel.filename = line.substring(5).trim();
                        levels.add(currentLevel);
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("No levels file found or error loading: " + e.getMessage());
        }

        System.out.println("Loaded " + chapters.size() + " chapters and " + levels.size() + " levels");
    }

    private void saveChapters() {
        try {
            FileWriter writer = new FileWriter("chapters.txt");
            for (ChapterData chapter : chapters) {
                writer.write("CHAPTER:" + chapter.number + "\n");
                writer.write("NAME:" + chapter.name + "\n\n");
            }
            writer.close();
            System.out.println("Chapters saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLevelsMetadata() {
        try {
            FileWriter writer = new FileWriter("levels.txt");
            for (LevelMetadata level : levels) {
                writer.write("LEVEL:" + level.number + "\n");
                writer.write("NAME:" + level.name + "\n");
                writer.write("CHAPTER:" + level.chapterNumber + "\n");
                writer.write("FILE:" + level.filename + "\n\n");
            }
            writer.close();
            System.out.println("Levels metadata saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== MAIN MENU ====================
    void showMainMenu() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        Label titleLabel = new Label("POLYFINITE LEVEL EDITOR");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #00E5FF; " +
                "-fx-effect: dropshadow(gaussian, #00E5FF, 15, 0.8, 0, 0);");

        Label subtitleLabel = new Label("What would you like to do?");
        subtitleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #AAAAAA;");

        Button createChapterButton = createMenuButton("📚 CREATE NEW CHAPTER", "#00E676");
        createChapterButton.setOnAction(e -> showCreateChapter());

        Button createLevelButton = createMenuButton("🗺️ CREATE NEW LEVEL", "#00E5FF");
        createLevelButton.setOnAction(e -> showCreateLevel());

        Button editLevelButton = createMenuButton("✏️ EDIT LEVEL", "#FFD700");
        editLevelButton.setOnAction(e -> showEditLevelSelection());

        Button deleteLevelButton = createMenuButton("🗑️ DELETE LEVEL", "#FF6E40");
        deleteLevelButton.setOnAction(e -> showDeleteLevelSelection());

        Button deleteChapterButton = createMenuButton("❌ DELETE CHAPTER", "#F44336");
        deleteChapterButton.setOnAction(e -> showDeleteChapterSelection());

        centerBox.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                new Label(""),
                createChapterButton,
                createLevelButton,
                editLevelButton,
                deleteLevelButton,
                deleteChapterButton
        );

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1280, 820);
        primaryStage.setTitle("POLYFINITE - Level Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(400);
        button.setPrefHeight(70);
        button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " +
                "-fx-background-color: " + color + "; -fx-text-fill: black; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.7, 0, 0);");
        return button;
    }

    // ==================== CREATE CHAPTER ====================
    private void showCreateChapter() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        Label titleLabel = new Label("CREATE NEW CHAPTER");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #00E676;");

        Label chapterNumLabel = new Label("Chapter Number:");
        chapterNumLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        TextField chapterNumField = new TextField();
        chapterNumField.setPromptText("e.g., 1");
        chapterNumField.setPrefWidth(300);
        chapterNumField.setStyle("-fx-font-size: 16px;");

        Label chapterNameLabel = new Label("Chapter Name:");
        chapterNameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        TextField chapterNameField = new TextField();
        chapterNameField.setPromptText("e.g., Alpha Sector");
        chapterNameField.setPrefWidth(300);
        chapterNameField.setStyle("-fx-font-size: 16px;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("◄ BACK");
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());

        Button createButton = new Button("✓ CREATE CHAPTER");
        createButton.setPrefWidth(200);
        createButton.setPrefHeight(50);
        createButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E676; -fx-text-fill: black;");

        createButton.setOnAction(e -> {
            String name = chapterNameField.getText().trim();
            String numText = chapterNumField.getText().trim();

            if (name.isEmpty() || numText.isEmpty()) {
                showError("Please fill all fields!");
                return;
            }

            try {
                int chapterNum = Integer.parseInt(numText);

                // Check if chapter number already exists
                for (ChapterData chapter : chapters) {
                    if (chapter.number == chapterNum) {
                        showError("Chapter " + chapterNum + " already exists!");
                        return;
                    }
                }

                ChapterData newChapter = new ChapterData(chapterNum, name);
                chapters.add(newChapter);
                saveChapters();

                showSuccess("Chapter Created!", "Chapter " + chapterNum + ": " + name + " created successfully!");
                showMainMenu();

            } catch (NumberFormatException ex) {
                showError("Please enter a valid chapter number!");
            }
        });

        buttonBox.getChildren().addAll(backButton, createButton);

        centerBox.getChildren().addAll(
                titleLabel,
                new Label(""),
                chapterNumLabel,
                chapterNumField,
                chapterNameLabel,
                chapterNameField,
                new Label(""),
                buttonBox
        );

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1280, 820);
        primaryStage.setScene(scene);
    }

    // ==================== CREATE LEVEL ====================
    private void showCreateLevel() {
        if (chapters.isEmpty()) {
            showError("No chapters found! Please create a chapter first.");
            return;
        }

        new LevelCreator(primaryStage, chapters, levels, this).start();
    }

    // ==================== EDIT LEVEL ====================
    private void showEditLevelSelection() {
        if (levels.isEmpty()) {
            showError("No levels found! Please create a level first.");
            return;
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("SELECT LEVEL TO EDIT");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FFD700; " +
                "-fx-padding: 15; -fx-background-color: #1a1a1a;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(30));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #2b2b2b;");
        scrollPane.setFitToWidth(true);

        VBox levelListBox = new VBox(10);
        levelListBox.setAlignment(Pos.TOP_CENTER);

        for (LevelMetadata level : levels) {
            Button levelButton = new Button("Level " + level.number + ": " + level.name);
            levelButton.setPrefWidth(500);
            levelButton.setPrefHeight(60);
            levelButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                    "-fx-background-color: #FFD700; -fx-text-fill: black;");

            levelButton.setOnAction(e -> {
                new LevelCreator(primaryStage, chapters, levels, this).startEdit(level);
            });

            levelListBox.getChildren().add(levelButton);
        }

        scrollPane.setContent(levelListBox);
        centerBox.getChildren().add(scrollPane);

        Button backButton = new Button("◄ BACK TO MENU");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());

        VBox bottomBox = new VBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15));
        root.setBottom(bottomBox);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1280, 820);
        primaryStage.setScene(scene);
    }

    // ==================== DELETE LEVEL ====================
    private void showDeleteLevelSelection() {
        if (levels.isEmpty()) {
            showError("No levels found!");
            return;
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("SELECT LEVEL TO DELETE");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FF6E40; " +
                "-fx-padding: 15; -fx-background-color: #1a1a1a;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(30));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #2b2b2b;");
        scrollPane.setFitToWidth(true);

        VBox levelListBox = new VBox(10);
        levelListBox.setAlignment(Pos.TOP_CENTER);

        for (LevelMetadata level : levels) {
            Button levelButton = new Button("Level " + level.number + ": " + level.name);
            levelButton.setPrefWidth(500);
            levelButton.setPrefHeight(60);
            levelButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                    "-fx-background-color: #FF6E40; -fx-text-fill: white;");

            levelButton.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Level");
                confirm.setHeaderText("Are you sure?");
                confirm.setContentText("Delete Level " + level.number + ": " + level.name + "?");

                if (confirm.showAndWait().get() == ButtonType.OK) {
                    // Delete level file
                    File levelFile = new File(level.filename);
                    if (levelFile.exists()) {
                        levelFile.delete();
                    }

                    // Remove from list
                    levels.remove(level);
                    saveLevelsMetadata();

                    showSuccess("Level Deleted", "Level " + level.number + " deleted successfully!");
                    showMainMenu();
                }
            });

            levelListBox.getChildren().add(levelButton);
        }

        scrollPane.setContent(levelListBox);
        centerBox.getChildren().add(scrollPane);

        Button backButton = new Button("◄ BACK TO MENU");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());

        VBox bottomBox = new VBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15));
        root.setBottom(bottomBox);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1280, 820);
        primaryStage.setScene(scene);
    }

    // ==================== DELETE CHAPTER ====================
    private void showDeleteChapterSelection() {
        if (chapters.isEmpty()) {
            showError("No chapters found!");
            return;
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("SELECT CHAPTER TO DELETE");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #F44336; " +
                "-fx-padding: 15; -fx-background-color: #1a1a1a;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(30));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #2b2b2b;");
        scrollPane.setFitToWidth(true);

        VBox chapterListBox = new VBox(10);
        chapterListBox.setAlignment(Pos.TOP_CENTER);

        for (ChapterData chapter : chapters) {
            // Count levels in this chapter
            int levelCount = (int) levels.stream().filter(level -> level.chapterNumber == chapter.number).count();

            Button chapterButton = new Button("Chapter " + chapter.number + ": " + chapter.name +
                    " (" + levelCount + " levels)");
            chapterButton.setPrefWidth(500);
            chapterButton.setPrefHeight(60);
            chapterButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                    "-fx-background-color: #F44336; -fx-text-fill: white;");

            chapterButton.setOnAction(e -> {
                if (levelCount > 0) {
                    showError("Cannot delete chapter with levels! Delete all levels in this chapter first.");
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Chapter");
                confirm.setHeaderText("Are you sure?");
                confirm.setContentText("Delete Chapter " + chapter.number + ": " + chapter.name + "?");

                if (confirm.showAndWait().get() == ButtonType.OK) {
                    chapters.remove(chapter);
                    saveChapters();

                    showSuccess("Chapter Deleted", "Chapter " + chapter.number + " deleted successfully!");
                    showMainMenu();
                }
            });

            chapterListBox.getChildren().add(chapterButton);
        }

        scrollPane.setContent(chapterListBox);
        centerBox.getChildren().add(scrollPane);

        Button backButton = new Button("◄ BACK TO MENU");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());

        VBox bottomBox = new VBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15));
        root.setBottom(bottomBox);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1280, 820);
        primaryStage.setScene(scene);
    }

    // ==================== UTILITY ====================
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// ==================== DATA CLASSES ====================
class ChapterData {
    int number;
    String name;

    ChapterData(int number, String name) {
        this.number = number;
        this.name = name;
    }
}

class LevelMetadata {
    int number;
    String name;
    int chapterNumber;
    String filename;

    LevelMetadata(int number) {
        this.number = number;
    }
}