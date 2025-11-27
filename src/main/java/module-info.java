module com.eliemichel.polyfinite {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires javafx.media;

    opens com.eliemichel.polyfinite to javafx.fxml;
    opens com.eliemichel.polyfinite.game to com.google.gson;
    opens com.eliemichel.polyfinite.game.quests to com.google.gson;
    opens com.eliemichel.polyfinite.ui to javafx.fxml;
    opens com.eliemichel.polyfinite.editor to javafx.fxml;

    exports com.eliemichel.polyfinite;
    exports com.eliemichel.polyfinite.editor;
    exports com.eliemichel.polyfinite.utils;
    exports com.eliemichel.polyfinite.game;
    exports com.eliemichel.polyfinite.game.quests;
    exports com.eliemichel.polyfinite.ui;
}