package com.eliemichel.polyfinite.game;

import com.eliemichel.polyfinite.database.DBConnectMySQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Singleton class that manages all research upgrades globally.
 * Towers can access this to get their research bonuses.
 *
 * BACKWARDS COMPATIBLE with Tower.java code that uses:
 * - getStatMultiplier("GLOBAL_DAMAGE")
 * - getStatMultiplier("BASIC_DAMAGE")
 * etc.
 */
public class ResearchManager {
    private static ResearchManager instance;
    private HashMap<String, Integer> researchLevels;
    private HashMap<String, ResearchData> researchData;
    private int currentSaveSlotId = 0;

    private ResearchManager() {
        researchLevels = new HashMap<>();
        researchData = new HashMap<>();
        initAllResearchData();
    }

    public static ResearchManager getInstance() {
        if (instance == null) {
            instance = new ResearchManager();
        }
        return instance;
    }

    private void initAllResearchData() {
        // ========== BASIC TOWER ==========
        addResearch("basic_damage", new double[]{0,2,4,6,8,11,14,17,21,25,30});
        addResearch("basic_range", new double[]{0,1,2,3,4,5,6,7,9,11,13});
        addResearch("basic_attack_speed", new double[]{0,2,4,6,8,11,14,17,21,25,30});
        addResearch("basic_proj_speed", new double[]{0,2,4,6,9,12,15,18,21,25,30});
        addResearch("basic_rotation", new double[]{0,2,4,6,8,11,14,17,21,25,30});
        addResearch("basic_dmg_mult", new double[]{0,2,4,6,8,11,14,17,21,25,30});
        addResearch("basic_price", new double[]{48,47,46,45,44,43,42,41,40,39,37});
        addResearch("basic_upgrade_price", new double[]{0,-1,-2,-3,-4,-5,-6,-8,-10,-12,-15});
        addResearch("basic_xp_mult", new double[]{0,3,6,9,12,16,20,24,28,33,40});
        addResearch("basic_max_mk", new double[]{3,4,5,6,7,8,9,10});
        addResearch("basic_max_xp", new double[]{4,5,6,7,9,10,12,14,16,18,20});

        // ========== SNIPER TOWER ==========
        addResearch("sniper_damage", new double[]{0,2,4,6,9,12,15,18,21,25,30});
        addResearch("sniper_range", new double[]{0,1,2,3,4,5,6,8,10,12,15});
        addResearch("sniper_attack_speed", new double[]{0,2,4,6,8,10,12,14,17,20,25});
        addResearch("sniper_rotation", new double[]{0,2,4,6,8,10,12,14,17,20,25});
        addResearch("sniper_aim_speed", new double[]{0,1,2,3,5,7,9,11,13,16,20});
        addResearch("sniper_crit_chance", new double[]{0,1,3,5,7,9,11,13,15,17,20});
        addResearch("sniper_crit_mult", new double[]{0,2,4,6,9,12,15,18,21,25,30});
        addResearch("sniper_price", new double[]{80,79,78,76,75,74,72,70,68,66,62});
        addResearch("sniper_upgrade_price", new double[]{0,-1,-2,-3,-4,-5,-6,-8,-10,-12,-15});
        addResearch("sniper_xp_mult", new double[]{0,3,6,9,12,16,20,24,28,33,40});
        addResearch("sniper_max_mk", new double[]{3,4,5,6,7,8,9,10});
        addResearch("sniper_max_xp", new double[]{4,5,6,7,9,10,12,14,16,18,20});

        // ========== CANNON TOWER ==========
        addResearch("cannon_damage", new double[]{0,2,4,6,8,11,14,17,21,25,30});
        addResearch("cannon_range", new double[]{0,1,2,3,4,5,6,7,9,11,13});
        addResearch("cannon_attack_speed", new double[]{0,2,4,6,9,12,15,18,21,25,30});
        addResearch("cannon_proj_speed", new double[]{0,2,4,6,9,12,15,18,21,25,30});
        addResearch("cannon_rotation", new double[]{0,2,4,6,8,11,14,17,21,25,30});
        addResearch("cannon_explosion", new double[]{0,1,3,5,7,9,11,13,15,17,20});
        addResearch("cannon_price", new double[]{60,59,58,57,56,55,54,53,51,50,46});
        addResearch("cannon_upgrade_price", new double[]{0,-1,-2,-3,-4,-5,-6,-8,-10,-12,-15});
        addResearch("cannon_xp_mult", new double[]{0,3,6,9,12,15,19,23,27,32,40});
        addResearch("cannon_max_mk", new double[]{3,4,5,6,7,8,9,10});
        addResearch("cannon_max_xp", new double[]{4,5,6,7,9,10,12,14,16,18,20});

        // ========== FREEZING TOWER ==========
        addResearch("freeze_percent", new double[]{0,1,2,3,4,5,6,8,10,12,15});
        addResearch("freeze_speed", new double[]{0,2,4,6,9,12,15,18,21,25,30});
        addResearch("freeze_range", new double[]{0,1,2,3,4,5,6,7,9,11,13});
        addResearch("freeze_price", new double[]{80,79,77,76,74,73,71,70,68,66,62});
        addResearch("freeze_upgrade_price", new double[]{0,-1,-2,-3,-4,-5,-6,-8,-10,-12,-15});
        addResearch("freeze_xp_mult", new double[]{0,3,6,9,12,16,20,24,28,33,40});
        addResearch("freeze_max_mk", new double[]{3,4,5,6,7,8,9,10});
        addResearch("freeze_max_xp", new double[]{4,5,6,7,9,10,12,14,16,18,20});

        // ========== GLOBAL UPGRADES ==========
        addResearch("global_damage", new double[]{0,5,10,15,20});
        addResearch("global_range", new double[]{0,5,10,15,20});
        addResearch("global_xp", new double[]{0,10,20,30,40});

        // Initialize all levels to 0
        for (String id : researchData.keySet()) {
            researchLevels.put(id, 0);
        }
    }

    private void addResearch(String id, double[] values) {
        researchData.put(id, new ResearchData(id, values));
    }

    // ========== DATABASE LOADING/SAVING ==========

    public void loadResearch(int saveSlotId) {
        this.currentSaveSlotId = saveSlotId;

        // Reset to defaults first
        for (String id : researchData.keySet()) {
            researchLevels.put(id, 0);
        }

        DBConnectMySQL db = new DBConnectMySQL();
        if (db.isConnected()) {
            try {
                String query = "SELECT research_id, current_level FROM player_research WHERE save_slot_id = ?";
                PreparedStatement stmt = db.getConnection().prepareStatement(query);
                stmt.setInt(1, saveSlotId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String id = rs.getString("research_id");
                    int level = rs.getInt("current_level");
                    if (researchData.containsKey(id)) {
                        researchLevels.put(id, level);
                    }
                }
                System.out.println("Research loaded for slot " + saveSlotId);
            } catch (Exception e) {
                System.out.println("Error loading research: " + e.getMessage());
            } finally {
                db.closeConnection();
            }
        }
    }

    public void saveResearch(String researchId, int level) {
        if (currentSaveSlotId == 0) return;

        DBConnectMySQL db = new DBConnectMySQL();
        if (db.isConnected()) {
            try {
                String query = "INSERT INTO player_research (save_slot_id, research_id, current_level) " +
                        "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE current_level = ?";
                PreparedStatement stmt = db.getConnection().prepareStatement(query);
                stmt.setInt(1, currentSaveSlotId);
                stmt.setString(2, researchId);
                stmt.setInt(3, level);
                stmt.setInt(4, level);
                stmt.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error saving research: " + e.getMessage());
            } finally {
                db.closeConnection();
            }
        }
    }

    // ========== BACKWARDS COMPATIBLE METHOD ==========

    /**
     * BACKWARDS COMPATIBLE - Used by Tower.java
     * Maps old effect types to new research IDs
     */
    public double getStatMultiplier(String effectType) {
        String researchId = mapEffectTypeToResearchId(effectType);

        if (researchId != null) {
            return 1.0 + getPercentBonus(researchId);
        }

        return 1.0;
    }

    private String mapEffectTypeToResearchId(String effectType) {
        // Global effects
        if (effectType.equals("GLOBAL_DAMAGE")) return "global_damage";
        if (effectType.equals("GLOBAL_RANGE")) return "global_range";
        if (effectType.equals("GLOBAL_XP")) return "global_xp";

        // Basic tower effects
        if (effectType.equals("BASIC_DAMAGE")) return "basic_damage";
        if (effectType.equals("BASIC_RANGE")) return "basic_range";
        if (effectType.equals("BASIC_SPEED")) return "basic_attack_speed";

        // Sniper tower effects
        if (effectType.equals("SNIPER_DAMAGE")) return "sniper_damage";
        if (effectType.equals("SNIPER_RANGE")) return "sniper_range";
        if (effectType.equals("SNIPER_SPEED")) return "sniper_attack_speed";
        if (effectType.equals("SNIPER_CRIT")) return "sniper_crit_chance";

        // Cannon tower effects
        if (effectType.equals("CANNON_DAMAGE")) return "cannon_damage";
        if (effectType.equals("CANNON_RANGE")) return "cannon_range";
        if (effectType.equals("CANNON_SPEED")) return "cannon_attack_speed";

        // Freeze tower effects
        if (effectType.equals("FREEZE_RANGE")) return "freeze_range";
        if (effectType.equals("FREEZING_RANGE")) return "freeze_range";

        return null;
    }

    // ========== CORE PUBLIC METHODS ==========

    public int getLevel(String researchId) {
        return researchLevels.getOrDefault(researchId, 0);
    }

    public void setLevel(String researchId, int level) {
        ResearchData data = researchData.get(researchId);
        if (data != null) {
            int maxLevel = data.values.length - 1;
            int newLevel = Math.min(level, maxLevel);
            researchLevels.put(researchId, newLevel);
            saveResearch(researchId, newLevel);
        }
    }

    public boolean upgrade(String researchId) {
        int current = getLevel(researchId);
        ResearchData data = researchData.get(researchId);
        if (data != null && current < data.values.length - 1) {
            int newLevel = current + 1;
            researchLevels.put(researchId, newLevel);
            saveResearch(researchId, newLevel);
            return true;
        }
        return false;
    }

    public int getMaxLevel(String researchId) {
        ResearchData data = researchData.get(researchId);
        if (data != null) {
            return data.values.length - 1;
        }
        return 0;
    }

    public double getPercentBonus(String researchId) {
        int level = getLevel(researchId);
        ResearchData data = researchData.get(researchId);
        if (data != null && level < data.values.length) {
            return data.values[level] / 100.0;
        }
        return 0;
    }

    public double getAbsoluteValue(String researchId) {
        int level = getLevel(researchId);
        ResearchData data = researchData.get(researchId);
        if (data != null && level < data.values.length) {
            return data.values[level];
        }
        return 0;
    }

    public double getValueAtLevel(String researchId, int level) {
        ResearchData data = researchData.get(researchId);
        if (data != null && level >= 0 && level < data.values.length) {
            return data.values[level];
        }
        return 0;
    }

    public double getMultiplier(String researchId) {
        return 1.0 + getPercentBonus(researchId);
    }

    // ========== TOWER HELPER METHODS ==========

    // Basic Tower
    public double getBasicDamageMultiplier() { return getMultiplier("basic_damage"); }
    public double getBasicRangeMultiplier() { return getMultiplier("basic_range"); }
    public double getBasicAttackSpeedMultiplier() { return getMultiplier("basic_attack_speed"); }
    public int getBasicMaxMK() { return (int) getAbsoluteValue("basic_max_mk"); }
    public int getBasicMaxXP() { return (int) getAbsoluteValue("basic_max_xp"); }

    // Sniper Tower
    public double getSniperDamageMultiplier() { return getMultiplier("sniper_damage"); }
    public double getSniperRangeMultiplier() { return getMultiplier("sniper_range"); }
    public double getSniperCritChanceBonus() { return getPercentBonus("sniper_crit_chance"); }
    public double getSniperCritMultiplierBonus() { return getMultiplier("sniper_crit_mult"); }
    public int getSniperMaxMK() { return (int) getAbsoluteValue("sniper_max_mk"); }

    // Cannon Tower
    public double getCannonDamageMultiplier() { return getMultiplier("cannon_damage"); }
    public double getCannonExplosionMultiplier() { return getMultiplier("cannon_explosion"); }
    public int getCannonMaxMK() { return (int) getAbsoluteValue("cannon_max_mk"); }

    // Freezing Tower
    public double getFreezePercentBonus() { return getPercentBonus("freeze_percent"); }
    public double getFreezeSpeedMultiplier() { return getMultiplier("freeze_speed"); }
    public int getFreezeMaxMK() { return (int) getAbsoluteValue("freeze_max_mk"); }

    public void resetAll() {
        for (String id : researchData.keySet()) {
            researchLevels.put(id, 0);
        }
    }
}

class ResearchData {
    String id;
    double[] values;

    ResearchData(String id, double[] values) {
        this.id = id;
        this.values = values;
    }
}