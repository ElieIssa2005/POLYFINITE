package com.eliemichel.polyfinite.editor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import com.eliemichel.polyfinite.game.*;
import com.eliemichel.polyfinite.game.tiles.*;

public class LevelCreator {

    private Stage stage;
    private ArrayList<ChapterData> chapters;
    private ArrayList<LevelMetadata> levels;
    private LevelEditor mainEditor;

    private LevelData levelData;
    private LevelMetadata editingLevel;
    private boolean isEditMode;

    private final int TILE_SIZE = 40;
    private TileType selectedTileType = TileType.ROAD;

    // UI Components - Step 1
    private TextField levelNameField;
    private TextField levelNumberField;
    private ComboBox<String> chapterComboBox;
    private TextField gridWidthField;
    private TextField gridHeightField;

    // UI Components - Step 2
    private Pane gridPane;
    private Rectangle[][] gridRectangles;
    private Label selectedTileLabel;
    private ScrollPane gridScrollPane;

    // UI Components - Step 3 (Wave Settings)
    private ComboBox<String> densityComboBox;
    private TextField customIntervalField;
    private TextField difficultyField;
    private TextField baseEnemiesField;
    private TextField maxEnemiesField;
    private TextField interWaveTimeField;
    private VBox enemyWeightsBox;
    private ArrayList<HBox> weightRows;
    // Direct reference to the "Total" label to avoid lookup before attachment
    private Label totalWeightLabel;

    // UI Components - Step 4 (Quest Settings)
    private VBox questsBox;
    private ArrayList<VBox> questRows;
    private TextField goldDropChanceField;
    private VBox milestoneBox;
    private ArrayList<TextField> milestoneFields;

    public LevelCreator(Stage stage, ArrayList<ChapterData> chapters, ArrayList<LevelMetadata> levels, LevelEditor mainEditor) {
        this.stage = stage;
        this.chapters = chapters;
        this.levels = levels;
        this.mainEditor = mainEditor;
        this.isEditMode = false;
        this.weightRows = new ArrayList<>();
        this.questRows = new ArrayList<>();
        this.milestoneFields = new ArrayList<>();
    }

    public void start() {
        isEditMode = false;
        editingLevel = null;
        showStep1();
    }

    public void startEdit(LevelMetadata level) {
        isEditMode = true;
        editingLevel = level;
        loadLevelForEdit(level);
    }

    private void loadLevelForEdit(LevelMetadata level) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(level.filename));
            String line;

            int gridWidth = 25;
            int gridHeight = 15;
            String levelName = level.name;
            int levelNumber = level.number;

            // Wave settings
            SpawnDensity density = SpawnDensity.MEDIUM;
            double customInterval = 0.5;
            double difficultyModifier = 1.0;
            int baseEnemyCount = 10;
            int maxEnemyCount = 100;
            double interWaveTime = 10.0;
            ArrayList<EnemyWeight> enemyWeights = new ArrayList<>();

            // Quest settings
            double goldDropChance = 0.05;
            ArrayList<QuestDefinition> questDefinitions = new ArrayList<>();
            ArrayList<WaveMilestone> waveMilestones = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("LEVEL_NAME:")) {
                    levelName = line.substring(11).trim();
                } else if (line.startsWith("GRID_WIDTH:")) {
                    gridWidth = Integer.parseInt(line.substring(11).trim());
                } else if (line.startsWith("GRID_HEIGHT:")) {
                    gridHeight = Integer.parseInt(line.substring(12).trim());
                } else if (line.startsWith("SPAWN_DENSITY:")) {
                    density = SpawnDensity.fromString(line.substring(14).trim());
                } else if (line.startsWith("CUSTOM_INTERVAL:")) {
                    customInterval = Double.parseDouble(line.substring(16).trim());
                } else if (line.startsWith("DIFFICULTY:")) {
                    difficultyModifier = Double.parseDouble(line.substring(11).trim());
                } else if (line.startsWith("BASE_ENEMIES:")) {
                    baseEnemyCount = Integer.parseInt(line.substring(13).trim());
                } else if (line.startsWith("MAX_ENEMIES:")) {
                    maxEnemyCount = Integer.parseInt(line.substring(12).trim());
                } else if (line.startsWith("INTER_WAVE_TIME:")) {
                    interWaveTime = Double.parseDouble(line.substring(16).trim());
                } else if (line.startsWith("ENEMY_WEIGHT:")) {
                    String[] parts = line.substring(13).split(":");
                    if (parts.length == 2) {
                        enemyWeights.add(new EnemyWeight(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                    }
                } else if (line.startsWith("GOLD_DROP_CHANCE:")) {
                    goldDropChance = Double.parseDouble(line.substring(17).trim());
                } else if (line.startsWith("QUEST:")) {
                    QuestDefinition quest = QuestDefinition.fromFileString(line.substring(6).trim());
                    if (quest != null) {
                        questDefinitions.add(quest);
                    }
                } else if (line.startsWith("WAVE_MILESTONE:")) {
                    String[] parts = line.substring(15).split(":");
                    try {
                        waveMilestones.add(new WaveMilestone(Integer.parseInt(parts[0].trim())));
                    } catch (NumberFormatException ignored) { }
                } else if (line.equals("GRID:")) {
                    break;
                }
            }

            levelData = new LevelData(gridWidth, gridHeight);
            levelData.setLevelNumber(levelNumber);
            levelData.setLevelName(levelName);
            levelData.setSpawnDensity(density);
            levelData.setCustomSpawnInterval(customInterval);
            levelData.setDifficultyModifier(difficultyModifier);
            levelData.setBaseEnemyCount(baseEnemyCount);
            levelData.setMaxEnemyCount(maxEnemyCount);
            levelData.setInterWaveTime(interWaveTime);
            if (!enemyWeights.isEmpty()) {
                levelData.setEnemyWeights(enemyWeights);
            }

            // Apply quest settings
            levelData.setGoldDropChance(goldDropChance);
            if (!questDefinitions.isEmpty()) {
                levelData.setQuestDefinitions(questDefinitions);
            }
            if (!waveMilestones.isEmpty()) {
                levelData.setWaveMilestones(waveMilestones);
            }

            // Load grid
            for (int row = 0; row < gridHeight; row++) {
                line = reader.readLine();
                if (line != null) {
                    String[] tiles = line.split(",");
                    for (int col = 0; col < gridWidth && col < tiles.length; col++) {
                        int tileCode = Integer.parseInt(tiles[col].trim());

                        Tile tile = null;
                        switch (tileCode) {
                            case 0:
                                tile = new EmptyTile(row, col);
                                break;
                            case 1:
                                tile = new RoadTile(row, col);
                                break;
                            case 2:
                                tile = new PlatformTile(row, col);
                                break;
                            case 3:
                                tile = new SpawnTile(row, col);
                                break;
                            case 4:
                                tile = new GoalTile(row, col);
                                break;
                            default:
                                tile = new EmptyTile(row, col);
                                break;
                        }
                        levelData.setTile(row, col, tile);
                    }
                }
            }

            reader.close();
            showStep2();

        } catch (Exception e) {
            showError("Error loading level: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== STEP 1: Level Setup ====================
    private void showStep1() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        Label titleLabel = new Label("STEP 1: LEVEL SETUP");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #00E5FF;");

        Label levelNameLabel = new Label("Level Name:");
        levelNameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        levelNameField = new TextField();
        levelNameField.setPromptText("e.g., Training Grounds");
        levelNameField.setPrefWidth(400);
        levelNameField.setStyle("-fx-font-size: 16px;");

        Label chapterLabel = new Label("Chapter:");
        chapterLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        chapterComboBox = new ComboBox<>();
        chapterComboBox.setPromptText("Select a chapter");
        chapterComboBox.setPrefWidth(400);
        chapterComboBox.setStyle("-fx-font-size: 16px;");

        for (ChapterData chapter : chapters) {
            chapterComboBox.getItems().add("Chapter " + chapter.number + ": " + chapter.name);
        }

        Label levelNumberLabel = new Label("Level Number:");
        levelNumberLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        levelNumberField = new TextField();
        levelNumberField.setPromptText("e.g., 1");
        levelNumberField.setPrefWidth(400);
        levelNumberField.setStyle("-fx-font-size: 16px;");

        Label gridSizeLabel = new Label("Grid Size:");
        gridSizeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        HBox gridSizeBox = new HBox(10);
        gridSizeBox.setAlignment(Pos.CENTER);

        gridWidthField = new TextField("25");
        gridWidthField.setPromptText("Width");
        gridWidthField.setPrefWidth(190);
        gridWidthField.setStyle("-fx-font-size: 16px;");

        Label xLabel = new Label("×");
        xLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        gridHeightField = new TextField("15");
        gridHeightField.setPromptText("Height");
        gridHeightField.setPrefWidth(190);
        gridHeightField.setStyle("-fx-font-size: 16px;");

        gridSizeBox.getChildren().addAll(gridWidthField, xLabel, gridHeightField);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("◄ CANCEL");
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white;");
        backButton.setOnAction(e -> mainEditor.showMainMenu());

        Button nextButton = new Button("NEXT: Design Grid ►");
        nextButton.setPrefWidth(220);
        nextButton.setPrefHeight(50);
        nextButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E676; -fx-text-fill: black;");

        nextButton.setOnAction(e -> handleStep1Next());

        buttonBox.getChildren().addAll(backButton, nextButton);

        centerBox.getChildren().addAll(
                titleLabel, new Label(""),
                levelNameLabel, levelNameField,
                chapterLabel, chapterComboBox,
                levelNumberLabel, levelNumberField,
                gridSizeLabel, gridSizeBox,
                new Label(""), buttonBox
        );

        root.setCenter(centerBox);
        Scene scene = new Scene(root, 1280, 820);
        stage.setScene(scene);
    }

    private void handleStep1Next() {
        String name = levelNameField.getText().trim();
        String numberText = levelNumberField.getText().trim();
        String widthText = gridWidthField.getText().trim();
        String heightText = gridHeightField.getText().trim();

        if (name.isEmpty() || numberText.isEmpty() || widthText.isEmpty() || heightText.isEmpty()) {
            showError("Please fill all fields!");
            return;
        }

        if (chapterComboBox.getValue() == null) {
            showError("Please select a chapter!");
            return;
        }

        try {
            int levelNum = Integer.parseInt(numberText);
            int width = Integer.parseInt(widthText);
            int height = Integer.parseInt(heightText);

            if (width < 5 || width > 100) {
                showError("Width must be between 5 and 100!");
                return;
            }
            if (height < 5 || height > 100) {
                showError("Height must be between 5 and 100!");
                return;
            }

            if (!isEditMode) {
                for (LevelMetadata level : levels) {
                    if (level.number == levelNum) {
                        showError("Level " + levelNum + " already exists!");
                        return;
                    }
                }
            }

            String selectedChapter = chapterComboBox.getValue();
            int chapterNum = Integer.parseInt(selectedChapter.split(":")[0].replace("Chapter ", "").trim());

            levelData = new LevelData(width, height);
            levelData.setLevelName(name);
            levelData.setLevelNumber(levelNum);

            if (editingLevel == null) {
                editingLevel = new LevelMetadata(levelNum);
            }
            editingLevel.chapterNumber = chapterNum;
            editingLevel.name = name;

            showStep2();

        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers!");
        }
    }

    // ==================== STEP 2: Grid Editor ====================
    private void showStep2() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        String modeText = isEditMode ? "EDIT" : "CREATE";
        Label titleLabel = new Label("STEP 2: DESIGN LEVEL GRID (" + modeText + " MODE)");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #00E5FF; " +
                "-fx-padding: 15; -fx-background-color: #1a1a1a;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        VBox leftPanel = createTilePalette();
        root.setLeft(leftPanel);

        gridScrollPane = new ScrollPane();
        gridScrollPane.setStyle("-fx-background: #2b2b2b; -fx-background-color: #2b2b2b;");

        gridPane = new Pane();
        gridPane.setStyle("-fx-background-color: #1a1a1a;");

        int gridWidth = levelData.getGridWidth();
        int gridHeight = levelData.getGridHeight();

        gridPane.setPrefSize(gridWidth * TILE_SIZE, gridHeight * TILE_SIZE);
        gridRectangles = new Rectangle[gridHeight][gridWidth];

        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
                rect.setX(col * TILE_SIZE);
                rect.setY(row * TILE_SIZE);
                rect.setFill(levelData.getTile(row, col).getColor());
                rect.setStroke(Color.web("#444444"));
                rect.setStrokeWidth(1);

                final int r = row;
                final int c = col;
                rect.setOnMouseClicked(e -> paintTile(r, c));
                rect.setOnMouseDragged(e -> paintTile(r, c));

                gridRectangles[row][col] = rect;
                gridPane.getChildren().add(rect);
            }
        }

        gridScrollPane.setContent(gridPane);
        root.setCenter(gridScrollPane);

        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: #1a1a1a;");

        Button clearButton = new Button("🗑 CLEAR GRID");
        clearButton.setPrefWidth(150);
        clearButton.setPrefHeight(50);
        clearButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 8;");
        clearButton.setOnAction(e -> clearGrid());

        Button backButton = new Button("◄ BACK");
        backButton.setPrefWidth(120);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white; -fx-background-radius: 8;");
        backButton.setOnAction(e -> {
            if (isEditMode) {
                mainEditor.showMainMenu();
            } else {
                showStep1();
            }
        });

        Button nextButton = new Button("NEXT: Wave Settings ►");
        nextButton.setPrefWidth(220);
        nextButton.setPrefHeight(50);
        nextButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E676; -fx-text-fill: black; -fx-background-radius: 8;");
        nextButton.setOnAction(e -> {
            if (!validateGrid()) {
                showError("Grid validation failed!\n\nMake sure you have:\n- At least 1 Spawn tile\n- At least 1 Goal tile\n- At least 1 Road tile");
                return;
            }
            showStep3();
        });

        buttonBox.getChildren().addAll(clearButton, backButton, nextButton);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 1280, 820);
        stage.setScene(scene);
    }

    private VBox createTilePalette() {
        VBox palette = new VBox(15);
        palette.setPadding(new Insets(20));
        palette.setStyle("-fx-background-color: #1a1a1a;");
        palette.setPrefWidth(200);

        Label paletteTitle = new Label("TILE PALETTE");
        paletteTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        selectedTileLabel = new Label("Selected: ROAD");
        selectedTileLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; " +
                "-fx-background-color: #333333; -fx-padding: 10; -fx-background-radius: 5;");

        ToggleGroup tileGroup = new ToggleGroup();

        RadioButton emptyRadio = createTileButton("Empty (Grass)", TileType.EMPTY, tileGroup);
        RadioButton roadRadio = createTileButton("Road", TileType.ROAD, tileGroup);
        RadioButton platformRadio = createTileButton("Platform", TileType.PLATFORM, tileGroup);
        RadioButton spawnRadio = createTileButton("Spawn Point", TileType.SPAWN, tileGroup);
        RadioButton goalRadio = createTileButton("Goal", TileType.GOAL, tileGroup);

        roadRadio.setSelected(true);

        palette.getChildren().addAll(
                paletteTitle, selectedTileLabel, new Separator(),
                emptyRadio, roadRadio, platformRadio, spawnRadio, goalRadio
        );

        return palette;
    }

    private RadioButton createTileButton(String name, TileType type, ToggleGroup group) {
        RadioButton radio = new RadioButton(name);
        radio.setToggleGroup(group);
        radio.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        radio.setOnAction(e -> {
            selectedTileType = type;
            selectedTileLabel.setText("Selected: " + type.getName().toUpperCase());
        });
        return radio;
    }

    private void paintTile(int row, int col) {
        Tile newTile = null;
        switch (selectedTileType) {
            case EMPTY: newTile = new EmptyTile(row, col); break;
            case ROAD: newTile = new RoadTile(row, col); break;
            case PLATFORM: newTile = new PlatformTile(row, col); break;
            case SPAWN: newTile = new SpawnTile(row, col); break;
            case GOAL: newTile = new GoalTile(row, col); break;
        }
        if (newTile != null) {
            levelData.setTile(row, col, newTile);
            gridRectangles[row][col].setFill(newTile.getColor());
        }
    }

    private void clearGrid() {
        for (int row = 0; row < levelData.getGridHeight(); row++) {
            for (int col = 0; col < levelData.getGridWidth(); col++) {
                Tile emptyTile = new EmptyTile(row, col);
                levelData.setTile(row, col, emptyTile);
                gridRectangles[row][col].setFill(emptyTile.getColor());
            }
        }
    }

    private boolean validateGrid() {
        boolean hasSpawn = false;
        boolean hasGoal = false;
        boolean hasRoad = false;

        for (int row = 0; row < levelData.getGridHeight(); row++) {
            for (int col = 0; col < levelData.getGridWidth(); col++) {
                Tile tile = levelData.getTile(row, col);
                if (tile instanceof SpawnTile) hasSpawn = true;
                if (tile instanceof GoalTile) hasGoal = true;
                if (tile instanceof RoadTile) hasRoad = true;
            }
        }
        return hasSpawn && hasGoal && hasRoad;
    }

    // ==================== STEP 3: Wave Settings ====================
    private void showStep3() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("STEP 3: WAVE SETTINGS");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #00E5FF; " +
                "-fx-padding: 15; -fx-background-color: #1a1a1a;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #2b2b2b; -fx-background-color: #2b2b2b;");
        scrollPane.setFitToWidth(true);

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(30));
        mainBox.setAlignment(Pos.TOP_CENTER);

        // Milestone Section
        VBox milestoneSection = createSection("STAR MILESTONES (reach these waves to earn stars)");
        milestoneBox = new VBox(10);
        milestoneFields = new ArrayList<>();

        Label milestoneHint = new Label("Each level has exactly 3 stars. Set the wave for each star below.");
        milestoneHint.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");
        milestoneBox.getChildren().add(milestoneHint);

        int starIndex = 1;
        for (WaveMilestone milestone : levelData.getWaveMilestones()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label label = new Label("Star " + starIndex + " wave:");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            label.setPrefWidth(120);

            TextField waveField = new TextField(String.valueOf(milestone.getWave()));
            waveField.setPrefWidth(100);
            milestoneFields.add(waveField);

            row.getChildren().addAll(label, waveField);
            milestoneBox.getChildren().add(row);
            starIndex++;
            if (starIndex > 3) break;
        }

        milestoneSection.getChildren().addAll(milestoneBox);

        // Spawn Density Section
        VBox densitySection = createSection("SPAWN DENSITY");
        HBox densityRow = new HBox(15);
        densityRow.setAlignment(Pos.CENTER_LEFT);

        Label densityLabel = new Label("Density:");
        densityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        densityLabel.setPrefWidth(120);

        densityComboBox = new ComboBox<>();
        densityComboBox.getItems().addAll("Low (1.0s)", "Medium (0.75s)", "High (0.5s)", "Extreme (0.25s)", "Custom");
        densityComboBox.setValue("Medium (0.75s)");
        densityComboBox.setPrefWidth(200);

        customIntervalField = new TextField("0.5");
        customIntervalField.setPrefWidth(100);
        customIntervalField.setPromptText("Seconds");
        customIntervalField.setDisable(true);

        densityComboBox.setOnAction(e -> {
            boolean isCustom = densityComboBox.getValue().equals("Custom");
            customIntervalField.setDisable(!isCustom);
        });

        densityRow.getChildren().addAll(densityLabel, densityComboBox, new Label("Custom:"), customIntervalField);
        densitySection.getChildren().add(densityRow);

        // Difficulty Section
        VBox difficultySection = createSection("DIFFICULTY SCALING");
        HBox difficultyRow = new HBox(15);
        difficultyRow.setAlignment(Pos.CENTER_LEFT);

        Label diffLabel = new Label("Modifier:");
        diffLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        diffLabel.setPrefWidth(120);

        difficultyField = new TextField("1.0");
        difficultyField.setPrefWidth(100);

        Label diffHint = new Label("(1.0 = 100% normal, 1.15 = 115% harder, 0.9 = 90% easier)");
        diffHint.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        difficultyRow.getChildren().addAll(diffLabel, difficultyField, diffHint);
        difficultySection.getChildren().add(difficultyRow);

        // Enemy Count Section
        VBox enemyCountSection = createSection("ENEMY COUNT PER WAVE");
        HBox countRow = new HBox(15);
        countRow.setAlignment(Pos.CENTER_LEFT);

        Label baseLabel = new Label("Base Count:");
        baseLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        baseLabel.setPrefWidth(120);

        baseEnemiesField = new TextField("10");
        baseEnemiesField.setPrefWidth(80);

        Label maxLabel = new Label("Max Count:");
        maxLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        maxEnemiesField = new TextField("100");
        maxEnemiesField.setPrefWidth(80);

        Label countHint = new Label("(Enemies grow from base to max over time)");
        countHint.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        countRow.getChildren().addAll(baseLabel, baseEnemiesField, maxLabel, maxEnemiesField, countHint);
        enemyCountSection.getChildren().add(countRow);

        // Inter-Wave Time Section
        VBox interWaveSection = createSection("INTER-WAVE TIME");
        HBox timeRow = new HBox(15);
        timeRow.setAlignment(Pos.CENTER_LEFT);

        Label timeLabel = new Label("Wait Time:");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        timeLabel.setPrefWidth(120);

        interWaveTimeField = new TextField("10.0");
        interWaveTimeField.setPrefWidth(80);

        Label timeHint = new Label("seconds between waves (player can skip for bonus)");
        timeHint.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        timeRow.getChildren().addAll(timeLabel, interWaveTimeField, timeHint);
        interWaveSection.getChildren().add(timeRow);

        // Enemy Weights Section
        VBox weightsSection = createSection("ENEMY COMPOSITION (must sum to 100%)");
        enemyWeightsBox = new VBox(10);
        weightRows = new ArrayList<>();

        // Create total label first so updateTotalLabel() can safely use it
        totalWeightLabel = new Label("Total: 0%");
        totalWeightLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14px; -fx-font-weight: bold;");
        totalWeightLabel.setId("totalLabel");

        // Add default weights from levelData
        for (EnemyWeight ew : levelData.getEnemyWeights()) {
            addWeightRow(ew.getEnemyType(), ew.getWeight());
        }

        HBox addButtonRow = new HBox(10);
        addButtonRow.setAlignment(Pos.CENTER_LEFT);

        Button addWeightButton = new Button("+ Add Enemy Type");
        addWeightButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addWeightButton.setOnAction(e -> addWeightRow("Regular", 0));

        addButtonRow.getChildren().addAll(addWeightButton, totalWeightLabel);

        weightsSection.getChildren().addAll(enemyWeightsBox, addButtonRow);

        // Milestone Section
        VBox milestoneSection = createSection("STAR MILESTONES (reach these waves to earn stars)");
        milestoneBox = new VBox(10);
        milestoneFields = new ArrayList<>();

        Label milestoneHint = new Label("Each level has exactly 3 stars. Set the wave for each star below.");
        milestoneHint.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");
        milestoneBox.getChildren().add(milestoneHint);

        ArrayList<WaveMilestone> normalizedMilestones = WaveMilestone.normalize(levelData.getWaveMilestones());
        levelData.setWaveMilestones(normalizedMilestones);

        for (int i = 0; i < 3; i++) {
            WaveMilestone milestone = normalizedMilestones.get(i);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label label = new Label("Star " + (i + 1) + " wave:");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            label.setPrefWidth(120);

            TextField waveField = new TextField(String.valueOf(milestone.getWave()));
            waveField.setPrefWidth(100);
            milestoneFields.add(waveField);

            row.getChildren().addAll(label, waveField);
            milestoneBox.getChildren().add(row);
        }

        milestoneSection.getChildren().addAll(milestoneBox);

        mainBox.getChildren().addAll(
                milestoneSection, densitySection, difficultySection, enemyCountSection,
                interWaveSection, weightsSection
        );

        scrollPane.setContent(mainBox);
        root.setCenter(scrollPane);

        // Bottom buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: #1a1a1a;");

        Button backButton = new Button("◄ BACK TO GRID");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white; -fx-background-radius: 8;");
        backButton.setOnAction(e -> showStep2());

        Button saveButton = new Button("NEXT: Quest Setup ►");
        saveButton.setPrefWidth(180);
        saveButton.setPrefHeight(50);
        saveButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E676; -fx-text-fill: black; -fx-background-radius: 8;");
        saveButton.setOnAction(e -> {
            if (validateAndApplyWaveSettings()) {
                showStep4();
            }
        });

        buttonBox.getChildren().addAll(backButton, saveButton);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 1280, 820);
        stage.setScene(scene);

        // Set existing values if editing
        applyExistingWaveSettings();

        // Ensure total label reflects any preloaded rows
        updateTotalLabel();
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #333333; -fx-padding: 15; -fx-background-radius: 8;");
        section.setMaxWidth(800);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px; -fx-font-weight: bold;");

        section.getChildren().add(titleLabel);
        return section;
    }

    private void addWeightRow(String enemyType, int weight) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Regular", "Fast", "Strong");
        typeCombo.setValue(enemyType);
        typeCombo.setPrefWidth(150);

        TextField weightField = new TextField(String.valueOf(weight));
        weightField.setPrefWidth(80);
        weightField.setPromptText("%");

        Label percentLabel = new Label("%");
        percentLabel.setStyle("-fx-text-fill: white;");

        Button removeButton = new Button("✕");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeButton.setOnAction(e -> {
            enemyWeightsBox.getChildren().remove(row);
            weightRows.remove(row);
            updateTotalLabel();
        });

        weightField.textProperty().addListener((obs, oldVal, newVal) -> updateTotalLabel());

        row.getChildren().addAll(typeCombo, weightField, percentLabel, removeButton);
        enemyWeightsBox.getChildren().add(row);
        weightRows.add(row);
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        int total = 0;
        for (HBox row : weightRows) {
            TextField weightField = (TextField) row.getChildren().get(1);
            try {
                String txt = weightField.getText().trim();
                if (!txt.isEmpty()) {
                    total += Integer.parseInt(txt);
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        if (totalWeightLabel != null) {
            totalWeightLabel.setText("Total: " + total + "%");
            if (total == 100) {
                totalWeightLabel.setStyle("-fx-text-fill: #00E676; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                totalWeightLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14px; -fx-font-weight: bold;");
            }
        }
    }

    private void applyExistingWaveSettings() {
        // Apply density
        SpawnDensity d = levelData.getSpawnDensity();
        switch (d) {
            case LOW: densityComboBox.setValue("Low (1.0s)"); break;
            case MEDIUM: densityComboBox.setValue("Medium (0.75s)"); break;
            case HIGH: densityComboBox.setValue("High (0.5s)"); break;
            case EXTREME: densityComboBox.setValue("Extreme (0.25s)"); break;
            case CUSTOM:
                densityComboBox.setValue("Custom");
                customIntervalField.setDisable(false);
                break;
        }

        customIntervalField.setText(String.valueOf(levelData.getCustomSpawnInterval()));
        difficultyField.setText(String.valueOf(levelData.getDifficultyModifier()));
        baseEnemiesField.setText(String.valueOf(levelData.getBaseEnemyCount()));
        maxEnemiesField.setText(String.valueOf(levelData.getMaxEnemyCount()));
        interWaveTimeField.setText(String.valueOf(levelData.getInterWaveTime()));
    }

    private boolean validateAndApplyWaveSettings() {
        try {
            // Parse density
            String densityStr = densityComboBox.getValue();
            SpawnDensity density;
            if (densityStr.startsWith("Low")) density = SpawnDensity.LOW;
            else if (densityStr.startsWith("Medium")) density = SpawnDensity.MEDIUM;
            else if (densityStr.startsWith("High")) density = SpawnDensity.HIGH;
            else if (densityStr.startsWith("Extreme")) density = SpawnDensity.EXTREME;
            else density = SpawnDensity.CUSTOM;

            double customInterval = Double.parseDouble(customIntervalField.getText().trim());
            double difficulty = Double.parseDouble(difficultyField.getText().trim());
            int baseCount = Integer.parseInt(baseEnemiesField.getText().trim());
            int maxCount = Integer.parseInt(maxEnemiesField.getText().trim());
            double interWaveTime = Double.parseDouble(interWaveTimeField.getText().trim());

            // Validate weights sum to 100
            int totalWeight = 0;
            ArrayList<EnemyWeight> weights = new ArrayList<>();
            for (HBox row : weightRows) {
                @SuppressWarnings("unchecked")
                ComboBox<String> typeCombo = (ComboBox<String>) row.getChildren().get(0);
                TextField weightField = (TextField) row.getChildren().get(1);

                String type = typeCombo.getValue();
                int weight = Integer.parseInt(weightField.getText().trim());
                totalWeight += weight;
                weights.add(new EnemyWeight(type, weight));
            }

            if (totalWeight != 100) {
                showError("Enemy weights must sum to 100%! Current total: " + totalWeight + "%");
                return false;
            }

            if (weights.isEmpty()) {
                showError("Please add at least one enemy type!");
                return false;
            }

            if (baseCount < 1 || maxCount < 1) {
                showError("Enemy counts must be at least 1!");
                return false;
            }

            if (baseCount > maxCount) {
                showError("Base enemy count cannot be greater than max!");
                return false;
            }

            // Apply settings
            levelData.setSpawnDensity(density);
            levelData.setCustomSpawnInterval(customInterval);
            levelData.setDifficultyModifier(difficulty);
            levelData.setBaseEnemyCount(baseCount);
            levelData.setMaxEnemyCount(maxCount);
            levelData.setInterWaveTime(interWaveTime);
            levelData.setEnemyWeights(weights);

            return true;

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields!");
            return false;
        }
    }

    // ==================== STEP 4: Quest Settings ====================
    private void showStep4() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("STEP 4: QUEST SETUP");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #00E5FF; " +
                "-fx-padding: 15; -fx-background-color: #1a1a1a;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #2b2b2b; -fx-background-color: #2b2b2b;");
        scrollPane.setFitToWidth(true);

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(30));
        mainBox.setAlignment(Pos.TOP_CENTER);

        // Gold Drop Chance Section
        VBox goldSection = createSection("GOLD DROP CHANCE");
        HBox goldRow = new HBox(15);
        goldRow.setAlignment(Pos.CENTER_LEFT);

        Label goldLabel = new Label("Drop Chance:");
        goldLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        goldLabel.setPrefWidth(120);

        goldDropChanceField = new TextField("5");
        goldDropChanceField.setPrefWidth(80);

        Label goldPercent = new Label("%");
        goldPercent.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label goldHint = new Label("(Chance for enemies to drop meta-currency Gold)");
        goldHint.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        goldRow.getChildren().addAll(goldLabel, goldDropChanceField, goldPercent, goldHint);
        goldSection.getChildren().add(goldRow);

        // Quests Section
        VBox questsSection = createSection("LEVEL QUESTS (3 quests recommended for 3-star system)");
        questsBox = new VBox(15);
        questRows = new ArrayList<>();

        // Add existing quests or default quests
        if (levelData.getQuestDefinitions().isEmpty()) {
            // Add 3 default quests
            addQuestRow(QuestType.DESTROY_ENEMIES, 50, null, RewardType.GOLD, 10);
            addQuestRow(QuestType.GAIN_SCORE, 500, null, RewardType.SCALAR, 5);
            addQuestRow(QuestType.BUILD_TOWERS, 3, "Basic", RewardType.GOLD, 15);
        } else {
            for (QuestDefinition def : levelData.getQuestDefinitions()) {
                addQuestRow(def.getType(), def.getTargetValue(), def.getTargetSpecifier(),
                           def.getRewardType(), def.getRewardAmount());
            }
        }

        HBox addQuestButtonRow = new HBox(10);
        addQuestButtonRow.setAlignment(Pos.CENTER_LEFT);

        Button addQuestButton = new Button("+ Add Quest");
        addQuestButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addQuestButton.setOnAction(e -> addQuestRow(QuestType.DESTROY_ENEMIES, 50, null, RewardType.GOLD, 10));

        Label questCountLabel = new Label("Quests: " + questRows.size());
        questCountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        questCountLabel.setId("questCountLabel");

        addQuestButtonRow.getChildren().addAll(addQuestButton, questCountLabel);

        questsSection.getChildren().addAll(questsBox, addQuestButtonRow);

        mainBox.getChildren().addAll(goldSection, questsSection);
        scrollPane.setContent(mainBox);
        root.setCenter(scrollPane);

        // Bottom buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: #1a1a1a;");

        Button backButton = new Button("◄ BACK TO WAVES");
        backButton.setPrefWidth(180);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #666666; -fx-text-fill: white; -fx-background-radius: 8;");
        backButton.setOnAction(e -> showStep3());

        Button saveButton = new Button("💾 SAVE LEVEL");
        saveButton.setPrefWidth(180);
        saveButton.setPrefHeight(50);
        saveButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #00E676; -fx-text-fill: black; -fx-background-radius: 8;");
        saveButton.setOnAction(e -> {
            if (validateAndApplyQuestSettings()) {
                saveLevelToFile();
            }
        });

        buttonBox.getChildren().addAll(backButton, saveButton);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 1280, 820);
        stage.setScene(scene);

        // Apply existing gold drop chance
        goldDropChanceField.setText(String.valueOf((int)(levelData.getGoldDropChance() * 100)));
    }

    private void addQuestRow(QuestType type, int targetValue, String specifier, 
                            RewardType rewardType, int rewardAmount) {
        VBox questBox = new VBox(8);
        questBox.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-background-radius: 5;");

        // Row 1: Quest Type and Target
        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
            "Destroy Enemies", "Destroy Enemy Type", "Destroy With Tower",
            "Spend Coins Total", "Spend Coins On Tower", "Gain Score",
            "Build Towers", "Upgrade Any To Level", "Upgrade Tower To Level"
        );
        typeCombo.setValue(getQuestTypeDisplayName(type));
        typeCombo.setPrefWidth(180);

        Label targetLabel = new Label("Target:");
        targetLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextField targetField = new TextField(String.valueOf(targetValue));
        targetField.setPrefWidth(80);

        row1.getChildren().addAll(typeLabel, typeCombo, targetLabel, targetField);

        // Row 2: Specifier (enemy type / tower type)
        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER_LEFT);

        Label specLabel = new Label("Specifier:");
        specLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        ComboBox<String> specCombo = new ComboBox<>();
        specCombo.getItems().addAll("NONE", "Regular", "Fast", "Strong", "Basic", "Sniper", "Cannon", "Freezing");
        specCombo.setValue(specifier != null ? specifier : "NONE");
        specCombo.setPrefWidth(120);

        Label specHint = new Label("(Enemy or Tower type, use NONE if not needed)");
        specHint.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

        row2.getChildren().addAll(specLabel, specCombo, specHint);

        // Row 3: Reward
        HBox row3 = new HBox(10);
        row3.setAlignment(Pos.CENTER_LEFT);

        Label rewardLabel = new Label("Reward:");
        rewardLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        ComboBox<String> rewardCombo = new ComboBox<>();
        rewardCombo.getItems().addAll("GOLD", "SCALAR", "VECTOR", "MATRIX", "TENSOR", "INFIAR");
        rewardCombo.setValue(rewardType.name());
        rewardCombo.setPrefWidth(100);

        Label amountLabel = new Label("Amount:");
        amountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextField amountField = new TextField(String.valueOf(rewardAmount));
        amountField.setPrefWidth(60);

        Button removeButton = new Button("✕ Remove");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeButton.setOnAction(e -> {
            questsBox.getChildren().remove(questBox);
            questRows.remove(questBox);
            updateQuestCountLabel();
        });

        row3.getChildren().addAll(rewardLabel, rewardCombo, amountLabel, amountField, removeButton);

        questBox.getChildren().addAll(row1, row2, row3);
        questsBox.getChildren().add(questBox);
        questRows.add(questBox);
        updateQuestCountLabel();
    }

    private String getQuestTypeDisplayName(QuestType type) {
        switch (type) {
            case DESTROY_ENEMIES: return "Destroy Enemies";
            case DESTROY_ENEMY_TYPE: return "Destroy Enemy Type";
            case DESTROY_WITH_TOWER: return "Destroy With Tower";
            case SPEND_COINS_TOTAL: return "Spend Coins Total";
            case SPEND_COINS_ON_TOWER: return "Spend Coins On Tower";
            case GAIN_SCORE: return "Gain Score";
            case BUILD_TOWERS: return "Build Towers";
            case UPGRADE_ANY_TO_LEVEL: return "Upgrade Any To Level";
            case UPGRADE_TOWER_TO_LEVEL: return "Upgrade Tower To Level";
            default: return "Destroy Enemies";
        }
    }

    private QuestType getQuestTypeFromDisplayName(String name) {
        switch (name) {
            case "Destroy Enemies": return QuestType.DESTROY_ENEMIES;
            case "Destroy Enemy Type": return QuestType.DESTROY_ENEMY_TYPE;
            case "Destroy With Tower": return QuestType.DESTROY_WITH_TOWER;
            case "Spend Coins Total": return QuestType.SPEND_COINS_TOTAL;
            case "Spend Coins On Tower": return QuestType.SPEND_COINS_ON_TOWER;
            case "Gain Score": return QuestType.GAIN_SCORE;
            case "Build Towers": return QuestType.BUILD_TOWERS;
            case "Upgrade Any To Level": return QuestType.UPGRADE_ANY_TO_LEVEL;
            case "Upgrade Tower To Level": return QuestType.UPGRADE_TOWER_TO_LEVEL;
            default: return QuestType.DESTROY_ENEMIES;
        }
    }

    private void updateQuestCountLabel() {
        // Find and update quest count label in parent
        if (questsBox != null && questsBox.getParent() != null) {
            VBox parentSection = (VBox) questsBox.getParent();
            for (javafx.scene.Node node : parentSection.getChildren()) {
                if (node instanceof HBox) {
                    HBox hbox = (HBox) node;
                    for (javafx.scene.Node child : hbox.getChildren()) {
                        if (child instanceof Label && "questCountLabel".equals(child.getId())) {
                            ((Label) child).setText("Quests: " + questRows.size());
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean validateAndApplyQuestSettings() {
        try {
            // Parse gold drop chance
            double goldChance = Double.parseDouble(goldDropChanceField.getText().trim()) / 100.0;
            if (goldChance < 0 || goldChance > 1) {
                showError("Gold drop chance must be between 0 and 100!");
                return false;
            }

            // Parse milestones
            ArrayList<WaveMilestone> milestones = new ArrayList<>();
            for (TextField waveField : milestoneFields) {
                int wave = Integer.parseInt(waveField.getText().trim());

                if (wave <= 0) {
                    showError("Milestone wave must be at least 1!");
                    return false;
                }

                milestones.add(new WaveMilestone(wave));
            }

            milestones.sort(Comparator.comparingInt(WaveMilestone::getWave));

            // Parse quests
            ArrayList<QuestDefinition> questDefs = new ArrayList<>();

            for (VBox questBox : questRows) {
                HBox row1 = (HBox) questBox.getChildren().get(0);
                HBox row2 = (HBox) questBox.getChildren().get(1);
                HBox row3 = (HBox) questBox.getChildren().get(2);

                @SuppressWarnings("unchecked")
                ComboBox<String> typeCombo = (ComboBox<String>) row1.getChildren().get(1);
                TextField targetField = (TextField) row1.getChildren().get(3);

                @SuppressWarnings("unchecked")
                ComboBox<String> specCombo = (ComboBox<String>) row2.getChildren().get(1);

                @SuppressWarnings("unchecked")
                ComboBox<String> rewardCombo = (ComboBox<String>) row3.getChildren().get(1);
                TextField amountField = (TextField) row3.getChildren().get(3);

                QuestType questType = getQuestTypeFromDisplayName(typeCombo.getValue());
                int targetValue = Integer.parseInt(targetField.getText().trim());
                String specifier = specCombo.getValue().equals("NONE") ? null : specCombo.getValue();
                RewardType rewardType = RewardType.valueOf(rewardCombo.getValue());
                int rewardAmount = Integer.parseInt(amountField.getText().trim());

                if (targetValue <= 0) {
                    showError("Target value must be greater than 0!");
                    return false;
                }

                if (rewardAmount <= 0) {
                    showError("Reward amount must be greater than 0!");
                    return false;
                }

                questDefs.add(new QuestDefinition(questType, targetValue, specifier, rewardType, rewardAmount));
            }

            // Apply settings
            levelData.setGoldDropChance(goldChance);
            levelData.setQuestDefinitions(questDefs);
            if (milestones.isEmpty()) {
                levelData.setWaveMilestones(new ArrayList<>());
            } else {
                levelData.setWaveMilestones(milestones);
            }

            return true;

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields!");
            return false;
        }
    }

    // ==================== SAVE TO FILE ====================
    private void saveLevelToFile() {
        try {
            String filename = "level_" + levelData.getLevelNumber() + ".txt";
            FileWriter writer = new FileWriter(filename);

            // Basic info
            writer.write("LEVEL_NAME: " + levelData.getLevelName() + "\n");
            writer.write("LEVEL_NUMBER: " + levelData.getLevelNumber() + "\n");
            writer.write("GRID_WIDTH: " + levelData.getGridWidth() + "\n");
            writer.write("GRID_HEIGHT: " + levelData.getGridHeight() + "\n");
            writer.write("MAP_NODE_X: 0\n");
            writer.write("MAP_NODE_Y: 0\n");
            writer.write("\n");

            // Wave settings
            writer.write("SPAWN_DENSITY: " + levelData.getSpawnDensity().name() + "\n");
            writer.write("CUSTOM_INTERVAL: " + levelData.getCustomSpawnInterval() + "\n");
            writer.write("DIFFICULTY: " + levelData.getDifficultyModifier() + "\n");
            writer.write("BASE_ENEMIES: " + levelData.getBaseEnemyCount() + "\n");
            writer.write("MAX_ENEMIES: " + levelData.getMaxEnemyCount() + "\n");
            writer.write("INTER_WAVE_TIME: " + levelData.getInterWaveTime() + "\n");
            writer.write("\n");

            // Enemy weights
            for (EnemyWeight ew : levelData.getEnemyWeights()) {
                writer.write("ENEMY_WEIGHT:" + ew.getEnemyType() + ":" + ew.getWeight() + "\n");
            }
            writer.write("\n");

            // Quest settings
            writer.write("GOLD_DROP_CHANCE: " + levelData.getGoldDropChance() + "\n");
            for (WaveMilestone milestone : levelData.getWaveMilestones()) {
                writer.write("WAVE_MILESTONE:" + milestone.getWave() + "\n");
            }
            for (QuestDefinition quest : levelData.getQuestDefinitions()) {
                writer.write("QUEST:" + quest.toFileString() + "\n");
            }
            writer.write("\n");

            // Grid
            writer.write("GRID:\n");
            for (int row = 0; row < levelData.getGridHeight(); row++) {
                for (int col = 0; col < levelData.getGridWidth(); col++) {
                    writer.write(levelData.getTile(row, col).getTypeId() + "");
                    if (col < levelData.getGridWidth() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }

            writer.close();

            // Update metadata
            editingLevel.filename = filename;

            if (!isEditMode) {
                levels.add(editingLevel);
            }

            saveLevelsMetadata();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Level Saved Successfully!");
            alert.setContentText("Level saved to: " + filename);
            alert.showAndWait();

            System.out.println("Level saved to " + filename);
            mainEditor.showMainMenu();

        } catch (IOException e) {
            showError("Failed to save level: " + e.getMessage());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

enum TileType {
    EMPTY("Empty"),
    ROAD("Road"),
    PLATFORM("Platform"),
    SPAWN("Spawn"),
    GOAL("Goal");

    private String name;

    TileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
