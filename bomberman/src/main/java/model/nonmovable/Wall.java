package model.nonmovable;

import component.Entity;
import javafx.scene.image.Image;

public class Wall extends Entity {
    private static final Image WALL_IMAGE = new Image(Wall.class.getResource(
            "/picture/Wall/PalmOS - Bomberman for Palm OS - Miscellaneous - Bomberman, Enemies, Bombs, Items, & Blocks_03.png")
            .toExternalForm());

    public Wall(double x, double y) {
        super(x, y);
        setImage(WALL_IMAGE);
    }

    @Override
    public void update(double dt) {}
}
