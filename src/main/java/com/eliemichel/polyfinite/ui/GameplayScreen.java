package com.eliemichel.polyfinite.ui;

import com.eliemichel.polyfinite.game.*;
import com.eliemichel.polyfinite.game.tiles.Tile;
import com.eliemichel.polyfinite.game.towers.Tower;
import com.eliemichel.polyfinite.ui.gameplay.*;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class GameplayScreen {

    private Stage stage;
    private LevelInfo levelInfo;
    private SaveSlot currentSave;
    private Canvas canvas;
    private int tileSize = 40;

    private LevelLoader levelLoader;
    private GameCamera camera;
    private WaveManager waveManager;
    private GameRenderer renderer;
    private TowerPanelManager towerPanelManager;
    private QuestManager questManager;
    private VBox questPanel;

    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private ArrayList<Projectile> projectiles;

    private ArrayList<WaveMilestone> waveMilestones;
    private ArrayList<Integer> milestonesReached;
    private int milestoneStarsEarned;
    private int lastWaveChecked;

    // OPTIMIZATION: Reusable list to avoid garbage collection
    private ArrayList<Enemy> enemiesToRemove;

    private enum GameState { PREPARING, PLAYING, PAUSED, VICTORY, DEFEAT }
    private GameState gameState;

    private int lives;
    private int paperMoney;  // In-level currency (renamed from gold)
    private int score;
    private long lastUpdateTime;
    private Random random = new Random();

    private Label livesLabel;
    private Label paperMoneyLabel;  // Renamed from goldLabel
    private Label waveLabel;
    private Label scoreLabel;
    private Button startWaveButton;
    private ArrayList<Label> starTrackerIcons;
    private Label nextStarLabel;

    private VBox towerSelectionPanel;
    private Group canvasGroup;
    private StackPane root;
    private EndLevelScreen endLevelScreen;
    private boolean levelEnded = false;

    private int selectedTileRow = -1;
    private int selectedTileCol = -1;
    private Tower selectedTower = null;

    public GameplayScreen(Stage stage, LevelInfo levelInfo, SaveSlot currentSave) {
        this.stage = stage;
        this.levelInfo = levelInfo;
        this.currentSave = currentSave;
        this.enemies = new ArrayList<>();
        this.towers = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.enemiesToRemove = new ArrayList<>(); // OPTIMIZATION: Initialize once

        this.gameState = GameState.PREPARING;
        this.lives = 20;
        this.paperMoney = 200;
        this.score = 0;

        initializeLevel();
    }

    private void initializeLevel() {
        levelLoader = new LevelLoader();
        levelLoader.loadLevel(levelInfo.getLevelNumber());
        levelLoader.findSpawnAndGoal();
        levelLoader.createEnemyPath();

        waveManager = new WaveManager(
                levelLoader.getLevelData(),
                enemies,
                levelLoader.getSpawnTile(),
                levelLoader.getEnemyPath(),
                tileSize
        );

        waveMilestones = WaveMilestone.normalize(levelLoader.getLevelData().getWaveMilestones());
        levelInfo.setWaveMilestones(new ArrayList<>(waveMilestones));
        milestonesReached = new ArrayList<>();
        milestoneStarsEarned = 0;
        lastWaveChecked = 0;

        // Initialize quest manager
        questManager = new QuestManager(levelInfo.getLevelNumber(), currentSave.getSlotNumber());
        questManager.initializeQuests(levelLoader.getLevelData().getQuestDefinitions());
        questManager.setOnQuestCompleted(() -> {
            System.out.println("A quest was completed!");
            refreshQuestPanel();
        });
        questManager.setOnQuestProgressChanged(this::refreshQuestPanel);

        // Set up skip bonus callback
        waveManager.setOnSkipBonus(() -> {
            int bonus = waveManager.getSkipBonusAmount();
            paperMoney += bonus;
            updatePaperMoneyLabel();
            System.out.println("Skip bonus applied: +" + bonus + " gold");
        });
    }

    public void show() {
        double extraSpace = 500;
        double levelWidth = levelLoader.getLevelData().getGridWidth() * tileSize;
        double levelHeight = levelLoader.getLevelData().getGridHeight() * tileSize;

        canvas = new Canvas(levelWidth + extraSpace * 2, levelHeight + extraSpace * 2);
        canvasGroup = new Group(canvas);

        camera = new GameCamera(canvasGroup);
        renderer = new GameRenderer(levelLoader.getLevelData(), tileSize, extraSpace, extraSpace);

        StackPane canvasContainer = new StackPane(canvasGroup);
        canvasContainer.setStyle("-fx-background-color: #000000;");

        livesLabel = new Label();
        paperMoneyLabel = new Label();
        waveLabel = new Label();
        scoreLabel = new Label();
        startWaveButton = new Button();

        HBox topBar = UIBuilder.createTopBar(livesLabel, paperMoneyLabel, waveLabel, scoreLabel,
                this::togglePause, waveManager.getCurrentWave(), lives, paperMoney, score, createStarTracker());
        topBar.setPickOnBounds(false);

        HBox bottomBar = UIBuilder.createBottomBar(startWaveButton, this::startNextWave, this::toggleSpeed);
        bottomBar.setPickOnBounds(false);

        BorderPane uiOverlay = new BorderPane();
        uiOverlay.setStyle("-fx-background-color: transparent;");
        uiOverlay.setPickOnBounds(false);
        uiOverlay.setTop(topBar);
        uiOverlay.setBottom(bottomBar);
        setupQuestPanel(uiOverlay);

        towerSelectionPanel = UIBuilder.createTowerSelectionPanel();
        towerPanelManager = new TowerPanelManager(towerSelectionPanel, towers, tileSize);
        towerPanelManager.setGold(paperMoney);
        towerPanelManager.setQuestManager(questManager);
        towerPanelManager.setOnGoldUpdate(() -> {
            paperMoney = towerPanelManager.getGold();
            updatePaperMoneyLabel();
        });
        towerPanelManager.setOnPanelHide(() -> {
            selectedTileRow = -1;
            selectedTileCol = -1;
            selectedTower = null;
        });

        root = new StackPane();
        root.setStyle("-fx-background-color: #000000;");
        root.getChildren().addAll(canvasContainer, uiOverlay, towerSelectionPanel);

        root.setOnScroll(e -> camera.handleZoom(e.getDeltaY()));
        root.setOnMousePressed(e -> camera.handleDragStart(e.getX(), e.getY()));
        root.setOnMouseDragged(e -> {
            if (camera.isDragging()) {
                camera.handleDrag(e.getX(), e.getY());
            }
        });
        root.setOnMouseReleased(e -> camera.handleDragEnd());
        root.setOnMouseClicked(e -> {
            if (camera.getTotalDragDistance() < 5) {
                handleClick(e.getX(), e.getY());
            }
        });

        stage.getScene().setFill(javafx.scene.paint.Color.BLACK);
        root.setOpacity(0);
        stage.getScene().setRoot(root);
        stage.setFullScreen(true);

        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                javafx.util.Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        fadeIn.setOnFinished(e -> startGameLoop());

        updateAllLabels();
    }

    private void handleClick(double x, double y) {
        int[] tile = camera.screenToTile(x, y, levelLoader.getLevelData().getGridWidth(),
                levelLoader.getLevelData().getGridHeight(), tileSize);

        if (tile == null) {
            towerPanelManager.hidePanel();
            return;
        }

        int row = tile[0];
        int col = tile[1];

        Tower clickedTower = getTowerAt(row, col);
        if (clickedTower != null) {
            selectedTower = clickedTower;
            selectedTileRow = -1;
            selectedTileCol = -1;
            towerPanelManager.setSelectedTower(clickedTower);
            towerPanelManager.showPanel();
            System.out.println("Clicked on tower: " + clickedTower.getTowerName());
            return;
        }

        Tile clickedTile = levelLoader.getLevelData().getTile(row, col);
        if (clickedTile != null && clickedTile.canPlaceTower()) {
            selectedTileRow = row;
            selectedTileCol = col;
            selectedTower = null;
            towerPanelManager.setSelectedTile(row, col);
            towerPanelManager.setSelectedTower(null);
            towerPanelManager.showPanel();
            return;
        }

        towerPanelManager.hidePanel();
        selectedTileRow = -1;
        selectedTileCol = -1;
        selectedTower = null;
    }

    private Tower getTowerAt(int row, int col) {
        for (int i = 0; i < towers.size(); i++) {
            Tower tower = towers.get(i);
            if (tower.getRow() == row && tower.getCol() == col) {
                return tower;
            }
        }
        return null;
    }

    private void startGameLoop() {
        lastUpdateTime = System.nanoTime();

        AnimationTimer gameLoop = new AnimationTimer() {
            private long frameCount = 0;
            private long totalUpdateTime = 0;
            private long totalRenderTime = 0;
            private long lastPrintTime = System.nanoTime();

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastUpdateTime) / 1000000000.0;
                lastUpdateTime = now;

                // PROFILING: Measure update time
                long updateStart = System.nanoTime();
                update(deltaTime);  // <-- Calls your existing update() method
                long updateEnd = System.nanoTime();
                totalUpdateTime += (updateEnd - updateStart);

                // PROFILING: Measure render time
                long renderStart = System.nanoTime();
                render();  // <-- Calls your existing render() method
                long renderEnd = System.nanoTime();
                totalRenderTime += (renderEnd - renderStart);

                frameCount++;

                // Print profiling stats every second
                if (now - lastPrintTime >= 1_000_000_000) {
                    long avgUpdateTime = totalUpdateTime / frameCount;
                    long avgRenderTime = totalRenderTime / frameCount;

                    System.out.println("=== PERFORMANCE STATS ===");
                    System.out.println("Frames: " + frameCount);
                    System.out.println("Enemies: " + enemies.size());
                    System.out.println("Towers: " + towers.size());
                    System.out.println("Projectiles: " + projectiles.size());
                    System.out.println("Avg Update Time: " + (avgUpdateTime / 1_000_000.0) + "ms");
                    System.out.println("Avg Render Time: " + (avgRenderTime / 1_000_000.0) + "ms");
                    System.out.println("Total Frame Time: " + ((avgUpdateTime + avgRenderTime) / 1_000_000.0) + "ms");
                    System.out.println("FPS: " + frameCount);
                    System.out.println("========================");

                    // Reset counters
                    frameCount = 0;
                    totalUpdateTime = 0;
                    totalRenderTime = 0;
                    lastPrintTime = now;
                }
            }
        };
        gameLoop.start();
    }

    // OPTIMIZED UPDATE METHOD
    private void update(double deltaTime) {
        long updateTotalStart = System.nanoTime();

        if (gameState == GameState.PAUSED || gameState == GameState.VICTORY || gameState == GameState.DEFEAT) {
            return;
        }

        // PROFILE: Enemy updates
        long enemyStart = System.nanoTime();
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update();

            if (enemy.hasReachedGoal()) {
                lives--;
                enemies.remove(i);
                updateLivesLabel();

                if (lives <= 0 && !levelEnded) {
                    levelEnded = true;
                    showEndLevelScreen();
                }
            }
            else if (!enemy.isAlive()) {
                // Find which tower killed this enemy (from last projectile hit)
                String killerTowerType = enemy.getLastHitByTower();
                
                paperMoney += enemy.getGoldReward();
                score += 10;
                
                // Check for meta-currency gold drop
                double goldDropChance = levelLoader.getLevelData().getGoldDropChance();
                if (random.nextDouble() < goldDropChance) {
                    PlayerCurrencies.getInstance().addCurrency(RewardType.GOLD, 1);
                    System.out.println("Enemy dropped 1 Gold!");
                }
                
                // Fire quest event
                questManager.onEnemyKilled(enemy.getEnemyType(), killerTowerType);
                
                enemies.remove(i);
                updatePaperMoneyLabel();
                updateScoreLabel();
                
                // Update score quest
                questManager.onScoreChanged(score);
            }
        }
        long enemyEnd = System.nanoTime();

        // PROFILE: Tower updates
        long towerStart = System.nanoTime();
        for (int i = 0; i < towers.size(); i++) {
            towers.get(i).update(deltaTime, enemies, projectiles);
        }
        long towerEnd = System.nanoTime();

        // PROFILE: Projectile updates
        long projStart = System.nanoTime();
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update();
            if (!p.isActive()) {
                projectiles.remove(i);
            }
        }
        long projEnd = System.nanoTime();

        // PROFILE: WaveManager update
        long waveStart = System.nanoTime();
        waveManager.update(deltaTime);
        checkWaveMilestones();
        long waveEnd = System.nanoTime();

        // PROFILE: State checks
        long stateCheckStart = System.nanoTime();
        if (waveManager.isWaiting()) {
            gameState = GameState.PREPARING;
            updateStartWaveButton(); // Update timer display every frame
        } else if (waveManager.isIdle()) {
            gameState = GameState.PREPARING;
        }
        long stateCheckEnd = System.nanoTime();

        // PROFILE: Panel update
        long panelStart = System.nanoTime();
        towerPanelManager.setGold(paperMoney);
        long panelEnd = System.nanoTime();

        long updateTotalEnd = System.nanoTime();

        // DETAILED PROFILING OUTPUT
        if (enemies.size() > 400) {
            double enemyTime = (enemyEnd - enemyStart) / 1_000_000.0;
            double towerTime = (towerEnd - towerStart) / 1_000_000.0;
            double projTime = (projEnd - projStart) / 1_000_000.0;
            double waveTime = (waveEnd - waveStart) / 1_000_000.0;
            double stateTime = (stateCheckEnd - stateCheckStart) / 1_000_000.0;
            double panelTime = (panelEnd - panelStart) / 1_000_000.0;
            double totalTime = (updateTotalEnd - updateTotalStart) / 1_000_000.0;
            double unaccountedTime = totalTime - (enemyTime + towerTime + projTime + waveTime + stateTime + panelTime);

            System.out.println(">>> ULTRA DETAILED PROFILING (" + enemies.size() + " enemies):");
            System.out.println("  Enemy Updates:   " + String.format("%.4f", enemyTime) + "ms");
            System.out.println("  Tower Updates:   " + String.format("%.4f", towerTime) + "ms");
            System.out.println("  Projectiles:     " + String.format("%.4f", projTime) + "ms");
            System.out.println("  WaveManager:     " + String.format("%.4f", waveTime) + "ms");
            System.out.println("  State Checks:    " + String.format("%.4f", stateTime) + "ms");
            System.out.println("  Panel Update:    " + String.format("%.4f", panelTime) + "ms");
            System.out.println("  UNACCOUNTED:     " + String.format("%.4f", unaccountedTime) + "ms ⚠️");
            System.out.println("  TOTAL:           " + String.format("%.4f", totalTime) + "ms");
            System.out.println();
        }
    }


    private void render() {
        renderer.render(canvas.getGraphicsContext2D(), towers, projectiles, enemies,
                selectedTower, selectedTileRow, selectedTileCol, towerPanelManager.getTowerTypeToPlace());
    }


    private void startNextWave() {
        if (waveManager.canStartWave()) {
            waveManager.playerStartWave();
            gameState = GameState.PLAYING;
            updateWaveLabel();
            updateStartWaveButton();
        }
    }

    private void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }

    private void toggleSpeed() {
        System.out.println("Speed toggle clicked - TODO");
    }

    private void showEndLevelScreen() {
        // Save quest progress
        questManager.onLevelEnd();

        int finalWave = waveManager.getCurrentWave();
        levelInfo.updateProgress(finalWave, score, milestoneStarsEarned);

        currentSave.saveLevelProgress(levelInfo.getLevelNumber(), finalWave,
                score, milestoneStarsEarned, questManager.getQuests(), waveMilestones);

        endLevelScreen = new EndLevelScreen(stage, currentSave, waveManager.getCurrentWave(),
                waveManager.getTotalWaves(), score, milestoneStarsEarned);
        root.getChildren().add(endLevelScreen.getOverlay());
    }

    private void updateAllLabels() {
        updateLivesLabel();
        updatePaperMoneyLabel();
        updateWaveLabel();
        updateScoreLabel();
        updateStartWaveButton();
        updateStarTracker();
    }

    private void updateLivesLabel() {
        if (livesLabel != null) {
            livesLabel.setText("❤️ Lives: " + lives);
        }
    }

    private void updatePaperMoneyLabel() {
        if (paperMoneyLabel != null) {
            paperMoneyLabel.setText("💵 " + paperMoney);
        }
    }

    private void updateWaveLabel() {
        if (waveLabel != null) {
            waveLabel.setText("🌊 Wave: " + waveManager.getCurrentWave());
        }
    }

    private void updateScoreLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText("⭐ " + score);
        }
    }

    private VBox createStarTracker() {
        VBox tracker = new VBox(6);
        tracker.setPadding(new Insets(8, 12, 8, 12));
        tracker.setAlignment(Pos.CENTER_LEFT);
        tracker.setStyle("-fx-background-color: #1f1f1f; -fx-border-color: #FFD700; -fx-border-width: 1; -fx-background-radius: 4; -fx-border-radius: 4;");

        Label title = new Label("⭐ Star Tracker");
        title.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox starsRow = new HBox(6);
        starsRow.setAlignment(Pos.CENTER_LEFT);
        starTrackerIcons = new ArrayList<>();
        for (int i = 0; i < waveMilestones.size(); i++) {
            Label starIcon = new Label("☆ W" + waveMilestones.get(i).getWave());
            starIcon.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");
            starTrackerIcons.add(starIcon);
            starsRow.getChildren().add(starIcon);
        }

        nextStarLabel = new Label();
        nextStarLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12px;");

        tracker.getChildren().addAll(title, starsRow, nextStarLabel);
        updateStarTracker();
        return tracker;
    }

    private void updateStarTracker() {
        if (starTrackerIcons == null || waveMilestones == null || waveManager == null) {
            return;
        }

        int currentWave = waveManager.getCurrentWave();
        for (int i = 0; i < waveMilestones.size() && i < starTrackerIcons.size(); i++) {
            WaveMilestone milestone = waveMilestones.get(i);
            Label icon = starTrackerIcons.get(i);
            boolean reached = milestone.isReached(currentWave);
            icon.setText((reached ? "⭐ " : "☆ ") + "W" + milestone.getWave());
            icon.setStyle("-fx-text-fill: " + (reached ? "#00E676" : "#AAAAAA") + "; -fx-font-size: 12px; -fx-font-weight: " + (reached ? "bold" : "normal") + ";");
        }

        WaveMilestone next = null;
        for (WaveMilestone milestone : waveMilestones) {
            if (!milestone.isReached(currentWave)) {
                next = milestone;
                break;
            }
        }

        if (next != null) {
            nextStarLabel.setText("Next star at wave " + next.getWave() + " (current: " + currentWave + ")");
        } else {
            nextStarLabel.setText("All 3 stars earned!");
        }
    }

    private void setupQuestPanel(BorderPane uiOverlay) {
        questPanel = new VBox(8);
        questPanel.setPadding(new Insets(12));
        questPanel.setMaxWidth(260);
        questPanel.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-border-color: #B388FF; -fx-border-width: 2; -fx-background-radius: 4; -fx-border-radius: 4;");
        questPanel.setPickOnBounds(false);

        refreshQuestPanel();

        uiOverlay.setRight(questPanel);
        BorderPane.setMargin(questPanel, new Insets(10, 20, 10, 10));
    }

    private void refreshQuestPanel() {
        if (questPanel == null) {
            return;
        }

        questPanel.getChildren().clear();

        Label title = new Label("🎯 ACTIVE QUESTS");
        title.setStyle("-fx-text-fill: #B388FF; -fx-font-size: 14px; -fx-font-weight: bold;");
        questPanel.getChildren().add(title);

        ArrayList<Quest> activeQuests = questManager != null ? questManager.getQuests() : new ArrayList<>();
        if (activeQuests.isEmpty()) {
            Label emptyLabel = new Label("No quests available");
            emptyLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");
            questPanel.getChildren().add(emptyLabel);
            return;
        }

        int index = 1;
        for (Quest quest : activeQuests) {
            VBox questBox = new VBox(2);
            questBox.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 4; -fx-padding: 6;");

            Label desc = new Label("Q" + index + ": " + quest.getDescription());
            desc.setStyle("-fx-text-fill: " + (quest.isCompleted() ? "#00E676" : "white") + "; -fx-font-size: 12px;");

            Label progress = new Label(quest.getProgressString());
            progress.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px;");

            questBox.getChildren().addAll(desc, progress);
            questPanel.getChildren().add(questBox);
            index++;
        }
    }

    private void checkWaveMilestones() {
        if (waveMilestones == null || waveMilestones.isEmpty()) {
            return;
        }

        int currentWave = waveManager.getCurrentWave();
        if (currentWave <= lastWaveChecked) {
            return;
        }

        lastWaveChecked = currentWave;
        updateWaveLabel();
        updateStarTracker();

        for (WaveMilestone milestone : waveMilestones) {
            if (milestone.isReached(currentWave) && !milestonesReached.contains(milestone.getWave())) {
                milestonesReached.add(milestone.getWave());
                milestoneStarsEarned = Math.min(3, milestoneStarsEarned + milestone.getStarsReward());
                System.out.println("Reached wave milestone " + milestone.getWave() + " (+" + milestone.getStarsReward() + " star)");
                updateStarTracker();
            }
        }
    }

    private void updateStartWaveButton() {
        if (startWaveButton != null) {
            if (waveManager.isIdle()) {
                startWaveButton.setText("▶️ START WAVE 1");
                startWaveButton.setDisable(false);
            } else if (waveManager.isWaiting()) {
                int timeLeft = (int) Math.ceil(waveManager.getInterWaveTimer());
                int bonus = waveManager.getSkipBonusAmount();
                startWaveButton.setText("⏩ SKIP (" + timeLeft + "s) +💰" + bonus);
                startWaveButton.setDisable(false);
            } else if (waveManager.isSpawning() || gameState == GameState.PLAYING) {
                startWaveButton.setText("⏳ WAVE IN PROGRESS");
                startWaveButton.setDisable(true);
            } else {
                startWaveButton.setText("▶️ START WAVE " + (waveManager.getCurrentWave() + 1));
                startWaveButton.setDisable(false);
            }
        }
    }
}