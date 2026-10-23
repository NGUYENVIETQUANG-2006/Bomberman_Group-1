package bomb;

import javafx.scene.canvas.GraphicsContext;

public class Player extends GameCharacter {
    private static final double BASE_SPEED = 200;
    private static final double MAX_SPEED = 240;
    private static final double SPEED_STEP = 20;

    private static final double BASE_COOLDOWN = 1.0;
    private static final double MIN_COOLDOWN = 0.3;
    private static final double COOLDOWN_STEP = 0.15;

    private static final int START_HEALTH = 3;
    private static final int MAX_HEALTH = 5;
    private static final double INVINCIBLE_TIME = 1.5;

    private static final int START_BLAST_RANGE = 1;
    private static final int MAX_BLAST_RANGE = 6;

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
            move(d.dx * speed * dt, d.dy * speed * dt, map);
        }

        if (control.wasPressed(keys.get(KeysBinding.Action.BOMB))) {
            Bomb bomb = placeBomb(map);
            if (bomb != null) {
                map.addBomb(bomb);
            }
        }
    }

    private Direction readDirection() {
        if (control.isHeld(keys.get(KeysBinding.Action.UP))) return Direction.UP;
        if (control.isHeld(keys.get(KeysBinding.Action.DOWN))) return Direction.DOWN;
        if (control.isHeld(keys.get(KeysBinding.Action.LEFT))) return Direction.LEFT;
        if (control.isHeld(keys.get(KeysBinding.Action.RIGHT))) return Direction.RIGHT;
        return null;
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

    public int getHealth()       { return health; }
    public int getScore()        { return score; }
    public void addScore(int p)  { score += p; }
    public int getBlastRange()   { return blastRange; }
    public boolean isInvincible() { return invincibleTimer > 0; }

    @Override
    public void render(GraphicsContext gc) {
        if (isInvincible() && ((int) (invincibleTimer * 10)) % 2 == 0) {
            return;
        }
        super.render(gc);
    }
}