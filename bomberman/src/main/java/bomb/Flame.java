package bomb;

public class Flame extends Entity {
    private static final double DURATION = 0.5;

    private final GameMap map;
    private double duration = DURATION;

    public Flame(double x, double y, GameMap map) {
        super(x, y);
        this.map = map;
    }

    @Override
    public void update(double dt) {
        duration -= dt;

        int r = getRow();
        int c = getCol();

        Player player = map.getPlayer();
        if (player != null && player.isAlive() && player.getRow() == r && player.getCol() == c) {
            player.onFlameHit();
        }

        for (Enemy enemy : map.getEnemies()) {
            if (enemy.isAlive() && enemy.getRow() == r && enemy.getCol() == c) {
                enemy.onFlameHit();
            }
        }
    }

    public boolean isExpired() {
        return duration <= 0;
    }
}