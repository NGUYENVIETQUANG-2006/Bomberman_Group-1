package model.nonmovable;

import component.Entity;
import javafx.scene.image.Image;

public class Wall extends Entity {
    private static final Image WALL_IMAGE = new Image(Wall.class.getResource(
            "/picture/Ice/jpg_06.png")
            .toExternalForm());

    public Wall(double x, double y) {
        super(x, y);
        setImage(WALL_IMAGE);
    }

    @Override
    public void update(double dt) {}
}
