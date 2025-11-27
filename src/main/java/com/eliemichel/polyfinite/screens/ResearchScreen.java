package com.eliemichel.polyfinite.screens;

import com.eliemichel.polyfinite.game.ResearchManager;
import com.eliemichel.polyfinite.game.SaveSlot;
import com.eliemichel.polyfinite.ui.MapScreen;
import com.eliemichel.polyfinite.ui.ScreenTransition;
import com.eliemichel.polyfinite.utils.AtlasManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class ResearchScreen {
    private Stage stage;
    private SaveSlot currentSave;
    private ResearchManager researchManager;
    private ScreenTransition transition;

    private Pane treePane;
    private Canvas connectionCanvas;
    private HashMap<String, ResearchNode> allNodes;
    private ArrayList<NodeConnection> connections;

    private VBox infoPanel;
    private Label infoTitle;
    private Label infoDesc;
    private Label infoLevel;
    private Label infoEffect;
    private Button upgradeButton;
    private ResearchNode selectedNode = null;

    private double dragStartX;
    private double dragStartY;
    private double offsetX = 0;
    private double offsetY = 0;
    private boolean isDragging = false;
    private double totalDragDistance = 0;

    private double nodeSize = 70;
    private double startX = 800;
    private double baseY = 900;
    private double horizontalSpacing = 600;
    private double clusterVerticalOffset = 220;
    private double verticalSpacing = 160;
    private double horizontalSpread = 130;
    private double globalOffsetX = -100;
    private double globalOffsetY = 0;
    private double globalRadius = 220;
    private double canvasWidth = 6000;
    private double canvasHeight = 2500;

    private Color bgColor = Color.rgb(30, 32, 36);
    private Color lineColor = Color.rgb(80, 82, 86);
    private Color textColor = Color.rgb(220, 220, 220);

    public ResearchScreen(Stage stage, SaveSlot save) {
        this.stage = stage;
        this.currentSave = save;
        this.researchManager = ResearchManager.getInstance();
        this.transition = new ScreenTransition(stage);
        this.allNodes = new HashMap<>();
        this.connections = new ArrayList<>();
        this.researchManager.loadResearch(save.getSlotNumber());
    }

    public void show() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: rgb(30, 32, 36);");

        HBox topBar = createTopBar();
        borderPane.setTop(topBar);

        Pane canvasContainer = new Pane();
        canvasContainer.setStyle("-fx-background-color: rgb(30, 32, 36);");

        connectionCanvas = new Canvas(canvasWidth, canvasHeight);
        treePane = new Pane();
        treePane.setPrefSize(canvasWidth, canvasHeight);

        canvasContainer.getChildren().addAll(connectionCanvas, treePane);

        canvasContainer.setOnMousePressed(e -> {
            dragStartX = e.getX();
            dragStartY = e.getY();
            isDragging = true;
            totalDragDistance = 0;
        });

        canvasContainer.setOnMouseDragged(e -> {
            if (!isDragging) return;

            double deltaX = e.getX() - dragStartX;
            double deltaY = e.getY() - dragStartY;

            totalDragDistance += Math.abs(deltaX) + Math.abs(deltaY);

            offsetX += deltaX;
            offsetY += deltaY;

            offsetX = Math.max(-4500, Math.min(800, offsetX));
            offsetY = Math.max(-1000, Math.min(1000, offsetY));

            treePane.setTranslateX(offsetX);
            treePane.setTranslateY(offsetY);
            connectionCanvas.setTranslateX(offsetX);
            connectionCanvas.setTranslateY(offsetY);

            dragStartX = e.getX();
            dragStartY = e.getY();
        });

        canvasContainer.setOnMouseReleased(e -> {
            isDragging = false;
        });

        canvasContainer.setOnMouseClicked(e -> {
            if (totalDragDistance < 5) {
                hideInfoPanel();
            }
        });

        infoPanel = createInfoPanel();

        HBox contentArea = new HBox(0);
        HBox.setHgrow(canvasContainer, Priority.ALWAYS);
        contentArea.getChildren().add(canvasContainer);

        borderPane.setCenter(contentArea);

        HBox bottomBar = createBottomBar();
        borderPane.setBottom(bottomBar);

        buildResearchTree();
        drawConnections();

        double coreX = startX + horizontalSpacing;
        double coreY = baseY;
        offsetX = (960.0) - coreX;
        offsetY = (540.0) - coreY;
        treePane.setTranslateX(offsetX);
        treePane.setTranslateY(offsetY);
        connectionCanvas.setTranslateX(offsetX);
        connectionCanvas.setTranslateY(offsetY);

        StackPane root = new StackPane();
        root.getChildren().add(borderPane);
        root.getChildren().add(infoPanel);
        StackPane.setAlignment(infoPanel, Pos.CENTER_RIGHT);

        stage.getScene().setFill(javafx.scene.paint.Color.BLACK);
        stage.getScene().setRoot(root);
        stage.setFullScreen(true);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: transparent;");

        Label titleLabel = new Label("Research");
        titleLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 28));
        titleLabel.setTextFill(textColor);

        Label starsLabel = new Label("★ " + currentSave.getTotalStars());
        starsLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 22));
        starsLabel.setTextFill(Color.rgb(255, 215, 0));

        topBar.getChildren().addAll(titleLabel, starsLabel);
        return topBar;
    }

    private VBox createInfoPanel() {
        VBox panel = new VBox(18);
        panel.setPrefWidth(640);
        panel.setMaxWidth(640);
        panel.setPadding(new Insets(25));
        panel.setStyle("-fx-background-color: rgb(40, 42, 46); -fx-border-color: rgb(60, 62, 66); -fx-border-width: 0 0 0 2;");
        panel.setTranslateX(700);

        infoTitle = new Label("");
        infoTitle.setFont(Font.font("Roboto", FontWeight.BOLD, 28));
        infoTitle.setTextFill(textColor);
        infoTitle.setWrapText(true);

        infoDesc = new Label("");
        infoDesc.setFont(Font.font("Roboto", 16));
        infoDesc.setTextFill(Color.rgb(180, 180, 180));
        infoDesc.setWrapText(true);

        infoLevel = new Label("");
        infoLevel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        infoLevel.setTextFill(Color.rgb(100, 200, 255));

        infoEffect = new Label("");
        infoEffect.setFont(Font.font("Roboto", FontWeight.BOLD, 17));
        infoEffect.setTextFill(Color.rgb(150, 255, 150));
        infoEffect.setWrapText(true);

        upgradeButton = new Button("UPGRADE");
        upgradeButton.setFont(Font.font("Roboto", FontWeight.BOLD, 18));
        upgradeButton.setPrefWidth(590);
        upgradeButton.setPrefHeight(55);
        upgradeButton.setStyle("-fx-background-color: rgb(60, 150, 60); -fx-text-fill: white; -fx-background-radius: 8;");
        upgradeButton.setOnAction(e -> upgradeSelectedNode());

        panel.getChildren().addAll(infoTitle, infoDesc, infoLevel, infoEffect, upgradeButton);
        return panel;
    }

    private void showInfoPanel() {
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(250), infoPanel);
        slide.setFromX(700);
        slide.setToX(0);
        slide.play();
    }

    private void hideInfoPanel() {
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(250), infoPanel);
        slide.setFromX(0);
        slide.setToX(700);
        slide.play();
        selectedNode = null;
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
            hideInfoPanel();
            MapScreen mapScreen = new MapScreen(stage, currentSave);
            mapScreen.show();
        });

        backBox.setOnMouseEntered(e -> {
            javafx.scene.effect.ColorAdjust lighterGrayTint = new javafx.scene.effect.ColorAdjust();
            lighterGrayTint.setSaturation(-1.0);
            lighterGrayTint.setBrightness(-0.3);
            backBgView.setEffect(lighterGrayTint);
        });

        backBox.setOnMouseExited(e -> {
            backBgView.setEffect(darkGrayTint);
        });

        bottomBar.getChildren().add(backBox);
        return bottomBar;
    }

    private void buildResearchTree() {
        double globalX = startX + 0 * horizontalSpacing + globalOffsetX;
        double coreX = startX + 1 * horizontalSpacing;
        double basicX = startX + 2 * horizontalSpacing;
        double sniperX = startX + 3 * horizontalSpacing;
        double cannonX = startX + 4 * horizontalSpacing;
        double freezeX = startX + 5 * horizontalSpacing;

        ResearchNode globalRoot = createNode("global_root", "Global", "All towers", globalX, baseY + globalOffsetY, null);
        globalRoot.button.setDisable(true); globalRoot.button.setOpacity(0.5);

        ResearchNode coreNode = createNode("core", "Core", "Central hub", coreX, baseY, globalRoot);
        coreNode.setCore();

        ResearchNode basicRoot = createNode("basic_root", "Basic", "Core tower", basicX, baseY, coreNode);
        basicRoot.button.setDisable(true); basicRoot.button.setOpacity(0.5);

        ResearchNode sniperRoot = createNode("sniper_root", "Sniper", "Long range", sniperX, baseY, basicRoot);
        sniperRoot.button.setDisable(true); sniperRoot.button.setOpacity(0.5);

        ResearchNode cannonRoot = createNode("cannon_root", "Cannon", "Area damage", cannonX, baseY, sniperRoot);
        cannonRoot.button.setDisable(true); cannonRoot.button.setOpacity(0.5);

        ResearchNode freezeRoot = createNode("freeze_root", "Freeze", "Slow enemies", freezeX, baseY, cannonRoot);
        freezeRoot.button.setDisable(true); freezeRoot.button.setOpacity(0.5);

        buildGlobalCluster(globalRoot);
        buildBasicCluster(basicRoot);
        buildSniperCluster(sniperRoot);
        buildCannonCluster(cannonRoot);
        buildFreezeCluster(freezeRoot);
    }

    private void buildGlobalCluster(ResearchNode parent) {
        double cx = parent.x;
        double cy = parent.y;
        double[] angles = { Math.toRadians(135), Math.toRadians(180), Math.toRadians(225) };

        createNode("global_damage", "Global Dmg", "Increases damage of ALL towers.",
                cx + Math.cos(angles[0]) * globalRadius, cy + Math.sin(angles[0]) * globalRadius, parent);

        createNode("global_range", "Global Range", "Increases range of ALL towers.",
                cx + Math.cos(angles[1]) * globalRadius, cy + Math.sin(angles[1]) * globalRadius, parent);

        createNode("global_xp", "Global XP", "Increases XP gain for towers.",
                cx + Math.cos(angles[2]) * globalRadius, cy + Math.sin(angles[2]) * globalRadius, parent);
    }

    private void buildBasicCluster(ResearchNode parent) {
        double dir = -1;
        double rootX = parent.x;
        double l1_Y = parent.y + (dir * clusterVerticalOffset);
        int count = 6;
        double startX = rootX - ((count - 1) * horizontalSpread) / 2.0;

        ResearchNode nDmg = createNode("basic_damage", "Damage", "Base damage.", startX + 0*horizontalSpread, l1_Y, parent);
        createNode("basic_dmg_mult", "Dmg Mult", "Multiply damage output.", nDmg.x, l1_Y + dir*verticalSpacing, nDmg);

        ResearchNode nSpd = createNode("basic_attack_speed", "Speed", "Attack speed.", startX + 1*horizontalSpread, l1_Y, parent);
        createNode("basic_rotation", "Rotation", "Turn speed.", nSpd.x, l1_Y + dir*verticalSpacing, nSpd);

        ResearchNode nRng = createNode("basic_range", "Range", "Firing range.", startX + 2*horizontalSpread, l1_Y, parent);
        createNode("basic_proj_speed", "Proj Speed", "Arrow flight speed.", nRng.x, l1_Y + dir*verticalSpacing, nRng);

        ResearchNode nXp = createNode("basic_xp_mult", "XP Factor", "Level up faster.", startX + 3*horizontalSpread, l1_Y, parent);
        ResearchNode nMaxXp = createNode("basic_max_xp", "Max XP", "XP cap increase.", nXp.x, l1_Y + dir*verticalSpacing, nXp);
        createNode("basic_max_mk", "Max MK", "Max level cap.", nMaxXp.x, l1_Y + dir*verticalSpacing*2, nMaxXp);

        ResearchNode nCost = createNode("basic_price", "Cost", "Build cost reduction.", startX + 4*horizontalSpread, l1_Y, parent);
        createNode("basic_upgrade_price", "Upg Cost", "Upgrade cost reduction.", nCost.x, l1_Y + dir*verticalSpacing, nCost);
    }

    private void buildSniperCluster(ResearchNode parent) {
        double dir = 1;
        double rootX = parent.x;
        double l1_Y = parent.y + (dir * clusterVerticalOffset);
        int count = 5;
        double startX = rootX - ((count - 1) * horizontalSpread) / 2.0;

        ResearchNode nDmg = createNode("sniper_damage", "Damage", "High caliber rounds.", startX + 0*horizontalSpread, l1_Y, parent);
        createNode("sniper_aim_speed", "Aim Speed", "Lock-on time.", nDmg.x, l1_Y + dir*verticalSpacing, nDmg);

        ResearchNode nSpd = createNode("sniper_attack_speed", "Speed", "Fire rate.", startX + 1*horizontalSpread, l1_Y, parent);
        createNode("sniper_rotation", "Rotation", "Turn speed.", nSpd.x, l1_Y + dir*verticalSpacing, nSpd);

        ResearchNode nCrit = createNode("sniper_crit_chance", "Crit %", "Critical hit chance.", startX + 2*horizontalSpread, l1_Y, parent);
        createNode("sniper_crit_mult", "Crit Dmg", "Critical damage.", nCrit.x, l1_Y + dir*verticalSpacing, nCrit);

        ResearchNode nRng = createNode("sniper_range", "Range", "Distance.", startX + 3*horizontalSpacing, l1_Y, parent);
        ResearchNode nCost = createNode("sniper_price", "Cost", "Build cost.", nRng.x, l1_Y + dir*verticalSpacing, nRng);
        createNode("sniper_upgrade_price", "Upg Cost", "Upgrade cost.", nCost.x, l1_Y + dir*verticalSpacing*2, nCost);

        ResearchNode nXp = createNode("sniper_xp_mult", "XP Factor", "XP gain.", startX + 4*horizontalSpread, l1_Y, parent);
        ResearchNode nMaxXp = createNode("sniper_max_xp", "Max XP", "XP cap.", nXp.x, l1_Y + dir*verticalSpacing, nXp);
        createNode("sniper_max_mk", "Max MK", "Level cap.", nMaxXp.x, l1_Y + dir*verticalSpacing*2, nMaxXp);
    }

    private void buildCannonCluster(ResearchNode parent) {
        double dir = -1;
        double rootX = parent.x;
        double l1_Y = parent.y + (dir * clusterVerticalOffset);
        int count = 5;
        double startX = rootX - ((count - 1) * horizontalSpread) / 2.0;

        ResearchNode nDmg = createNode("cannon_damage", "Damage", "Shell damage.", startX + 0*horizontalSpread, l1_Y, parent);
        createNode("cannon_explosion", "Blast", "Explosion radius.", nDmg.x, l1_Y + dir*verticalSpacing, nDmg);

        ResearchNode nRng = createNode("cannon_range", "Range", "Distance.", startX + 1*horizontalSpread, l1_Y, parent);
        createNode("cannon_proj_speed", "Proj Spd", "Travel speed.", nRng.x, l1_Y + dir*verticalSpacing, nRng);

        ResearchNode nSpd = createNode("cannon_attack_speed", "Speed", "Fire rate.", startX + 2*horizontalSpread, l1_Y, parent);
        createNode("cannon_rotation", "Rotation", "Turn speed.", nSpd.x, l1_Y + dir*verticalSpacing, nSpd);

        ResearchNode nCost = createNode("cannon_price", "Cost", "Build cost.", startX + 3*horizontalSpacing, l1_Y, parent);
        createNode("cannon_upgrade_price", "Upg Cost", "Upgrade cost.", nCost.x, l1_Y + dir*verticalSpacing, nCost);

        ResearchNode nXp = createNode("cannon_xp_mult", "XP Factor", "XP gain.", startX + 4*horizontalSpread, l1_Y, parent);
        ResearchNode nMaxXp = createNode("cannon_max_xp", "Max XP", "XP cap.", nXp.x, l1_Y + dir*verticalSpacing, nXp);
        createNode("cannon_max_mk", "Max MK", "Level cap.", nMaxXp.x, l1_Y + dir*verticalSpacing*2, nMaxXp);
    }

    private void buildFreezeCluster(ResearchNode parent) {
        double dir = 1;
        double rootX = parent.x;
        double l1_Y = parent.y + (dir * clusterVerticalOffset);
        int count = 4;
        double startX = rootX - ((count - 1) * horizontalSpread) / 2.0;

        ResearchNode nSlow = createNode("freeze_percent", "Slow %", "Slow intensity.", startX + 0*horizontalSpread, l1_Y, parent);

        ResearchNode nSpd = createNode("freeze_speed", "Fire Rate", "Apply rate.", startX + 1*horizontalSpread, l1_Y, parent);
        createNode("freeze_range", "Range", "Area size.", nSpd.x, l1_Y + dir*verticalSpacing, nSpd);

        ResearchNode nCost = createNode("freeze_price", "Cost", "Build cost.", startX + 2*horizontalSpread, l1_Y, parent);
        createNode("freeze_upgrade_price", "Upg Cost", "Upgrade cost.", nCost.x, l1_Y + dir*verticalSpacing, nCost);

        ResearchNode nXp = createNode("freeze_xp_mult", "XP Factor", "XP gain.", startX + 3*horizontalSpread, l1_Y, parent);
        ResearchNode nMaxXp = createNode("freeze_max_xp", "Max XP", "XP cap.", nXp.x, l1_Y + dir*verticalSpacing, nXp);
        createNode("freeze_max_mk", "Max MK", "Level cap.", nMaxXp.x, l1_Y + dir*verticalSpacing*2, nMaxXp);
    }

    private ResearchNode createNode(String id, String name, String desc, double x, double y, ResearchNode parent) {
        ResearchNode node = new ResearchNode(id, name, desc, x, y);
        if (parent != null) connections.add(new NodeConnection(parent, node));
        allNodes.put(id, node);
        treePane.getChildren().add(node.button);
        return node;
    }

    private void drawConnections() {
        GraphicsContext gc = connectionCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(lineColor);
        gc.setLineWidth(3);
        for (NodeConnection conn : connections) {
            gc.strokeLine(conn.from.x, conn.from.y, conn.to.x, conn.to.y);
        }
    }

    private void showNodeInfo(ResearchNode node) {
        selectedNode = node;
        showInfoPanel();
        infoTitle.setText(node.name);
        infoDesc.setText(node.description);

        int currentLevel = researchManager.getLevel(node.id);
        int maxLevel = researchManager.getMaxLevel(node.id);

        infoLevel.setText("Level: " + currentLevel + " / " + maxLevel);

        double currentEffect = researchManager.getPercentBonus(node.id) * 100;

        if (node.id.contains("max_mk")) {
            infoEffect.setText("Current: " + (int)researchManager.getAbsoluteValue(node.id) + " MK");
        } else if (node.id.contains("price")) {
            infoEffect.setText("Current Cost: " + (int)researchManager.getAbsoluteValue(node.id));
        } else {
            infoEffect.setText("Current Bonus: +" + String.format("%.1f", currentEffect) + "%");
        }

        if (currentLevel >= maxLevel) {
            upgradeButton.setText("MAX LEVEL");
            upgradeButton.setDisable(true);
            upgradeButton.setStyle("-fx-background-color: rgb(100, 100, 100); -fx-text-fill: white; -fx-background-radius: 8;");
        } else {
            upgradeButton.setText("UPGRADE (FREE)");
            upgradeButton.setDisable(false);
            upgradeButton.setStyle("-fx-background-color: rgb(60, 150, 60); -fx-text-fill: white; -fx-background-radius: 8;");
        }
    }

    private void upgradeSelectedNode() {
        if (selectedNode == null) return;
        boolean success = researchManager.upgrade(selectedNode.id);
        if (success) {
            selectedNode.updateLevel();
            showNodeInfo(selectedNode);
        }
    }

    private class ResearchNode {
        String id;
        String name;
        String description;
        double x, y;
        Button button;
        Label levelLabel;
        boolean isCore = false;

        ResearchNode(String id, String name, String description, double x, double y) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.x = x;
            this.y = y;

            button = new Button();
            button.setPrefSize(nodeSize, nodeSize);
            button.setLayoutX(x - nodeSize / 2);
            button.setLayoutY(y - nodeSize / 2);

            Image bgImage = AtlasManager.getInstance().getAtlas().getRegion("global-upgrades-icon-background");
            if (bgImage != null) {
                ImageView bgView = new ImageView(bgImage);
                bgView.setFitWidth(nodeSize);
                bgView.setFitHeight(nodeSize);
                bgView.setPreserveRatio(true);
                bgView.setMouseTransparent(true);
                button.setGraphic(bgView);
            }

            button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            button.setOnAction(e -> showNodeInfo(this));

            levelLabel = new Label("0");
            levelLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 15));
            levelLabel.setTextFill(Color.WHITE);
            levelLabel.setLayoutX(x + 22);
            levelLabel.setLayoutY(y - 38);
            levelLabel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 3 7 3 7; -fx-background-radius: 5;");
            levelLabel.setMouseTransparent(true);

            if (!id.endsWith("_root")) {
                treePane.getChildren().add(levelLabel);
            }

            updateLevel();
        }

        void setCore() {
            isCore = true;
            button.setStyle("-fx-background-color: rgba(80, 150, 255, 0.4); -fx-border-color: rgb(120, 180, 255); -fx-border-width: 4; -fx-border-radius: 10; -fx-background-radius: 10;");
        }

        void updateLevel() {
            if (id.endsWith("_root")) return;
            int level = researchManager.getLevel(id);
            levelLabel.setText(String.valueOf(level));
            if (level > 0 && !isCore) {
                button.setStyle("-fx-background-color: rgba(60, 150, 60, 0.25); -fx-border-color: rgb(120, 200, 120); -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
            } else if (!isCore) {
                button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            }
        }
    }

    private class NodeConnection {
        ResearchNode from;
        ResearchNode to;
        NodeConnection(ResearchNode from, ResearchNode to) { this.from = from; this.to = to; }
    }
}
