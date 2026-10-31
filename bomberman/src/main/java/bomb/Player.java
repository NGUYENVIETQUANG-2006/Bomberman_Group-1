package bomb;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

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
    private static final double WALK_FRAME_TIME = 0.15;
    private static final long DEAD_FRAME_TIME_NANOS = 150_000_000L;

    private static final Image LOOKING_FRONT = loadImage("looking_front.png");
    private static final Image LOOKING_UP = loadImage("Looking_up.png");
    private static final Image LOOKING_LEFT = loadImage("Looking_left.png");
    private static final Image LOOKING_RIGHT = loadImage("Looking_right.png");
    private static final Image WALKING_UP_LEFT = loadImage("walkingup_leftfoot.png");
    private static final Image WALKING_UP_RIGHT = loadImage("walkingup_righfoot.png");
    private static final Image WALKING_DOWN_LEFT = loadImage("walkingdown_leftfoot.png");
    private static final Image WALKING_DOWN_RIGHT = loadImage("walkingdown_rightfoot.png");
    private static final Image WALKING_LEFT_LEFT = loadImage("walking_left_leftfoot.png");
    private static final Image WALKING_LEFT_RIGHT = loadImage("walking_left_rightfoot.png");
    private static final Image WALKING_RIGHT_LEFT = loadImage("walking_right_leftfoot.png");
    private static final Image WALKING_RIGHT_RIGHT = loadImage("walking_right_rightfoot.png");
    private static final Image DEAD_FRAME_1 = loadImage("dead_frame1.png");
    private static final Image DEAD_FRAME_2 = loadImage("dead_frame2.png");
    private static final Image DEAD_FRAME_3 = loadImage("dead_frame3.png");

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
    private Direction facing = Direction.DOWN;
    private Direction walkingDirection;
    private boolean walking;
    private boolean rightFoot;
    private double walkFrameTimer;
    private long deathStartedNanos;

    private static Image loadImage(String name) {
        return new Image(Player.class.getResource("/picture/bomber/" + name).toExternalForm());
    }

    public Player(double x, double y, InputHandler control, KeysBinding keys) {
        super(x, y, BASE_SPEED, MAX_SPEED);
        this.control = control;
        this.keys = keys;
        setImage(LOOKING_FRONT);
    }

    @Override
    public void update(double dt) {
        if (!isAlive()) {
            return;
        }
        if (map == null) {
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
            facing = d;
            if (hasClearCenterPath(d)) {
                double previousX = x;
                double previousY = y;
                moveGridAligned(d, dt);
                if (Math.abs(x - previousX) > 1e-6 || Math.abs(y - previousY) > 1e-6) {
                    updateWalkingAnimation(d, dt);
                } else {
                    stopWalking();
                }
            } else {
                stopWalking();
                setImage(standingImage(d));
            }
        } else {
            stopWalking();
            setImage(standingImage(facing));
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

    private void updateWalkingAnimation(Direction d, double dt) {
        if (!walking || walkingDirection != d) {
            walking = true;
            walkingDirection = d;
            rightFoot = false;
            walkFrameTimer = 0;
        }
        walkFrameTimer += dt;
        while (walkFrameTimer >= WALK_FRAME_TIME) {
            walkFrameTimer -= WALK_FRAME_TIME;
            rightFoot = !rightFoot;
        }
        setImage(walkingImage(d, rightFoot));
    }

    private void stopWalking() {
        walking = false;
        walkingDirection = null;
        walkFrameTimer = 0;
    }

    private Image standingImage(Direction d) {
        return switch (d) {
            case UP -> LOOKING_UP;
            case DOWN -> LOOKING_FRONT;
            case LEFT -> LOOKING_LEFT;
            case RIGHT -> LOOKING_RIGHT;
        };
    }

    private Image walkingImage(Direction d, boolean useRightFoot) {
        return switch (d) {
            case UP -> useRightFoot ? WALKING_UP_RIGHT : WALKING_UP_LEFT;
            case DOWN -> useRightFoot ? WALKING_DOWN_RIGHT : WALKING_DOWN_LEFT;
            case LEFT -> useRightFoot ? WALKING_LEFT_RIGHT : WALKING_LEFT_LEFT;
            case RIGHT -> useRightFoot ? WALKING_RIGHT_RIGHT : WALKING_RIGHT_LEFT;
        };
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
            deathStartedNanos = System.nanoTime();
            setImage(DEAD_FRAME_1);
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
        if (!isAlive()) {
            long elapsed = Math.max(0, System.nanoTime() - deathStartedNanos);
            int frame = (int) Math.min(2, elapsed / DEAD_FRAME_TIME_NANOS);
            setImage(switch (frame) {
                case 0 -> DEAD_FRAME_1;
                case 1 -> DEAD_FRAME_2;
                default -> DEAD_FRAME_3;
            });
            drawPlayerImage(gc);
            return;
        }
        if (isInvincible() && ((int) (invincibleTimer * 10)) % 2 == 0) {
            return;
        }
        drawPlayerImage(gc);
    }

    private void drawPlayerImage(GraphicsContext gc) {
        double tileSize = GameMap.TILE_SIZE;
        double scale = Math.min(tileSize / img.getWidth(), tileSize / img.getHeight());
        double width = img.getWidth() * scale;
        double height = img.getHeight() * scale;
        gc.drawImage(img, x + (tileSize - width) / 2, y + (tileSize - height) / 2, width, height);
    }
}
