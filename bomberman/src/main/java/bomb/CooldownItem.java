package bomb;

import javafx.scene.image.Image;

public class CooldownItem extends Item {
    private static final Image ITEM_IMAGE = new Image(CooldownItem.class.getResource("/picture/Item/Cooldown_Item.png").toExternalForm());
 
    public CooldownItem(double x, double y) {
        super(x, y);
        setImage(ITEM_IMAGE);
    }
 
    @Override
    public void applyTo(Player player) {
        player.reduceCooldown();
    }
}
