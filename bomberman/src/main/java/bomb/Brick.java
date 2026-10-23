package bomb;

public class Brick extends Entity {
    private boolean destroyed = false;
    private final ItemSpawner itemSpawner = new ItemSpawner();
 
    public Brick(double x, double y) {
        super(x, y);
    }
 
    @Override
    public void update(double dt) {}

    @Override
    public void onFlameHit() {
        destroy();
    }
 
    public void destroy() {
        destroyed = true;
    }

    Item rollDrop() {
        return itemSpawner.rollDrop(x, y);
    }
 
    public boolean isDestroyed() {
        return destroyed;
    }
}