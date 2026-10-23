package bomb;

public abstract class GameCharacter extends Entity {
    private static final double MARGIN = GameMap.TILE_SIZE * 0.15;
    private static final double EPS = 1e-6;

    protected double speed;
    protected Direction direction = Direction.DOWN;
    protected GameMap map;

    private boolean alive = true;
    private final double maxSpeed;

    protected GameCharacter(double x, double y, double speed, double maxSpeed) {
        super(x, y);
        this.speed = speed;
        this.maxSpeed = maxSpeed;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public void move(double dx, double dy, GameMap map) {
        if (!collisionDetect(x + dx, y, map)) {
            x += dx;
        }
        if (!collisionDetect(x, y + dy, map)) {
            y += dy;
        }
    }

    public boolean collisionDetect(double nx, double ny, GameMap map) {
        double left = nx + MARGIN;
        double right = nx + GameMap.TILE_SIZE - MARGIN - EPS;
        double top = ny + MARGIN;
        double bottom = ny + GameMap.TILE_SIZE - MARGIN - EPS;

        int c1 = (int) Math.floor(left / GameMap.TILE_SIZE);
        int c2 = (int) Math.floor(right / GameMap.TILE_SIZE);
        int r1 = (int) Math.floor(top / GameMap.TILE_SIZE);
        int r2 = (int) Math.floor(bottom / GameMap.TILE_SIZE);

        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                if (map.isBlockedAt(r, c)) {
                    return true;
                }
            }
        }

        for (Bomb b : map.getBombs()) {
            boolean willOverlap = overlapsCell(nx, ny, b.getRow(), b.getCol());
            boolean overlapsNow = overlapsCell(x, y, b.getRow(), b.getCol());
            if (willOverlap && !overlapsNow) {
                return true;
            }
        }
        return false;
    }

    private boolean overlapsCell(double px, double py, int r, int c) {
        double cellL = c * GameMap.TILE_SIZE;
        double cellT = r * GameMap.TILE_SIZE;
        return px + MARGIN < cellL + GameMap.TILE_SIZE
                && px + GameMap.TILE_SIZE - MARGIN > cellL
                && py + MARGIN < cellT + GameMap.TILE_SIZE
                && py + GameMap.TILE_SIZE - MARGIN > cellT;
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public void onFlameHit() {
        kill();
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public Direction getDirection() {
        return direction;
    }
}
