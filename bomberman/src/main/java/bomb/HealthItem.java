package bomb;

public class HealthItem extends Item {
    public HealthItem(double x, double y) {
        super(x, y);
    }
 
    @Override
    public void applyTo(Player player) {
        player.addHealth();
    }
}
