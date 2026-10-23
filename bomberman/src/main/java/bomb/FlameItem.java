package bomb;

public class FlameItem extends Item {
    public FlameItem(double x, double y) {
        super(x, y);
    }
 
    @Override
    public void applyTo(Player player) {
        player.increaseRange();
    }
}
