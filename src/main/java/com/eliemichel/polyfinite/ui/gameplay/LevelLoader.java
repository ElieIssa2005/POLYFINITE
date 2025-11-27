package com.eliemichel.polyfinite.ui.gameplay;

import com.eliemichel.polyfinite.game.EnemyWeight;
import com.eliemichel.polyfinite.game.LevelData;
import com.eliemichel.polyfinite.game.QuestDefinition;
import com.eliemichel.polyfinite.game.SpawnDensity;
import com.eliemichel.polyfinite.game.WaveData;
import com.eliemichel.polyfinite.game.tiles.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class LevelLoader {

    private LevelData levelData;
    private int[] spawnTile;
    private int[] goalTile;
    private ArrayList<int[]> enemyPath;
    private ArrayList<WaveData> waves;

    public LevelLoader() {
        this.waves = new ArrayList<>();
    }

    public void loadLevel(int levelNumber) {
        String filename = "level_" + levelNumber + ".txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            int gridWidth = 25;
            int gridHeight = 15;
            String levelName = "Level " + levelNumber;

            // Wave settings with defaults
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

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("LEVEL_NAME:")) {
                    levelName = line.substring(11).trim();
                } else if (line.startsWith("GRID_WIDTH:")) {
                    gridWidth = Integer.parseInt(line.substring(11).trim());
                } else if (line.startsWith("GRID_HEIGHT:")) {
                    gridHeight = Integer.parseInt(line.substring(12).trim());
                } else if (line.startsWith("SPAWN_DENSITY:")) {
                    String densityStr = line.substring(14).trim();
                    density = SpawnDensity.fromString(densityStr);
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
                    // Format: ENEMY_WEIGHT:Regular:80
                    String[] parts = line.substring(13).split(":");
                    if (parts.length == 2) {
                        String enemyType = parts[0].trim();
                        int weight = Integer.parseInt(parts[1].trim());
                        enemyWeights.add(new EnemyWeight(enemyType, weight));
                    }
                } else if (line.startsWith("GOLD_DROP_CHANCE:")) {
                    goldDropChance = Double.parseDouble(line.substring(17).trim());
                } else if (line.startsWith("QUEST:")) {
                    QuestDefinition quest = QuestDefinition.fromFileString(line.substring(6).trim());
                    if (quest != null) {
                        questDefinitions.add(quest);
                    }
                } else if (line.equals("GRID:")) {
                    break;
                }
            }

            levelData = new LevelData(gridWidth, gridHeight);
            levelData.setLevelNumber(levelNumber);
            levelData.setLevelName(levelName);

            // Apply wave settings
            levelData.setSpawnDensity(density);
            levelData.setCustomSpawnInterval(customInterval);
            levelData.setDifficultyModifier(difficultyModifier);
            levelData.setBaseEnemyCount(baseEnemyCount);
            levelData.setMaxEnemyCount(maxEnemyCount);
            levelData.setInterWaveTime(interWaveTime);

            // Apply enemy weights (use defaults if none specified)
            if (!enemyWeights.isEmpty()) {
                levelData.setEnemyWeights(enemyWeights);
            }

            // Apply quest settings
            levelData.setGoldDropChance(goldDropChance);
            if (!questDefinitions.isEmpty()) {
                levelData.setQuestDefinitions(questDefinitions);
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

            setupRoadTextures();
            reader.close();
            System.out.println("Level " + levelNumber + " loaded successfully!");
            System.out.println("  Density: " + levelData.getSpawnDensity().getDisplayName());
            System.out.println("  Difficulty: " + (levelData.getDifficultyModifier() * 100) + "%");
            System.out.println("  Base enemies: " + levelData.getBaseEnemyCount());
            System.out.println("  Max enemies: " + levelData.getMaxEnemyCount());

        } catch (Exception e) {
            System.out.println("Error loading level: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Detect road connections and assign textures
    private void setupRoadTextures() {
        int rows = levelData.getGridHeight();
        int cols = levelData.getGridWidth();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Tile tile = levelData.getTile(row, col);

                boolean connectTop = isRoadAt(row - 1, col);
                boolean connectRight = isRoadAt(row, col + 1);
                boolean connectBottom = isRoadAt(row + 1, col);
                boolean connectLeft = isRoadAt(row, col - 1);

                if (tile instanceof RoadTile) {
                    RoadTile roadTile = (RoadTile) tile;
                    roadTile.setConnections(connectTop, connectRight, connectBottom, connectLeft);
                }
                else if (tile instanceof SpawnTile) {
                    SpawnTile spawnTile = (SpawnTile) tile;
                    spawnTile.setConnections(connectTop, connectRight, connectBottom, connectLeft);
                }
                else if (tile instanceof GoalTile) {
                    GoalTile goalTile = (GoalTile) tile;
                    goalTile.setConnections(connectTop, connectRight, connectBottom, connectLeft);
                }
            }
        }
    }

    private boolean isRoadAt(int row, int col) {
        int rows = levelData.getGridHeight();
        int cols = levelData.getGridWidth();

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        Tile tile = levelData.getTile(row, col);
        return (tile instanceof RoadTile) || (tile instanceof SpawnTile) || (tile instanceof GoalTile);
    }

    public void findSpawnAndGoal() {
        for (int row = 0; row < levelData.getGridHeight(); row++) {
            for (int col = 0; col < levelData.getGridWidth(); col++) {
                Tile tile = levelData.getTile(row, col);
                if (tile instanceof SpawnTile) {
                    spawnTile = new int[]{row, col};
                    System.out.println("Found spawn at: " + row + ", " + col);
                }
                if (tile instanceof GoalTile) {
                    goalTile = new int[]{row, col};
                    System.out.println("Found goal at: " + row + ", " + col);
                }
            }
        }
    }

    public void createEnemyPath() {
        enemyPath = new ArrayList<>();

        if (spawnTile == null || goalTile == null) {
            System.out.println("Cannot create path - spawn or goal not found!");
            return;
        }

        ArrayList<int[]> path = findPathBFS(spawnTile, goalTile);

        if (path != null && !path.isEmpty()) {
            enemyPath = path;
            System.out.println("Path created with " + enemyPath.size() + " waypoints");
        } else {
            System.out.println("ERROR: No path found from spawn to goal!");
        }
    }

    private ArrayList<int[]> findPathBFS(int[] start, int[] goal) {
        int rows = levelData.getGridHeight();
        int cols = levelData.getGridWidth();

        boolean[][] visited = new boolean[rows][cols];
        int[][][] parent = new int[rows][cols][2];

        ArrayList<int[]> queue = new ArrayList<>();
        queue.add(start);
        visited[start[0]][start[1]] = true;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        boolean foundGoal = false;

        while (!queue.isEmpty()) {
            int[] current = queue.remove(0);
            int row = current[0];
            int col = current[1];

            if (row == goal[0] && col == goal[1]) {
                foundGoal = true;
                break;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && !visited[newRow][newCol]) {
                    Tile tile = levelData.getTile(newRow, newCol);

                    if (tile instanceof RoadTile || tile instanceof SpawnTile || tile instanceof GoalTile) {
                        visited[newRow][newCol] = true;
                        parent[newRow][newCol][0] = row;
                        parent[newRow][newCol][1] = col;
                        queue.add(new int[]{newRow, newCol});
                    }
                }
            }
        }

        if (!foundGoal) {
            return null;
        }

        ArrayList<int[]> path = new ArrayList<>();
        int[] current = goal;

        while (current[0] != start[0] || current[1] != start[1]) {
            path.add(0, current);
            int row = current[0];
            int col = current[1];
            current = new int[]{parent[row][col][0], parent[row][col][1]};
        }

        path.add(0, start);
        return path;
    }

    // loadWaves is no longer needed for infinite wave system
    // but keeping for backwards compatibility
    public void loadWaves(int levelNumber) {
        waves.clear();
        // Waves are now generated dynamically by WaveManager
        System.out.println("Wave system is now infinite - waves generated dynamically");
    }

    public LevelData getLevelData() {
        return levelData;
    }

    public int[] getSpawnTile() {
        return spawnTile;
    }

    public int[] getGoalTile() {
        return goalTile;
    }

    public ArrayList<int[]> getEnemyPath() {
        return enemyPath;
    }

    public ArrayList<WaveData> getWaves() {
        return waves;
    }
}
