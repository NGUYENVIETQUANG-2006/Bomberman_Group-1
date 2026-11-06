module bomberman {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.media;

    exports component;
    exports core to javafx.graphics;
    exports core.input;
    exports core.map;
    exports UI;
    exports model.item;
    exports model.movable;
    exports model.movable.npc;
    exports model.nonmovable;
}
