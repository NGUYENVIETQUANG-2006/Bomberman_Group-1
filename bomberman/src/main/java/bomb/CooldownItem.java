package bomb;

public class CooldownItem extends Item {
 
    public CooldownItem(double x, double y) {
        super(x, y);
    }
 
    @Override
    public void applyTo(Player player) {
        player.reduceCooldown();
    }
}
