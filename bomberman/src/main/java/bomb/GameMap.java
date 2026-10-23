package bomb;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    public static final int TILE_SIZE = 48;

    private final int rows;
    private final int columns;

    private final Entity[][] tiles;

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Flame> flames = new ArrayList<>();

    private int playerSpawnRow;
    private int playerSpawnCol;

    private Player player;

    public GameMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.tiles = new Entity[rows][columns];
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < columns;
    }

    public Entity getTile(int r, int c) {
        return inBounds(r, c) ? tiles[r][c] : null;
    }

    public boolean isBlockedAt(int r, int c) {
        if (!inBounds(r, c)) return true;
        Entity t = tiles[r][c];
        return t instanceof Wall || t instanceof Brick;
    }

    public boolean hasBombAt(int r, int c) {
        for (Bomb b : bombs) {
            int br = (int) (b.getY() / TILE_SIZE);
            int bc = (int) (b.getX() / TILE_SIZE);
            if (br == r && bc == c) return true;
        }
        return false;
    }

    public void setTile(int r, int c, Entity e) {
        if (inBounds(r, c)) tiles[r][c] = e;
    }

    public void removeTile(int r, int c) {
        if (inBounds(r, c)) tiles[r][c] = null;
    }

    public void addBomb(Bomb b) { bombs.add(b); }
    public void addEnemy(Enemy e) { 
        e.setMap(this);
        enemies.add(e); 
    }
    public void addFlame(Flame f) { flames.add(f); }

    public List<Bomb> getBombs() { return bombs; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Flame> getFlames() { return flames; }

    public void setPlayerSpawn(int r, int c) {
        this.playerSpawnRow = r;
        this.playerSpawnCol = c;
    }

    public void setPlayer(Player p) {
        this.player = p;
        p.setMap(this);
    }
    public Player getPlayer() { return player; }

    public int getPlayerSpawnRow() { return playerSpawnRow; }
    public int getPlayerSpawnCol() { return playerSpawnCol; }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
}
