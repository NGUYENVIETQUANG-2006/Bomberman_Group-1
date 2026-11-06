package model.item;

import component.Item;
import javafx.scene.image.Image;
import model.movable.Player;

public class FlameItem extends Item {
    private static final Image ITEM_IMAGE = new Image(FlameItem.class.getResource("/picture/Item/Flame_Item.png").toExternalForm());

    public FlameItem(double x, double y) {
        super(x, y);
        setImage(ITEM_IMAGE);
    }
 
    @Override
    public void applyTo(Player player) {
        player.increaseRange();
    }
}
