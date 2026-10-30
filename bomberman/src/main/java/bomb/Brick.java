package bomb;

public class Brick extends Entity {
    private boolean destroyed = false; 
    private final Item drop;

    public Brick(double x, double y) {
        this(x, y, null);
    }

    public Brick(double x, double y, Item drop) {
        super(x, y);
        this.drop = drop;
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

    public Item getDrop() {
        return drop;
    }
}
