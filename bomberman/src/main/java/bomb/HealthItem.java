package bomb;

import javafx.scene.image.Image;

public class HealthItem extends Item {
    private static final Image ITEM_IMAGE = new Image(HealthItem.class.getResource("/picture/Item/Health_Item.png").toExternalForm());

    public HealthItem(double x, double y) {
        super(x, y);
        setImage(ITEM_IMAGE);
    }
 
    @Override
    public void applyTo(Player player) {
        player.addHealth();
    }
}
