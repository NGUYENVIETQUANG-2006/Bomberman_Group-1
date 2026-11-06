package bomb;

import javafx.scene.image.Image;

public class Flame extends Entity {
    private static final double DURATION = 0.5;
    private static final Image CORE = loadImage("lv1_core.png");
    private static final Image UP = loadImage("lv1_up.png");
    private static final Image DOWN = loadImage("lv1_down.png");
    private static final Image LEFT = loadImage("lv1_left.png");
    private static final Image RIGHT = loadImage("lv1_right.png");
    private static final Image LV2_CORE = loadImage("lv2_core.png");
    private static final Image LV2_MID_HORIZONTAL = loadImage("lv2_mid_horizontal.png");
    private static final Image LV2_MID_VERTICAL = loadImage("lv2_mid_vertical.png");
    private static final Image LV2_OUT_UP = loadImage("lv2_out_up.png");
    private static final Image LV2_OUT_DOWN = loadImage("lv2_out_down.png");
    private static final Image LV2_OUT_LEFT = loadImage("lv2_out_left.png");
    private static final Image LV2_OUT_RIGHT = loadImage("lv2_out_right.png");
    private static final Image LV3_CORE = loadImage("lv3_core.png");
    private static final Image LV3_MID_HORIZONTAL = loadImage("lv3_mid_horizontal.png");
    private static final Image LV3_MID_VERTICAL = loadImage("lv3_mid_vertical.png");
    private static final Image LV3_OUT_UP = loadImage("lv3_out_up.png");
    private static final Image LV3_OUT_DOWN = loadImage("lv3_out_down.png");
    private static final Image LV3_OUT_LEFT = loadImage("lv3_out_left.png");
    private static final Image LV3_OUT_RIGHT = loadImage("lv3_out_right.png");

    private final GameMap map;
    private double duration = DURATION;

    public Flame(double x, double y, GameMap map, Image image) {
        super(x, y);
        this.map = map;
        setImage(image);
    }

    public static Image coreImage(int blastRange) {
        return switch (blastRange) {
            case 2 -> LV2_CORE;
            case 3 -> LV3_CORE;
            default -> CORE;
        };
    }

    public static Image imageFor(Direction direction, int blastRange, boolean endpoint) {
        if (blastRange == 2 || blastRange == 3) {
            Image midHorizontal = blastRange == 3 ? LV3_MID_HORIZONTAL : LV2_MID_HORIZONTAL;
            Image midVertical = blastRange == 3 ? LV3_MID_VERTICAL : LV2_MID_VERTICAL;
            if (!endpoint) {
                return direction == Direction.LEFT || direction == Direction.RIGHT
                        ? midHorizontal : midVertical;
            }
            return switch (direction) {
                case UP -> blastRange == 3 ? LV3_OUT_UP : LV2_OUT_UP;
                case DOWN -> blastRange == 3 ? LV3_OUT_DOWN : LV2_OUT_DOWN;
                case LEFT -> blastRange == 3 ? LV3_OUT_LEFT : LV2_OUT_LEFT;
                case RIGHT -> blastRange == 3 ? LV3_OUT_RIGHT : LV2_OUT_RIGHT;
            };
        }
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case LEFT -> LEFT;
            case RIGHT -> RIGHT;
        };
    }

    private static Image loadImage(String name) {
        return new Image(Flame.class.getResource("/picture/Blast/" + name).toExternalForm());
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
