package bomberman;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Lớp gốc của mọi đối tượng trong game (tường, gạch, vật phẩm, bom, lửa, nhân vật).
 * Toạ độ (x, y) là góc trên bên trái của đối tượng, tính bằng pixel.
 */
public abstract class Entity {
    protected double x;
    protected double y;
    protected Image img;

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Cập nhật trạng thái mỗi frame. dt là số giây kể từ frame trước. */
    public abstract void update(double dt);

    /** Vẽ đối tượng. Lớp con có animation thì override. */
    public void render(GraphicsContext gc) {
        if (img != null) {
            gc.drawImage(img, x, y, GameMap.TILE_SIZE, GameMap.TILE_SIZE);
        }
    }

    /** Được gọi khi lửa chạm vào đối tượng. Mặc định không làm gì. */
    public void onFlameHit() {
    }

    // ---------- Vị trí ----------
    public double getX() { return x; }
    public double getY() { return y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Cột trên lưới, tính theo tâm của đối tượng. */
    public int getCol() {
        return (int) ((x + GameMap.TILE_SIZE / 2.0) / GameMap.TILE_SIZE);
    }

    /** Hàng trên lưới, tính theo tâm của đối tượng. */
    public int getRow() {
        return (int) ((y + GameMap.TILE_SIZE / 2.0) / GameMap.TILE_SIZE);
    }

    public void setImage(Image img) {
        this.img = img;
    }
}