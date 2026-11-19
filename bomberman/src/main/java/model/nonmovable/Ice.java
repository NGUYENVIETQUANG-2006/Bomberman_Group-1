package model.nonmovable;

import component.Item;
import javafx.scene.image.Image;

public class Ice extends Brick {
    private static final Image ICE_IMAGE = new Image(
            Ice.class.getResource("/picture/Ice/jpg_03.png").toExternalForm());

    public Ice(double x, double y) {
        this(x, y, null);
    }

    public Ice(double x, double y, Item drop) {
        super(x, y, drop);
        setImage(ICE_IMAGE);
    }
}
