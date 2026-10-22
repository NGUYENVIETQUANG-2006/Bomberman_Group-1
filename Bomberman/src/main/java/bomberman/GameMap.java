package bomberman;

import java.util.ArrayList;
import java.util.List;

/**
 * Dữ liệu của một màn chơi: lưới ô tĩnh + các đối tượng động.
 * Toạ độ pixel của một ô (r, c) là (c * TILE_SIZE, r * TILE_SIZE).
 */
public class GameMap {
    public static final int TILE_SIZE = 48;

    private final int rows;
    private final int columns;

    /** Wall, Brick, Portal, Item. null nghĩa là ô trống (cỏ). */
    private final Entity[][] tiles;

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Flame> flames = new ArrayList<>();

    private int playerSpawnRow;
    private int playerSpawnCol;

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.tiles = new Entity[rows][columns];
    }

    // ---------- Truy vấn lưới ----------
    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < columns;
    }

    public Entity getTile(int r, int c) {
        return inBounds(r, c) ? tiles[r][c] : null;
    }

    /** Ô có chặn người/kẻ địch đi qua không (tường, gạch, hoặc ngoài bản đồ). */
    public boolean isBlockedAt(int r, int c) {
        if (!inBounds(r, c)) return true;
        Entity t = tiles[r][c];
        return t instanceof Wall || t instanceof Brick;
    }

    /** Ô này đang có bom không. Bom được đặt căn đúng vào ô nên chia cho TILE_SIZE là ra hàng/cột. */
    public boolean hasBombAt(int r, int c) {
        for (Bomb b : bombs) {
            int br = (int) (b.getY() / TILE_SIZE);
            int bc = (int) (b.getX() / TILE_SIZE);
            if (br == r && bc == c) return true;
        }
        return false;
    }

    // ---------- Thay đổi lưới ----------
    public void setTile(int r, int c, Entity e) {
        if (inBounds(r, c)) tiles[r][c] = e;
    }

    /** Dùng khi gạch bị phá hoặc vật phẩm được nhặt. */
    public void removeTile(int r, int c) {
        if (inBounds(r, c)) tiles[r][c] = null;
    }

    // ---------- Đối tượng động ----------
    public void addBomb(Bomb b)       { bombs.add(b); }
    public void addEnemy(Enemy e)     { enemies.add(e); }
    public void addFlame(Flame f)     { flames.add(f); }

    public List<Bomb> getBombs()      { return bombs; }
    public List<Enemy> getEnemies()   { return enemies; }
    public List<Flame> getFlames()    { return flames; }

    // ---------- Vị trí xuất phát của Player ----------
    public void setPlayerSpawn(int r, int c) {
        this.playerSpawnRow = r;
        this.playerSpawnCol = c;
    }

    public int getPlayerSpawnRow() { return playerSpawnRow; }
    public int getPlayerSpawnCol() { return playerSpawnCol; }

    public int getRows()    { return rows; }
    public int getColumns() { return columns; }
}