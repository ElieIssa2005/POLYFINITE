package com.eliemichel.polyfinite.game;

import com.eliemichel.polyfinite.database.DBConnectMySQL;
import java.sql.ResultSet;

public class SaveSlot {

    private int slotNumber;
    private boolean isEmpty;
    private int totalStars;
    private int currentLevel;
    private int gold;
    private String playerName;

    public SaveSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.isEmpty = true;
        this.totalStars = 0;
        this.currentLevel = 1;
        this.playerName = "Player " + slotNumber;
        this.gold = 20;
    }

    public void loadFromDatabase() {
        DBConnectMySQL connector = new DBConnectMySQL();

        if (!connector.isConnected()) {
            System.out.println("Cannot load save slot - database not connected");
            return;
        }

        try {
            String sql = "SELECT * FROM save_slots WHERE slot_number = " + slotNumber;
            ResultSet rs = connector.getStatement().executeQuery(sql);

            if (rs.next()) {
                this.playerName = rs.getString("player_name");
                this.totalStars = rs.getInt("total_stars");
                this.currentLevel = rs.getInt("current_level");
                this.gold = rs.getInt("gold");

                if (this.totalStars > 0 || this.currentLevel > 1) {
                    this.isEmpty = false;
                } else {
                    this.isEmpty = true;
                }

                System.out.println("Loaded slot " + slotNumber + ": " + totalStars + " stars, level " + currentLevel);
            }

            connector.closeConnection();

        } catch (Exception e) {
            System.out.println("Error loading save slot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startNewGame() {
        this.isEmpty = false;
        this.totalStars = 0;
        this.currentLevel = 1;
        this.gold = 20;

        DBConnectMySQL connector = new DBConnectMySQL();

        if (!connector.isConnected()) {
            return;
        }

        try {
            String sql = "UPDATE save_slots SET total_stars = 0, current_level = 1, gold = 500 WHERE slot_number = " + slotNumber;
            connector.getStatement().executeUpdate(sql);
            connector.closeConnection();
            System.out.println("Started new game in slot " + slotNumber);
        } catch (Exception e) {
            System.out.println("Error starting new game: " + e.getMessage());
        }
    }

    public void saveLevelProgress(int levelNumber, int wave, int score, boolean q1, boolean q2, boolean q3) {
        DBConnectMySQL connector = new DBConnectMySQL();

        if (!connector.isConnected()) {
            System.out.println("Cannot save progress - database not connected");
            return;
        }

        try {
            int starsEarned = (q1 ? 1 : 0) + (q2 ? 1 : 0) + (q3 ? 1 : 0);

            String checkSql = "SELECT * FROM level_progress WHERE save_slot_id = " + slotNumber + " AND level_number = " + levelNumber;
            ResultSet rs = connector.getStatement().executeQuery(checkSql);

            if (rs.next()) {
                int oldBestWave = rs.getInt("best_wave");
                int oldBestScore = rs.getInt("best_score");
                boolean oldQ1 = rs.getBoolean("quest_1_completed");
                boolean oldQ2 = rs.getBoolean("quest_2_completed");
                boolean oldQ3 = rs.getBoolean("quest_3_completed");
                int timesPlayed = rs.getInt("times_played");

                int newBestWave = Math.max(oldBestWave, wave);
                int newBestScore = Math.max(oldBestScore, score);
                boolean newQ1 = oldQ1 || q1;
                boolean newQ2 = oldQ2 || q2;
                boolean newQ3 = oldQ3 || q3;
                int newStars = (newQ1 ? 1 : 0) + (newQ2 ? 1 : 0) + (newQ3 ? 1 : 0);

                String updateSql = "UPDATE level_progress SET " +
                        "best_wave = " + newBestWave + ", " +
                        "best_score = " + newBestScore + ", " +
                        "quest_1_completed = " + newQ1 + ", " +
                        "quest_2_completed = " + newQ2 + ", " +
                        "quest_3_completed = " + newQ3 + ", " +
                        "stars_earned = " + newStars + ", " +
                        "times_played = " + (timesPlayed + 1) + " " +
                        "WHERE save_slot_id = " + slotNumber + " AND level_number = " + levelNumber;

                connector.getStatement().executeUpdate(updateSql);
                System.out.println("Updated level progress: " + newStars + " stars");

            } else {
                String insertSql = "INSERT INTO level_progress " +
                        "(save_slot_id, level_number, best_wave, best_score, quest_1_completed, quest_2_completed, quest_3_completed, stars_earned, times_played) " +
                        "VALUES (" + slotNumber + ", " + levelNumber + ", " + wave + ", " + score + ", " + q1 + ", " + q2 + ", " + q3 + ", " + starsEarned + ", 1)";

                connector.getStatement().executeUpdate(insertSql);
                System.out.println("Created new level progress: " + starsEarned + " stars");
            }

            String totalStarsSql = "SELECT SUM(stars_earned) as total FROM level_progress WHERE save_slot_id = " + slotNumber;
            ResultSet totalRs = connector.getStatement().executeQuery(totalStarsSql);

            if (totalRs.next()) {
                int newTotalStars = totalRs.getInt("total");

                String updateSlotSql = "UPDATE save_slots SET total_stars = " + newTotalStars + " WHERE slot_number = " + slotNumber;
                connector.getStatement().executeUpdate(updateSlotSql);

                this.totalStars = newTotalStars;
                this.isEmpty = false;

                System.out.println("Total stars: " + newTotalStars);
            }

            connector.closeConnection();

        } catch (Exception e) {
            System.out.println("Error saving level progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(int totalStars) {
        this.totalStars = totalStars;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGold() { return gold; }
}