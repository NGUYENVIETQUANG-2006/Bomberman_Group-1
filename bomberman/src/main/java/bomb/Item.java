package bomb;

public abstract class Item extends Entity {
 
    public Item(double x, double y) {
        super(x, y);
    }

    public abstract void applyTo(Player player);

    @Override
    public void update(double dt) {
    }
}
