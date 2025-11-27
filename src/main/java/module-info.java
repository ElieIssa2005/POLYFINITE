module com.eliemichel.polyfinite {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires javafx.media;

    opens com.eliemichel.polyfinite to javafx.fxml;
    opens com.eliemichel.polyfinite.domain.level to com.google.gson;
    opens com.eliemichel.polyfinite.domain.quests to com.google.gson;
    opens com.eliemichel.polyfinite.domain.quests.types to com.google.gson;
    opens com.eliemichel.polyfinite.domain.player to com.google.gson;
    opens com.eliemichel.polyfinite.ui.menu to javafx.fxml;
    opens com.eliemichel.polyfinite.ui.map to javafx.fxml;
    opens com.eliemichel.polyfinite.ui.gameplay to javafx.fxml;
    opens com.eliemichel.polyfinite.ui.endlevel to javafx.fxml;
    opens com.eliemichel.polyfinite.ui.screens.research to javafx.fxml;
    opens com.eliemichel.polyfinite.editor to javafx.fxml;

    exports com.eliemichel.polyfinite;
    exports com.eliemichel.polyfinite.editor;
    exports com.eliemichel.polyfinite.utils;
    exports com.eliemichel.polyfinite.infrastructure.database;
    exports com.eliemichel.polyfinite.domain.player;
    exports com.eliemichel.polyfinite.domain.level;
    exports com.eliemichel.polyfinite.domain.progression;
    exports com.eliemichel.polyfinite.domain.quests;
    exports com.eliemichel.polyfinite.domain.quests.types;
    exports com.eliemichel.polyfinite.domain.enemies;
    exports com.eliemichel.polyfinite.domain.enemies.types;
    exports com.eliemichel.polyfinite.domain.towers;
    exports com.eliemichel.polyfinite.domain.towers.types;
    exports com.eliemichel.polyfinite.domain.tiles;
    exports com.eliemichel.polyfinite.application.gameplay;
    exports com.eliemichel.polyfinite.ui.menu;
    exports com.eliemichel.polyfinite.ui.map;
    exports com.eliemichel.polyfinite.ui.gameplay;
    exports com.eliemichel.polyfinite.ui.endlevel;
    exports com.eliemichel.polyfinite.ui.screens.research;
}