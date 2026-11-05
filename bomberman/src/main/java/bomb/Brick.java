package bomb;

import javafx.scene.image.Image;

public class Brick extends Entity {
    private static final Image BRICK_IMAGE = new Image(Brick.class.getResource(
            "/picture/Brick/PalmOS - Bomberman for Palm OS - Miscellaneous - Bomberman, Enemies, Bombs, Items, & Blocks_03.png")
            .toExternalForm());

    private boolean destroyed = false; 
    private final Item drop;

    public Brick(double x, double y) {
        this(x, y, null);
    }

    public Brick(double x, double y, Item drop) {
        super(x, y);
        this.drop = drop;
        setImage(BRICK_IMAGE);
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
