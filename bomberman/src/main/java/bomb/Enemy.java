package bomb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Enemy extends GameCharacter {
    private static final double TOUCH_DISTANCE = GameMap.TILE_SIZE * 0.7;
    private static final double WANDER_KEEP_STRAIGHT = 0.7;

    protected final Random random = new Random();

    private boolean moving = false;
    private double stepTarget = 0;

    protected Enemy(double x, double y, double speed) {
        super(x, y, speed, speed);
    }

    public abstract Direction targetSearch(Player player);

    @Override
    public void update(double dt) {
        if (!isAlive() || map == null) {
            return;
        }

        double remaining = speed * dt;
        while (remaining > 0) {
            if (!moving) {
                Direction d = targetSearch(map.getPlayer());
                if (d == null || !canEnter(d)) {
                    break;
                }
                direction = d;
                moving = true;
                stepTarget = GameMap.TILE_SIZE;
            }

            double step = Math.min(remaining, stepTarget);
            x += direction.dx * step;
            y += direction.dy * step;
            stepTarget -= step;
            remaining -= step;

            if (stepTarget <= 0) {
                snapToGrid();
                moving = false;
            }
        }
        killPlayerIfTouching();
    }

    private void snapToGrid() {
        x = Math.round(x / GameMap.TILE_SIZE) * (double) GameMap.TILE_SIZE;
        y = Math.round(y / GameMap.TILE_SIZE) * (double) GameMap.TILE_SIZE;
    }

    private void killPlayerIfTouching() {
        Player p = map.getPlayer();
        if (p != null && p.isAlive()
                && Math.abs(x - p.getX()) < TOUCH_DISTANCE
                && Math.abs(y - p.getY()) < TOUCH_DISTANCE) {
            p.kill();
        }
    }

    protected boolean canEnter(Direction d) {
        int r = getRow() + d.dy;
        int c = getCol() + d.dx;
        return !map.isBlockedAt(r, c) && !map.hasBombAt(r, c);
    }

    protected static Direction opposite(Direction d) {
        return switch (d) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    protected Direction wander() {
        List<Direction> options = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d != opposite(direction) && canEnter(d)) {
                options.add(d);
            }
        }
        if (options.isEmpty()) {
            return canEnter(opposite(direction)) ? opposite(direction) : null;
        }
        if (options.contains(direction) && random.nextDouble() < WANDER_KEEP_STRAIGHT) {
            return direction;
        }
        return options.get(random.nextInt(options.size()));
    }
}
