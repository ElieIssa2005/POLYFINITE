package com.eliemichel.polyfinite.utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LevelAssetsViewer extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #2a2a2a;");

        // --- ADDING ALL TABS ---
        tabPane.getTabs().addAll(
                createTab("Map Tiles", getMapTiles()),
                createTab("Towers", getTowerAssets()),
                createTab("Enemies", getEnemyAssets()),
                createTab("Miners/Res", getMinerAssets()),
                createTab("Projectiles/VFX", getProjectileVFXAssets()),
                createTab("Items/Loot", getLootAssets()),
                createTab("Icons", getIconAssets()),
                createTab("UI Elements", getUIAssets()),
                createTab("Misc/Fonts", getMiscAssets())
        );

        Scene scene = new Scene(tabPane, 1400, 900);
        primaryStage.setTitle("POLYFINITE - FULL ATLAS VIEWER");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createTab(String title, String[] assets) {
        Tab tab = new Tab(title + " (" + assets.length + ")");
        tab.setClosable(false);
        tab.setContent(createTileGrid(assets));
        return tab;
    }

    // ============================================================================================
    // ===================================== ASSET LISTS ==========================================
    // ============================================================================================

    private String[] getMapTiles() {
        return new String[]{
                // Roads
                "tile-type-road-oooo", "tile-type-road-ooox", "tile-type-road-ooxo", "tile-type-road-ooxx",
                "tile-type-road-oxoo", "tile-type-road-oxox", "tile-type-road-oxxo", "tile-type-road-oxxx",
                "tile-type-road-xooo", "tile-type-road-xoox", "tile-type-road-xoxo", "tile-type-road-xoxx",
                "tile-type-road-xxoo", "tile-type-road-xxox", "tile-type-road-xxxo", "tile-type-road-xxxx",
                // Platforms
                "tile-type-platform", "tile-type-platform-extra-2", "tile-type-platform-extra-3",
                "tile-type-platform-extra-4", "tile-type-platform-extra-5", "tile-type-platform-shade-0",
                "tile-type-platform-shade-1", "tile-type-platform-shade-2", "tile-type-platform-shade-3",
                "tile-type-platform-shade-4", "tile-type-platform-shade-5",
                // Spawns & Targets
                "tile-type-spawn-glow", "tile-type-spawn-inactive", "tile-type-spawn-overlay", "tile-type-spawn-portal",
                "tile-type-target-base", "tile-type-target-core-1", "tile-type-target-core-2",
                "tile-type-target-core-3", "tile-type-target-core-4", "tile-type-target-hollow",
                // Sources
                "tile-type-source-1", "tile-type-source-2", "tile-type-source-3", "tile-type-source-4",
                "tile-type-source-5", "tile-type-source-6", "tile-type-source-7", "tile-type-source-8",
                "tile-type-source-crack",
                // Cores
                "tile-type-core-bottom", "tile-type-core-left", "tile-type-core-right", "tile-type-core-top",
                // Boss Tiles
                "tile-type-boss-custom", "tile-type-boss-hard", "tile-type-boss-no",
                "tile-type-boss-one-bg", "tile-type-boss-one-fg", "tile-type-boss-rare",
                // Gates
                "gate-barrier-health-high-horizontal", "gate-barrier-health-high-vertical",
                "gate-barrier-health-low-horizontal", "gate-barrier-health-low-vertical",
                "gate-barrier-type-horizontal", "gate-barrier-type-vertical",
                "gate-teleport-horizontal", "gate-teleport-vertical",
                "gate-outline-horizontal-active", "gate-outline-horizontal-hover",
                "gate-outline-vertical-active", "gate-outline-vertical-hover",
                // Misc Tiles
                "tile-type-game-value-base", "tile-type-script", "random-tile", "random-barrier", "random-teleport",
                "quest-tile-base", "quest-tile-overlay", "tile-ice-overlay",
                "tile-type-xm-sound-track-base", "tile-type-xm-sound-track-disc",
                "tile-type-xm-sound-track-corner-0", "tile-type-xm-sound-track-corner-1",
                "tile-type-xm-sound-track-corner-2", "tile-type-xm-sound-track-corner-3"
        };
    }

    private String[] getTowerAssets() {
        return new String[]{
                "tower-aim",
                "tower-air", "tower-air-base", "tower-air-shape", "tower-air-weapon", "tower-air-weapon-heavy",
                "tower-air-extra-1", "tower-air-extra-2", "tower-air-extra-powerful-a", "tower-air-extra-powerful-b",
                "tower-air-extra-special-a", "tower-air-extra-special-b", "tower-air-extra-ultimate-1", "tower-air-extra-ultimate-2",

                "tower-basic", "tower-basic-base", "tower-basic-shape", "tower-basic-weapon", "tower-basic-weapon-double",
                "tower-basic-extra-1-a", "tower-basic-extra-1-b", "tower-basic-extra-2-a", "tower-basic-extra-2-b",
                "tower-basic-extra-powerful-a", "tower-basic-extra-powerful-b", "tower-basic-extra-special-a", "tower-basic-extra-special-b",
                "tower-basic-extra-ultimate-1", "tower-basic-extra-ultimate-2",

                "tower-blast", "tower-blast-base-new", "tower-blast-shape", "tower-blast-weapon-new", "tower-blast-weapon-heavy-new",
                "tower-blast-extra-1-new", "tower-blast-extra-2-new", "tower-blast-extra-powerful-1", "tower-blast-extra-powerful-2",
                "tower-blast-extra-special-1", "tower-blast-extra-special-2", "tower-blast-extra-ultimate-1", "tower-blast-extra-ultimate-2",

                "tower-cannon", "tower-cannon-base-new", "tower-cannon-shape", "tower-cannon-weapon-new", "tower-cannon-weapon-long-new",
                "tower-cannon-extra-1-new", "tower-cannon-extra-2-new", "tower-cannon-extra-powerful-1", "tower-cannon-extra-powerful-2",
                "tower-cannon-extra-special-new", "tower-cannon-extra-ultimate-1", "tower-cannon-extra-ultimate-2",

                "tower-crusher", "tower-crusher-base-new", "tower-crusher-shape", "tower-crusher-hook",
                "tower-crusher-weapon-top-new", "tower-crusher-weapon-bottom-new", "tower-crusher-weapon-top-thorns-new", "tower-crusher-weapon-bottom-thorns-new",
                "tower-crusher-extra-1-1", "tower-crusher-extra-1-2", "tower-crusher-extra-2-1", "tower-crusher-extra-2-2",
                "tower-crusher-extra-powerful-1", "tower-crusher-extra-powerful-2", "tower-crusher-extra-special-1", "tower-crusher-extra-special-2",
                "tower-crusher-extra-ultimate-1", "tower-crusher-extra-ultimate-2",

                "tower-flamethrower", "tower-flamethrower-base-new", "tower-flamethrower-shape",
                "tower-flamethrower-weapon-new", "tower-flamethrower-weapon-plasma-new",
                "tower-flamethrower-extra-1-1", "tower-flamethrower-extra-1-2", "tower-flamethrower-extra-2-1", "tower-flamethrower-extra-2-2",
                "tower-flamethrower-extra-powerful-1", "tower-flamethrower-extra-powerful-2",
                "tower-flamethrower-extra-special-1", "tower-flamethrower-extra-special-2",
                "tower-flamethrower-extra-ultimate-1", "tower-flamethrower-extra-ultimate-2",

                "tower-freezing", "tower-freezing-base-new", "tower-freezing-shape",
                "tower-freezing-weapon-new", "tower-freezing-weapon-twisted-new",
                "tower-freezing-extra-1-1", "tower-freezing-extra-1-2", "tower-freezing-extra-2-1", "tower-freezing-extra-2-2",
                "tower-freezing-extra-powerful-1", "tower-freezing-extra-powerful-2", "tower-freezing-extra-special-1", "tower-freezing-extra-special-2",
                "tower-freezing-extra-ultimate-1", "tower-freezing-extra-ultimate-2",

                "tower-gauss", "tower-gauss-base-new", "tower-gauss-shape", "tower-gauss-weapon", "tower-gauss-weapon-long", "tower-gauss-weapon-glow",
                "tower-gauss-extra-1-new", "tower-gauss-extra-2-new", "tower-gauss-extra-powerful-1", "tower-gauss-extra-powerful-2", "tower-gauss-extra-powerful-3",
                "tower-gauss-extra-special-1", "tower-gauss-extra-special-2", "tower-gauss-extra-ultimate-new",

                "tower-laser", "tower-laser-base-new", "tower-laser-shape", "tower-laser-weapon-new", "tower-laser-weapon-mirrors-new",
                "tower-laser-extra-1-new", "tower-laser-extra-2-new", "tower-laser-extra-powerful-1", "tower-laser-extra-powerful-2", "tower-laser-extra-powerful-3",
                "tower-laser-extra-special-1", "tower-laser-extra-special-2", "tower-laser-extra-ultimate-new",

                "tower-minigun", "tower-minigun-base-new", "tower-minigun-shape",
                "tower-minigun-weapon-new", "tower-minigun-weapon-heavy-new", "tower-minigun-weapon-heat-new", "tower-minigun-weapon-heat-heavy-new",
                "tower-minigun-microgun-base", "tower-minigun-microgun-weapon",
                "tower-minigun-extra-1-new", "tower-minigun-extra-2-new", "tower-minigun-extra-powerful-1", "tower-minigun-extra-powerful-2",
                "tower-minigun-extra-special-1", "tower-minigun-extra-special-2", "tower-minigun-extra-ultimate-new",

                "tower-missile", "tower-missile-base-new", "tower-missile-shape", "tower-missile-weapon-new", "tower-missile-weapon-triple-new",
                "tower-missile-extra-1-new", "tower-missile-extra-2-1", "tower-missile-extra-2-2",
                "tower-missile-extra-powerful-1", "tower-missile-extra-powerful-2", "tower-missile-extra-powerful-3",
                "tower-missile-extra-special-1", "tower-missile-extra-special-2", "tower-missile-extra-ultimate-new",

                "tower-multishot", "tower-multishot-base-new", "tower-multishot-shape", "tower-multishot-weapon-new", "tower-multishot-weapon-penetrating-new",
                "tower-multishot-extra-1-new", "tower-multishot-extra-2-new", "tower-multishot-extra-powerful-new",
                "tower-multishot-extra-special-1", "tower-multishot-extra-special-2", "tower-multishot-extra-special-3", "tower-multishot-extra-ultimate-new",

                "tower-sniper", "tower-sniper-base-new", "tower-sniper-shape",
                "tower-sniper-weapon-new", "tower-sniper-weapon-short-new", "tower-sniper-weapon-penetrating-new", "tower-sniper-weapon-short-penetrating-new",
                "tower-sniper-extra-1-new", "tower-sniper-extra-powerful-1", "tower-sniper-extra-powerful-2",
                "tower-sniper-extra-special-1", "tower-sniper-extra-special-2", "tower-sniper-extra-special-3", "tower-sniper-extra-ultimate-new",

                "tower-splash", "tower-splash-base-new", "tower-splash-shape", "tower-splash-weapon-new", "tower-splash-weapon-center-new", "tower-splash-weapon-thin-new",
                "tower-splash-extra-1-1", "tower-splash-extra-1-2", "tower-splash-extra-2-1", "tower-splash-extra-2-2",
                "tower-splash-extra-powerful-1", "tower-splash-extra-powerful-2", "tower-splash-extra-special-1", "tower-splash-extra-special-2",
                "tower-splash-extra-ultimate-1", "tower-splash-extra-ultimate-2",

                "tower-tesla", "tower-tesla-base-new", "tower-tesla-shape", "tower-tesla-weapon-new", "tower-tesla-weapon-high-current-new",
                "tower-tesla-extra-1-new", "tower-tesla-extra-2-new", "tower-tesla-extra-powerful-1", "tower-tesla-extra-powerful-2",
                "tower-tesla-extra-special-1", "tower-tesla-extra-special-2", "tower-tesla-extra-ultimate-1", "tower-tesla-extra-ultimate-2",

                "tower-venom", "tower-venom-base-new", "tower-venom-shape", "tower-venom-weapon-new", "tower-venom-weapon-concentrated-new",
                "tower-venom-extra-1-new", "tower-venom-extra-2-new", "tower-venom-extra-powerful-1", "tower-venom-extra-powerful-2",
                "tower-venom-extra-special-1", "tower-venom-extra-special-2", "tower-venom-extra-ultimate-1", "tower-venom-extra-ultimate-2",

                "tower-menu-stat-line"
        };
    }

    private String[] getEnemyAssets() {
        return new String[]{
                "enemy-type-armored", "enemy-type-armored-emj", "enemy-type-armored-hl",
                "enemy-type-boss",
                "enemy-type-boss-broot",
                "enemy-type-boss-constructor",
                "enemy-type-boss-metaphor", "enemy-type-boss-metaphor-body", "enemy-type-boss-metaphor-creep", "enemy-type-boss-metaphor-leg",
                "enemy-type-boss-mobchain", "enemy-type-boss-mobchain-body", "enemy-type-boss-mobchain-creep", "enemy-type-boss-mobchain-head",
                "enemy-type-boss-snake-head", "enemy-type-boss-snake-body", "enemy-type-boss-snake-tail",
                "enemy-type-fast", "enemy-type-fast-emj", "enemy-type-fast-hl",
                "enemy-type-fighter", "enemy-type-fighter-emj", "enemy-type-fighter-hl", "enemy-type-fighter-small",
                "enemy-type-healer", "enemy-type-healer-emj", "enemy-type-healer-hl",
                "enemy-type-heli", "enemy-type-heli-emj", "enemy-type-heli-hl",
                "enemy-type-icy", "enemy-type-icy-emj", "enemy-type-icy-hl",
                "enemy-type-jet", "enemy-type-jet-emj", "enemy-type-jet-hl",
                "enemy-type-light", "enemy-type-light-emj", "enemy-type-light-hl",
                "enemy-type-regular", "enemy-type-regular-emj", "enemy-type-regular-hl",
                "enemy-type-strong", "enemy-type-strong-emj", "enemy-type-strong-hl",
                "enemy-type-toxic", "enemy-type-toxic-emj", "enemy-type-toxic-hl",
                "boss-wave-icon-BROOT", "boss-wave-icon-CONSTRUCTOR", "boss-wave-icon-METAPHOR", "boss-wave-icon-MOBCHAIN", "boss-wave-icon-SNAKE",
                "enemy-dialog", "enemy-ice-overlay-1", "enemy-ice-overlay-2",
                "unit-type-ball-lightning", "unit-type-snowball"
        };
    }

    private String[] getMinerAssets() {
        return new String[]{
                "miner-infiar", "miner-infiar-base", "miner-infiar-blade",
                "miner-matrix", "miner-matrix-base", "miner-matrix-blade",
                "miner-scalar", "miner-scalar-base", "miner-scalar-blade",
                "miner-tensor", "miner-tensor-base", "miner-tensor-blade",
                "miner-vector", "miner-vector-base", "miner-vector-blade",
                "resource-infiar", "resource-matrix", "resource-scalar", "resource-tensor", "resource-vector",
                "resource-orb-infiar", "resource-orb-matrix", "resource-orb-scalar", "resource-orb-tensor", "resource-orb-vector",
                "mine"
        };
    }

    private String[] getProjectileVFXAssets() {
        return new String[]{
                "projectile-air", "projectile-basic", "projectile-buff", "projectile-bullet-wall",
                "projectile-cannon", "projectile-cannon-splinter", "projectile-missile", "projectile-multishot",
                "projectile-splash", "projectile-venom",
                "particle-anger", "particle-default", "particle-default-long", "particle-flame", "particle-ice",
                "particle-killshot", "particle-lens-flare", "particle-lightning-0", "particle-lightning-1", "particle-lightning-2",
                "particle-one", "particle-paper", "particle-pentagon", "particle-plus", "particle-shield",
                "particle-shockwave", "particle-shockwave-twirled", "particle-shockwave-twirled-fat",
                "particle-smoke", "particle-snowflake", "particle-star", "particle-triangle", "particle-twist", "particle-zero",
                "muzzle-flash-1", "muzzle-flash-2", "muzzle-flash-compensator-1", "muzzle-flash-compensator-2", "muzzle-flash-small",
                "bullet-trace", "bullet-trace-smoke", "bullet-trace-thin",
                "spark-a", "spark-b", "spark-c", "spark-d",
                "splatter-1", "splatter-2", "splatter-3",
                "explosion",
                "3d-sphere", "3d-sphere-basic",
                "attention-rays-0", "attention-rays-1", "attention-rays-2", "attention-rays-3",
                "aura-range", "ball-lightning-orb", "chain-lightning", "chain-lightning-edge", "chain-lightning-straight", "chain-lightning-wide",
                "jet-thrust", "laser", "laser-cap", "laser-wide",
                "overload-impulse-a", "overload-impulse-b", "overload-impulse-c", "overload-impulse-d",
                "freezing-monitoring-trace", "circle"
        };
    }

    private String[] getLootAssets() {
        return new String[]{
                "chest-blue", "chest-blue-encrypted", "chest-cyan", "chest-cyan-encrypted", "chest-green", "chest-green-encrypted",
                "chest-orange", "chest-orange-encrypted", "chest-pink", "chest-pink-encrypted", "chest-purple", "chest-purple-encrypted",
                "money-pack-double-gain", "money-pack-huge", "money-pack-large", "money-pack-medium", "money-pack-small", "money-pack-tiny",
                "accelerator-pack-huge", "accelerator-pack-large", "accelerator-pack-medium", "accelerator-pack-small", "accelerator-pack-tiny",
                "flying-paper-1-1", "flying-paper-1-2", "flying-paper-1-3", "flying-paper-1-4", "flying-paper-5-1", "flying-paper-20-1",
                "flying-paper-100-1", "flying-paper-200-1", "flying-paper-500-1", "flying-paper-1000-1", "flying-paper-5000-1", "flying-paper-10000-1",
                "blueprint-AGILITY", "blueprint-EXPERIENCE", "blueprint-POWER", "blueprint-SPECIAL_I", "blueprint-SPECIAL_II", "blueprint-SPECIAL_III", "blueprint-SPECIAL_IV",
                "ability-token", "loot-token", "lucky-shot-token", "prestige-token", "rarity-token", "research-token", "research-token-used", "time-accelerator",
                "dust-item", "dust-item-prestige",
                "trophy-MINER_INFIAR", "trophy-MINER_MATRIX", "trophy-MINER_SCALAR", "trophy-MINER_TENSOR", "trophy-MINER_VECTOR",
                "trophy-MODIFIER_ATTACK_SPEED", "trophy-MODIFIER_BALANCE", "trophy-MODIFIER_BOUNTY", "trophy-MODIFIER_DAMAGE", "trophy-MODIFIER_EXPERIENCE",
                "trophy-MODIFIER_MINING_SPEED", "trophy-MODIFIER_POWER", "trophy-MODIFIER_SEARCH",
                "trophy-RESOURCE_INFIAR", "trophy-RESOURCE_MATRIX", "trophy-RESOURCE_SCALAR", "trophy-RESOURCE_TENSOR", "trophy-RESOURCE_VECTOR",
                "trophy-SPECIAL_DEVELOPER", "trophy-SPECIAL_MASTER", "trophy-SPECIAL_MILLION", "trophy-SPECIAL_STORYLINE",
                "trophy-TOWER_AIR", "trophy-TOWER_BASIC", "trophy-TOWER_BLAST", "trophy-TOWER_CANNON", "trophy-TOWER_FLAMETHROWER",
                "trophy-TOWER_FREEZING", "trophy-TOWER_LASER", "trophy-TOWER_MINIGUN", "trophy-TOWER_MISSILE", "trophy-TOWER_MULTISHOT",
                "trophy-TOWER_SNIPER", "trophy-TOWER_SPLASH", "trophy-TOWER_TESLA", "trophy-TOWER_VENOM",
                "modifier-base-attack-speed", "modifier-base-balance", "modifier-base-bounty", "modifier-base-damage", "modifier-base-experience",
                "modifier-base-mining-speed", "modifier-base-power", "modifier-base-search",
                "modifier-miniature-attack-speed", "modifier-miniature-balance", "modifier-miniature-bounty", "modifier-miniature-damage", "modifier-miniature-experience",
                "modifier-miniature-mining-speed", "modifier-miniature-power", "modifier-miniature-search",
                "modifier-icon-wires", "modifier-wires-bottom", "modifier-wires-bottom-left", "modifier-wires-bottom-right",
                "modifier-wires-left", "modifier-wires-right", "modifier-wires-top", "modifier-wires-top-left", "modifier-wires-top-right"
        };
    }

    private String[] getIconAssets() {
        return new String[]{
                "icon-ability", "icon-aim-time", "icon-arrow-pointer-bottom", "icon-arrow-pointer-bottom-left", "icon-arrow-pointer-right",
                "icon-arrow-pointer-right-bottom", "icon-arrow-pointer-right-top", "icon-arrow-pointer-top-left", "icon-arrow-pointer-top-right",
                "icon-astar", "icon-attack-speed", "icon-backpack", "icon-backpack-arrow-down", "icon-badge", "icon-bag-pipe",
                "icon-ball-lightning", "icon-ball-lightning-clock", "icon-bass-guitar", "icon-battery", "icon-battery-max",
                "icon-binary-search", "icon-blizzard", "icon-blueprint", "icon-book-closed", "icon-book-opened",
                "icon-brass", "icon-break", "icon-brute-force-search", "icon-bubble-sort", "icon-build-price",
                "icon-build-time", "icon-bullet", "icon-bullet-wall", "icon-cache", "icon-calendar", "icon-camera", "icon-capacity",
                "icon-cello", "icon-check", "icon-chest", "icon-clipboard", "icon-clock", "icon-clock-max", "icon-clock-min", "icon-clock-plus",
                "icon-clockwise", "icon-cloud-download", "icon-cloud-upload", "icon-coin", "icon-coins",
                "icon-colored-discord", "icon-colored-facebook", "icon-colored-reddit", "icon-count", "icon-counter-clockwise",
                "icon-critical-damage", "icon-critical-damage-percent", "icon-crosshair", "icon-crown",
                "icon-cubes-stacked", "icon-cubes-stacked-flame", "icon-cubes-stacked-tall", "icon-cuckoo",
                "icon-damage", "icon-damage-multiplier", "icon-damage-split", "icon-dat-paper", "icon-density-high", "icon-density-low", "icon-density-medium",
                "icon-dijkstras", "icon-division", "icon-dollar", "icon-double-arrow-up", "icon-double-triangle-right",
                "icon-drag-tile", "icon-drum", "icon-drum-set", "icon-dust", "icon-easel", "icon-edit",
                "icon-effect-global", "icon-effect-level", "icon-enemy-heart", "icon-enemy-speed", "icon-enlarge",
                "icon-exclamation-triangle", "icon-exit", "icon-experience", "icon-experience-balance", "icon-experience-bar",
                "icon-experience-generation", "icon-experience-generation-lite", "icon-experience-max", "icon-experience-plus",
                "icon-explosion-range", "icon-explosion-shrapnel", "icon-eye", "icon-factory",
                "icon-fireball", "icon-firestorm", "icon-firestorm-clock", "icon-five-stars", "icon-flag", "icon-flame", "icon-flame-clock",
                "icon-flame-damage", "icon-flame-percent", "icon-floppy", "icon-flute", "icon-four-stars", "icon-freeze-in-time",
                "icon-freeze-multiple-plus", "icon-freeze-percent", "icon-fx", "icon-gate", "icon-gate-health", "icon-gate-health-high",
                "icon-gate-health-low", "icon-gate-teleport", "icon-gate-type", "icon-gift", "icon-globe", "icon-google-play",
                "icon-greek-alpha", "icon-greek-beta", "icon-greek-delta", "icon-greek-gamma", "icon-greek-theta", "icon-greek-xi", "icon-greek-zeta",
                "icon-guitar", "icon-hash", "icon-heart", "icon-hearts", "icon-house", "icon-index-mapping",
                "icon-infinitode-1-logo", "icon-infinitode-2-logo", "icon-infinity", "icon-info", "icon-info-circle", "icon-info-square",
                "icon-insert-tile", "icon-ios-app-store", "icon-item-pack", "icon-joystick", "icon-joystick-google-play", "icon-key",
                "icon-large-fonts", "icon-laser", "icon-leaf", "icon-letter", "icon-lightning-bolt", "icon-lightning-bolt-time",
                "icon-lightning-damage", "icon-lightning-damage-2", "icon-lightning-length", "icon-link-out", "icon-lm-algorithm",
                "icon-locale", "icon-lock", "icon-lock-unlocked", "icon-lock-vertical", "icon-loic", "icon-loic-clock", "icon-loop",
                "icon-loot-rarity", "icon-lrm", "icon-lstm-network", "icon-lucky-wheel", "icon-lucky-wheel-plus", "icon-lvl-plus",
                "icon-magazine-size", "icon-magnet", "icon-magnifying-glass", "icon-max", "icon-mdps", "icon-meltdown", "icon-menu",
                "icon-merge-sort", "icon-min", "icon-miner-top", "icon-minus", "icon-modifier",
                "icon-modifier-attack-speed-research", "icon-modifier-balance-research", "icon-modifier-bounty-research",
                "icon-modifier-damage-research", "icon-modifier-experience-research", "icon-modifier-mining-speed-research",
                "icon-modifier-power-research", "icon-modifier-search-research", "icon-money", "icon-mouse-wheel", "icon-music-player",
                "icon-new-item", "icon-note", "icon-nuke", "icon-organ", "icon-overload", "icon-pan-zoom", "icon-pause",
                "icon-penetration-damage", "icon-percent", "icon-piano", "icon-pickaxe", "icon-piercing-projectile", "icon-pin",
                "icon-platform", "icon-plus", "icon-poison", "icon-power", "icon-power-plus", "icon-projectile-count", "icon-projectile-speed",
                "icon-projectile-weight", "icon-quake", "icon-quest", "icon-quest-difficulty", "icon-quest-prize", "icon-quest-slot", "icon-question",
                "icon-quicksort", "icon-rainbow-table", "icon-range", "icon-range-damage", "icon-range-minimum", "icon-reed",
                "icon-remove-tile", "icon-research", "icon-restart", "icon-rewind", "icon-road", "icon-rocket", "icon-rocket-aim-time",
                "icon-rocket-angle", "icon-rocket-heart", "icon-rotation-and-projectile-speed", "icon-rotation-speed",
                "icon-rp-algorithm", "icon-sand-clock", "icon-scg-algorithm", "icon-screen", "icon-sell-refund", "icon-shop-cart",
                "icon-shot-angle", "icon-size", "icon-skill-point", "icon-skull", "icon-skull-and-bones", "icon-skull-and-bones-clock",
                "icon-skull-and-bones-clock-plus", "icon-smoke-bomb", "icon-smoke-bomb-clock", "icon-snowball", "icon-snowball-plus",
                "icon-speaker", "icon-speaker-crossed", "icon-speed-high", "icon-speed-low", "icon-speed-medium", "icon-speed-pause",
                "icon-speedometer-clock", "icon-star", "icon-star-hollow", "icon-star-stack", "icon-statistics", "icon-step-forward",
                "icon-stopwatch", "icon-string-ensemble", "icon-stun", "icon-stun-clock", "icon-swords", "icon-synth-effect",
                "icon-synth-lead", "icon-synth-pad", "icon-target", "icon-target-first", "icon-target-last", "icon-target-near",
                "icon-target-random", "icon-target-strong", "icon-target-weak", "icon-terminal", "icon-three-stars", "icon-thumbs-up",
                "icon-thunder", "icon-tile", "icon-tile-random", "icon-time-accelerator", "icon-times", "icon-timsort", "icon-token",
                "icon-tools", "icon-tower", "icon-tower-top", "icon-trash-bin", "icon-trash-bin-dollar",
                "icon-triangle-bottom", "icon-triangle-bottom-hollow", "icon-triangle-left", "icon-triangle-left-hollow",
                "icon-triangle-right", "icon-triangle-right-hollow", "icon-triangle-top", "icon-triangle-top-hollow",
                "icon-trophy", "icon-turbo", "icon-two-stars", "icon-upgrade", "icon-upgrade-max", "icon-upgrade-money", "icon-user",
                "icon-users", "icon-wave", "icon-wave-money", "icon-wave-time", "icon-weight", "icon-windstorm", "icon-windstorm-clock",
                "icon-wrench", "icon-x2", "icon-xylophone"
        };
    }

    private String[] getUIAssets() {
        return new String[]{
                "ui-ability-button", "ui-ability-button-edges", "ui-ability-button-empty", "ui-ability-button-empty-plus",
                "ui-ability-button-energy-mark", "ui-ability-button-selection", "ui-ability-energy-bar", "ui-ability-selection-token-button",
                "ui-aim-strategy-switch-item-background", "ui-auto-force-wave-overlay", "ui-back-button", "ui-core-menu-upgrade-button",
                "ui-core-menu-upgrade-selection", "ui-daily-loot-legendaries", "ui-daily-quest-play-button",
                "ui-dialog-background-1", "ui-dialog-background-2", "ui-dialog-background-3", "ui-dialog-button-left", "ui-dialog-button-right",
                "ui-exp-line-cap", "ui-exp-line-end", "ui-forward-button", "ui-game-over-leaderboards-rank",
                "ui-game-over-left-button", "ui-game-over-overlay-left-button", "ui-game-over-overlay-right-button", "ui-game-over-prestige-button",
                "ui-horizontal-slider-handle", "ui-inventory-decoding-small-button", "ui-inventory-toggle-button",
                "ui-item-count-selector-cancel-button", "ui-item-count-selector-confirm-button", "ui-item-count-selector-minus-button",
                "ui-item-count-selector-plus-button", "ui-item-count-selector-scroll-button", "ui-leaderboard-switch-button",
                "ui-level-selection-milestone-top", "ui-level-selection-new-level-tag", "ui-level-selection-overlay-button",
                "ui-level-selection-thumb-inner-shadow", "ui-level-selection-thumb-shadow-bottom", "ui-level-selection-thumb-shadow-right",
                "ui-level-selection-vertical-scroll-hint", "ui-level-selection-waves-timeline-button", "ui-lucky-wheel-handle",
                "ui-main-menu-dq-preview", "ui-map-editor-tile-info-menu-tab", "ui-map-prestige-button-left", "ui-map-prestige-button-right",
                "ui-money-screen-button", "ui-money-screen-button-edge", "ui-money-screen-button-small-bottom",
                "ui-money-screen-button-small-bottom-edge", "ui-money-screen-button-small-top", "ui-money-screen-button-small-top-edge",
                "ui-pause-button-video-ad-icon", "ui-quest-list-background", "ui-quest-list-title-background",
                "ui-quests-prestige-milestone-mark", "ui-research-menu-top", "ui-rewarding-ads-notch-bg",
                "ui-right-menu-button", "ui-screen-border-0", "ui-screen-border-1", "ui-screen-border-2", "ui-screen-border-3",
                "ui-screen-border-5", "ui-screen-border-6", "ui-screen-border-7", "ui-screen-border-8",
                "ui-sell-button", "ui-stopwatch-timer-background",
                "ui-tile-inventory-cell-1", "ui-tile-inventory-cell-2", "ui-tile-inventory-cell-count", "ui-tile-menu-background",
                "ui-tile-menu-details-bottom", "ui-tile-menu-details-center", "ui-tile-menu-details-right", "ui-tile-menu-details-toggle-button",
                "ui-tile-menu-details-top", "ui-tile-menu-side-tooltip", "ui-tile-menu-tab-active", "ui-tile-menu-tab-background",
                "ui-tile-menu-tab-inactive-left", "ui-tile-menu-tab-inactive-right", "ui-tile-menu-toggle-button",
                "ui-top-bar-ad-available", "ui-top-bar-exit", "ui-top-bar-money", "ui-tower-ability-outline",
                "ui-upgrade-button", "ui-upgrade-level-line", "ui-upgrade-level-line-start",
                "player-level-1", "player-level-2", "player-level-3", "player-level-4", "player-level-5", "player-level-6", "player-level-7", "player-level-8", "player-level-9", "player-level-10",
                "player-level-11", "player-level-12", "player-level-13", "player-level-14", "player-level-15", "player-level-16", "player-level-17", "player-level-18", "player-level-19", "player-level-20",
                "player-level-21", "player-level-22", "player-level-23", "player-level-24", "player-level-25", "player-level-26", "player-level-27", "player-level-28", "player-level-29", "player-level-30",
                "player-level-max",
                "player-level-digit-0", "player-level-digit-1", "player-level-digit-2", "player-level-digit-3", "player-level-digit-4", "player-level-digit-5", "player-level-digit-6", "player-level-digit-7", "player-level-digit-8", "player-level-digit-9",
                "player-profile-avatar-frame",
                "pb-bg-artifact", "pb-bg-common", "pb-bg-epic", "pb-bg-legendary", "pb-bg-not-received", "pb-bg-rare", "pb-bg-supreme", "pb-bg-very-rare",
                "pb-icon-daily-game", "pb-icon-invited-players", "pb-icon-killed-enemies", "pb-icon-mined-resources",
                "pb-over-daily-game", "pb-over-invited-players", "pb-over-killed-enemies", "pb-over-mined-resources",
                "buff-health-bar-icon-armor", "buff-health-bar-icon-blast-throw-back", "buff-health-bar-icon-blizzard", "buff-health-bar-icon-bonus-coins",
                "buff-health-bar-icon-bonus-xp", "buff-health-bar-icon-burn", "buff-health-bar-icon-chain-reaction", "buff-health-bar-icon-death-explosion",
                "buff-health-bar-icon-freezing", "buff-health-bar-icon-invulnerability", "buff-health-bar-icon-no-bonus-system-points", "buff-health-bar-icon-no-damage",
                "buff-health-bar-icon-poison", "buff-health-bar-icon-poison-three", "buff-health-bar-icon-poison-two", "buff-health-bar-icon-regeneration",
                "buff-health-bar-icon-slipping", "buff-health-bar-icon-snowball", "buff-health-bar-icon-stun", "buff-health-bar-icon-vulnerability",
                "build-selection", "build-selection-count", "build-selection-count-overlay", "build-selection-hover",
                "game-ui-coin-icon", "game-ui-health-icon", "global-upgrades-icon-background", "global-upgrades-icon-background-invisible",
                "global-upgrades-icon-background-small", "global-upgrades-icon-level-overlay", "global-upgrades-icon-selection", "global-upgrades-icon-selection-small"
        };
    }

    private String[] getMiscAssets() {
        return new String[]{
                "font", "font-cjk", "font-damage-especially-effective", "font-damage-over-time", "font-damage-regular", "font-debug", "font-jp",
                "gradient-bottom", "gradient-corner-bottom-left", "gradient-corner-bottom-right", "gradient-corner-top-left", "gradient-corner-top-right",
                "gradient-horizontal", "gradient-left", "gradient-radial-bottom", "gradient-radial-top", "gradient-right", "gradient-top",
                "infinitode-2-logo", "infinitode-2-logo-colored-small", "sdf-infinitode-2-logo", "steam-icon", "google-g-icon",
                "gem-pentagon-base", "gem-pentagon-overlay", "gem-square-base", "gem-square-overlay", "gem-triangle-base", "gem-triangle-overlay",
                "gpmod-add-all-ability-charges", "gpmod-add-random-core-tile", "gpmod-add-random-platform", "gpmod-all-abilities-for-random-tower",
                "gpmod-base-explodes-on-enemy-pass", "gpmod-bonus-damage-per-buff", "gpmod-boost-existing-enemies-with-loot", "gpmod-build-random-miner",
                "gpmod-coin-generation", "gpmod-critical-damage", "gpmod-debuffs-last-longer", "gpmod-double-mining-speed", "gpmod-enemies-drop-resources",
                "gpmod-first-enemies-in-wave-explode", "gpmod-increase-selected-bonuses-power", "gpmod-last-enemies-in-wave-deal-no-damage",
                "gpmod-lightning-strike-on-tower-level-up", "gpmod-low-hp-enemies-deal-no-damage", "gpmod-mine-legendary-items",
                "gpmod-mined-items-turn-into-dust", "gpmod-miners-spawn-enemies", "gpmod-more-bonus-variants-next-time", "gpmod-multiply-looted-items",
                "gpmod-multiply-mdps", "gpmod-nuke-on-bonus-stage", "gpmod-receive-coins", "gpmod-sell-all-towers", "gpmod-spawn-zombies-from-base",
                "gpmod-summon-loot-boss", "gpmod-towers-attack-speed", "gpmod-towers-damage", "gpmod-trigger-random-ability",
                "item-cell-a", "item-cell-a-coat-COMMON", "item-cell-a-coat-EPIC", "item-cell-a-coat-LEGENDARY", "item-cell-a-coat-RARE", "item-cell-a-coat-VERY_RARE",
                "item-cell-a-shape", "item-cell-b", "item-cell-b-coat-COMMON", "item-cell-b-coat-EPIC", "item-cell-b-coat-LEGENDARY", "item-cell-b-coat-RARE",
                "item-cell-b-coat-VERY_RARE", "item-cell-b-shape", "item-cell-game-value-global", "item-cell-game-value-level",
                "item-cell-glow", "item-cell-number-bg", "item-gate-barrier-type-icon", "item-gate-teleport-icon",
                "level-select-goal-scale-check", "level-select-goal-scale-triangle", "level-select-wave-marker-boss-frame",
                "settings-toggle-off", "settings-toggle-on", "shop-ad-bar-reflection", "small-circle", "small-circle-outline", "small-triangle-mark-bottom",
                "tiny-arrow-bottom", "tiny-arrow-bottom-left", "tiny-arrow-bottom-right", "tiny-arrow-left", "tiny-arrow-right", "tiny-arrow-top",
                "tiny-arrow-top-left", "tiny-arrow-top-right", "upgrade-arrow-up", "checkbox", "checkbox-checked",
                "path-arrow-bottom", "path-arrow-left", "path-arrow-right", "path-arrow-up",
                "blizzard-screen-border", "blank", "button-hold-mark", "button-hold-mark-white", "coin-small", "count-bubble", "crosshair-small", "crusher-chain",
                "dashed-line", "double-gain-shard", "encounter-bird-wing-left", "encounter-bird-wing-right", "game-over-ad-button-glow",
                "line", "loading-icon", "lstm-network", "lucky-shot", "lucky-wheel-x2", "lucky-wheel-x3", "main-menu-check-outline",
                "map-preview-placeholder", "no-texture", "placeholder", "rewarding-ad", "rounded-small-rect", "xp-orb"
        };
    }

    // ============================================================================================
    // ===================================== VIEW CREATION ========================================
    // ============================================================================================

    private ScrollPane createTileGrid(String[] tileNames) {
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));
        flowPane.setStyle("-fx-background-color: #2a2a2a;");
        flowPane.setAlignment(Pos.TOP_LEFT);

        for (String name : tileNames) {
            try {
                if (name != null && !name.isEmpty()) {
                    VBox tileBox = createTileDisplay(name);
                    flowPane.getChildren().add(tileBox);
                }
            } catch (Exception e) {
                // Keep going even if one fails
            }
        }

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2a2a2a; -fx-background-color: #2a2a2a;");
        return scrollPane;
    }

    private VBox createTileDisplay(String tileName) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444444; -fx-border-width: 1; -fx-background-radius: 3; -fx-border-radius: 3;");

        // Reasonable size for viewing many assets
        box.setPrefWidth(120);
        box.setPrefHeight(140);

        try {
            Image tileImage = AtlasManager.getInstance().getAtlas().getRegion(tileName);

            if (tileImage != null) {
                ImageView imageView = new ImageView(tileImage);
                imageView.setFitWidth(64);
                imageView.setFitHeight(64);
                imageView.setPreserveRatio(true);
                box.getChildren().add(imageView);
            } else {
                Label errorLabel = new Label("NULL IMG");
                errorLabel.setStyle("-fx-text-fill: red;");
                box.getChildren().add(errorLabel);
            }

            Label nameLabel = new Label(tileName);
            nameLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 9px;");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(110);
            nameLabel.setAlignment(Pos.CENTER);

            box.getChildren().add(nameLabel);

        } catch (Exception e) {
            Label errorLabel = new Label("MISSING");
            errorLabel.setStyle("-fx-text-fill: #FF4444; -fx-font-size: 10px;");
            box.getChildren().add(errorLabel);
        }

        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}