package bomb;

public abstract class Item extends Entity {
    protected static final double DEFAULT_LIFETIME = 15.0;
 
    private double lifeTime;
 
    public Item(double x, double y) {
        this(x, y, DEFAULT_LIFETIME);
    }
 
    public Item(double x, double y, double lifeTime) {
        super(x, y);
        this.lifeTime = lifeTime;
    }

    public abstract void applyTo(Player player);
 
    @Override
    public void update(double dt) {
        if (lifeTime > 0) {
            lifeTime -= dt;
        }
    }

    public boolean isExpired() {
        return lifeTime <= 0;
    }
}
