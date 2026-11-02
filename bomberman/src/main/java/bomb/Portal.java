package bomb;

import javafx.scene.image.Image;

public class Portal extends Entity {
    private static final Image DOOR_IMAGE = new Image(
            Portal.class.getResource("/picture/Door/Custom _ Edited - Bomberman Customs - Enemies - Bomberman Enemies (7 Styles)(1).png")
                    .toExternalForm());

    public Portal(double x, double y) {
        super(x, y);
        setImage(DOOR_IMAGE);
    }
 
    @Override
    public void update(double dt) {}
}
