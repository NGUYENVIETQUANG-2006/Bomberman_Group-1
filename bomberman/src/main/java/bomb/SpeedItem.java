package bomb;

public class SpeedItem extends Item {
    public SpeedItem(double x, double y) {
        super(x, y);
    }

    @Override
    public void applyTo(Player player) {
        player.increaseSpeed();
    }
}
