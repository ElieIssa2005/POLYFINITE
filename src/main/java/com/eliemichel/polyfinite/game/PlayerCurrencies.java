package com.eliemichel.polyfinite.game;

import com.eliemichel.polyfinite.database.DBConnectMySQL;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Singleton class that manages all meta-currencies for the player.
 * These are used outside levels for research upgrades.
 */
public class PlayerCurrencies {

    private static PlayerCurrencies instance;

    private HashMap<RewardType, Integer> currencies;
    private int currentSaveSlotId = 0;

    private PlayerCurrencies() {
        currencies = new HashMap<>();
        // Initialize all currencies to 0
        for (RewardType type : RewardType.values()) {
            currencies.put(type, 0);
        }
    }

    public static PlayerCurrencies getInstance() {
        if (instance == null) {
            instance = new PlayerCurrencies();
        }
        return instance;
    }

    // Load currencies from database for a save slot
    public void loadCurrencies(int saveSlotId) {
        this.currentSaveSlotId = saveSlotId;

        // Reset to 0
        for (RewardType type : RewardType.values()) {
            currencies.put(type, 0);
        }

        DBConnectMySQL db = new DBConnectMySQL();

        if (!db.isConnected()) {
            System.out.println("Cannot load currencies - database not connected");
            return;
        }

        try {
            String sql = "SELECT currency_type, amount FROM player_currencies " +
                         "WHERE save_slot_id = " + saveSlotId;

            ResultSet rs = db.getStatement().executeQuery(sql);

            while (rs.next()) {
                String typeName = rs.getString("currency_type");
                int amount = rs.getInt("amount");

                try {
                    RewardType type = RewardType.valueOf(typeName);
                    currencies.put(type, amount);
                } catch (Exception e) {
                    // Unknown currency type, skip
                }
            }

            db.closeConnection();
            System.out.println("Loaded currencies for slot " + saveSlotId);

        } catch (Exception e) {
            System.out.println("Error loading currencies: " + e.getMessage());
        }
    }

    // Save a single currency to database
    private void saveCurrency(RewardType type) {
        if (currentSaveSlotId == 0) return;

        DBConnectMySQL db = new DBConnectMySQL();

        if (!db.isConnected()) {
            return;
        }

        try {
            int amount = currencies.get(type);

            // Check if record exists
            String checkSql = "SELECT id FROM player_currencies WHERE save_slot_id = " + currentSaveSlotId +
                             " AND currency_type = '" + type.name() + "'";

            ResultSet rs = db.getStatement().executeQuery(checkSql);

            if (rs.next()) {
                // Update
                String updateSql = "UPDATE player_currencies SET amount = " + amount +
                                  " WHERE save_slot_id = " + currentSaveSlotId +
                                  " AND currency_type = '" + type.name() + "'";
                db.getStatement().executeUpdate(updateSql);
            } else {
                // Insert
                String insertSql = "INSERT INTO player_currencies (save_slot_id, currency_type, amount) " +
                                  "VALUES (" + currentSaveSlotId + ", '" + type.name() + "', " + amount + ")";
                db.getStatement().executeUpdate(insertSql);
            }

            db.closeConnection();

        } catch (Exception e) {
            System.out.println("Error saving currency: " + e.getMessage());
        }
    }

    // Add currency and save
    public void addCurrency(RewardType type, int amount) {
        if (amount <= 0) return;

        int current = currencies.get(type);
        currencies.put(type, current + amount);
        saveCurrency(type);

        System.out.println("Added " + amount + " " + type.getDisplayName() + 
                          " (Total: " + currencies.get(type) + ")");
    }

    // Spend currency if player has enough
    public boolean spendCurrency(RewardType type, int amount) {
        if (amount <= 0) return true;

        int current = currencies.get(type);
        if (current < amount) {
            return false; // Not enough
        }

        currencies.put(type, current - amount);
        saveCurrency(type);
        return true;
    }

    // Check if player can afford
    public boolean canAfford(RewardType type, int amount) {
        return currencies.get(type) >= amount;
    }

    // Get current amount
    public int getCurrency(RewardType type) {
        return currencies.get(type);
    }

    // Convenience getters
    public int getGold() { return getCurrency(RewardType.GOLD); }
    public int getScalar() { return getCurrency(RewardType.SCALAR); }
    public int getVector() { return getCurrency(RewardType.VECTOR); }
    public int getMatrix() { return getCurrency(RewardType.MATRIX); }
    public int getTensor() { return getCurrency(RewardType.TENSOR); }
    public int getInfiar() { return getCurrency(RewardType.INFIAR); }

    // Reset all currencies (for new game)
    public void resetAll() {
        for (RewardType type : RewardType.values()) {
            currencies.put(type, 0);
        }
    }
}
