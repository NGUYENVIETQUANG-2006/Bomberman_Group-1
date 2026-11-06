package bomb;

import javafx.scene.image.Image;

public class SpeedItem extends Item {
    private static final Image ITEM_IMAGE = new Image(SpeedItem.class.getResource("/picture/Item/Speed_Item.png").toExternalForm());

    public SpeedItem(double x, double y) {
        super(x, y);
        setImage(ITEM_IMAGE);
    }

    @Override
    public void applyTo(Player player) {
        player.increaseSpeed();
    }
}
