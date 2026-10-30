package bomb;

import javafx.scene.canvas.GraphicsContext;

public class Player extends GameCharacter {
    private static final double BASE_SPEED = 120;
    private static final double MAX_SPEED = 200;
    private static final double SPEED_STEP = 20;

    private static final double BASE_COOLDOWN = 1.0;
    private static final double MIN_COOLDOWN = 0.3;
    private static final double COOLDOWN_STEP = 0.15;

    private static final int START_HEALTH = 3;
    private static final int MAX_HEALTH = 5;
    private static final double INVINCIBLE_TIME = 1.5;

    private static final int START_BLAST_RANGE = 1;
    private static final int MAX_BLAST_RANGE = 3;

    private int health = START_HEALTH;
    private int score = 0;
    private double bombCooldown = BASE_COOLDOWN;
    private double invincibleTimer = 0;
    private final int maxHealth = MAX_HEALTH;
    private int blastRange = START_BLAST_RANGE;
    private final int maxBlastRange = MAX_BLAST_RANGE;
    private double cooldownTimer = 0;
    private final InputHandler control;
    private final KeysBinding keys;

    public Player(double x, double y, InputHandler control, KeysBinding keys) {
        super(x, y, BASE_SPEED, MAX_SPEED);
        this.control = control;
        this.keys = keys;
    }

    @Override
    public void update(double dt) {
        if (!isAlive() || map == null) {
            return;
        }

        if (cooldownTimer > 0) {
            cooldownTimer -= dt;
        }
        if (invincibleTimer > 0) {
            invincibleTimer -= dt;
        }

        Direction d = readDirection();
        if (d != null) {
            direction = d;
            if (hasClearCenterPath(d)) {
                moveGridAligned(d, dt);
            } else {
                clearBlockedDirection(d);
            }
        }

        if (control.wasPressed(keys.get(KeysBinding.Action.BOMB))) {
            Bomb bomb = placeBomb(map);
            if (bomb != null) {
                map.addBomb(bomb);
            }
        }
    }

    private boolean hasClearCenterPath(Direction d) {
        int currentRow = cellAlongMovementAxis(y, d.dy, getRow());
        int currentCol = cellAlongMovementAxis(x, d.dx, getCol());
        int nextRow = currentRow + d.dy;
        int nextCol = currentCol + d.dx;
        return !map.isBlockedAt(nextRow, nextCol);
    }

    private int cellAlongMovementAxis(double position, int movement, int fallback) {
        double cellPosition = position / GameMap.TILE_SIZE;
        if (movement > 0) {
            return (int) Math.floor(cellPosition);
        }
        if (movement < 0) {
            return (int) Math.ceil(cellPosition);
        }
        return fallback;
    }

    void moveGridAligned(Direction d, double dt) {
        double distance = Math.max(0, speed * dt);
        if (d.dx != 0) {
            double targetY = Math.round(y / GameMap.TILE_SIZE) * GameMap.TILE_SIZE;
            double correction = targetY - y;
            double step = Math.min(Math.abs(correction), distance);
            if (step > 0) {
                double previousY = y;
                move(0, Math.copySign(step, correction), map);
                if (Math.abs(y - previousY) < 1e-6) {
                    clearBlockedDirection(d);
                    return;
                }
                distance -= step;
            }
            if (Math.abs(y - targetY) < 1e-6 && distance > 0) {
                double previousX = x;
                move(d.dx * distance, 0, map);
                if (Math.abs(x - previousX) < 1e-6) {
                    clearBlockedDirection(d);
                }
            } else if (Math.abs(y - targetY) >= 1e-6 && step == 0) {
                clearBlockedDirection(d);
            }
        } else {
            double targetX = Math.round(x / GameMap.TILE_SIZE) * GameMap.TILE_SIZE;
            double correction = targetX - x;
            double step = Math.min(Math.abs(correction), distance);
            if (step > 0) {
                double previousX = x;
                move(Math.copySign(step, correction), 0, map);
                if (Math.abs(x - previousX) < 1e-6) {
                    clearBlockedDirection(d);
                    return;
                }
                distance -= step;
            }
            if (Math.abs(x - targetX) < 1e-6 && distance > 0) {
                double previousY = y;
                move(0, d.dy * distance, map);
                if (Math.abs(y - previousY) < 1e-6) {
                    clearBlockedDirection(d);
                }
            } else if (Math.abs(x - targetX) >= 1e-6 && step == 0) {
                clearBlockedDirection(d);
            }
        }
    }

    private void clearBlockedDirection(Direction d) {
        direction = null;
    }

    private Direction readDirection() {
        for (javafx.scene.input.KeyCode key : control.getHeldKeysNewestFirst()) {
            Direction candidate = directionForAction(keys.actionFor(key));
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    private Direction directionForAction(KeysBinding.Action action) {
        if (action == null) {
            return null;
        }
        return switch (action) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            case BOMB -> null;
        };
    }

    @Override
    protected boolean canMoveThroughBombs() {
        return true;
    }

    public boolean canPlaceBomb() {
        return isAlive() && cooldownTimer <= 0;
    }

    public Bomb placeBomb(GameMap map) {
        int r = getRow();
        int c = getCol();
        if (!canPlaceBomb() || map.hasBombAt(r, c)) {
            return null;
        }
        cooldownTimer = bombCooldown;
        return new Bomb(c * GameMap.TILE_SIZE, r * GameMap.TILE_SIZE, blastRange);
    }

    @Override
    public void kill() {
        if (!isAlive() || invincibleTimer > 0) {
            return;
        }
        health--;
        if (health <= 0) {
            super.kill();
        } else {
            invincibleTimer = INVINCIBLE_TIME;
        }
    }

    public void addHealth() {
        health = Math.min(maxHealth, health + 1);
    }

    public void increaseRange() {
        blastRange = Math.min(maxBlastRange, blastRange + 1);
    }

    public void increaseSpeed() {
        speed = Math.min(getMaxSpeed(), speed + SPEED_STEP);
    }

    public void reduceCooldown() {
        bombCooldown = Math.max(MIN_COOLDOWN, bombCooldown - COOLDOWN_STEP);
    }

    public int getHealth() { return health; }
    public int getScore() { return score; }
    public void addScore(int p) { score += p; }
    public int getBlastRange() { return blastRange; }
    public boolean isInvincible() { return invincibleTimer > 0; }

    public double getSpeed() { return speed; }
    public double getBombCooldown() { return bombCooldown; }

    public void restoreState(int health, int score, double speed, int blastRange, double bombCooldown) {
        this.health = Math.max(1, Math.min(maxHealth, health));
        this.score = Math.max(0, score);
        this.speed = Math.max(BASE_SPEED, Math.min(getMaxSpeed(), speed));
        this.blastRange = Math.max(START_BLAST_RANGE, Math.min(maxBlastRange, blastRange));
        this.bombCooldown = Math.max(MIN_COOLDOWN, Math.min(BASE_COOLDOWN, bombCooldown));
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isInvincible() && ((int) (invincibleTimer * 10)) % 2 == 0) {
            return;
        }
        super.render(gc);
    }
}
