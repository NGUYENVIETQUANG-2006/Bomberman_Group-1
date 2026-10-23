package bomb;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {
    protected double x;
    protected double y;
    protected Image img;

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update(double dt);

    public void render(GraphicsContext gc) {
        if (img != null) {
            gc.drawImage(img, x, y, GameMap.TILE_SIZE, GameMap.TILE_SIZE);
        }
    }

    public void onFlameHit() {}

    public double getX() { return x; }
    public double getY() { return y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getCol() {
        return (int) ((x + GameMap.TILE_SIZE / 2.0) / GameMap.TILE_SIZE);
    }

    public int getRow() {
        return (int) ((y + GameMap.TILE_SIZE / 2.0) / GameMap.TILE_SIZE);
    }

    public void setImage(Image img) {
        this.img = img;
    }
}