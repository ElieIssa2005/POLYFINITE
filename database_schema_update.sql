-- =====================================================
-- POLYFINITE DATABASE SCHEMA UPDATE
-- Quest System and Currency Tables
-- =====================================================

-- Quest Progress Table
-- Stores progress for each quest per save slot and level
CREATE TABLE IF NOT EXISTS quest_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    save_slot_id INT NOT NULL,
    level_number INT NOT NULL,
    quest_id VARCHAR(100) NOT NULL,
    current_progress INT DEFAULT 0,
    completed BOOLEAN DEFAULT FALSE,
    reward_granted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_quest (save_slot_id, level_number, quest_id)
);

-- Player Currencies Table
-- Stores meta-currencies (Gold, Scalar, Vector, Matrix, Tensor, Infiar)
CREATE TABLE IF NOT EXISTS player_currencies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    save_slot_id INT NOT NULL,
    currency_type VARCHAR(50) NOT NULL,
    amount INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_currency (save_slot_id, currency_type)
);

-- Insert default currencies for existing save slots (optional)
-- This will create zero-balance entries for all currency types
-- INSERT IGNORE INTO player_currencies (save_slot_id, currency_type, amount)
-- SELECT slot_number, 'GOLD', 0 FROM save_slots;
-- (Repeat for SCALAR, VECTOR, MATRIX, TENSOR, INFIAR)
