package bomb;

public class Brick extends Entity {
    private boolean destroyed = false;
    ItemSpawner its = new ItemSpawner();
 
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
 
    public boolean isDestroyed() {
        return destroyed;
    }
}