package bomb;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class Bomb extends Entity {
    private static final double ANIMATION_FRAME_TIME = 0.33;
    private static final Image[] ANIMATION_FRAMES = {
            loadFrame("frame1.png"),
            loadFrame("frame2.png"),
            loadFrame("frame3.png")
    };
    private static final double FUSE_TIME = 2.0;   // giây

    private double timer = FUSE_TIME;
    private double animationTimer;
    private int animationFrame;
    private final int blastRange;
    private boolean exploded = false;

    private static Image loadFrame(String name) {
        return new Image(Bomb.class.getResource("/animation/bomb_animation/" + name).toExternalForm());
    }

    public Bomb(double x, double y, int blastRange) {
        super(x, y);
        this.blastRange = blastRange;
        setImage(ANIMATION_FRAMES[0]);
    }

    @Override
    public void update(double dt) {
        if (!exploded) {
            timer -= dt;
            animationTimer += dt;
            while (animationTimer >= ANIMATION_FRAME_TIME) {
                animationTimer -= ANIMATION_FRAME_TIME;
                animationFrame = (animationFrame + 1) % ANIMATION_FRAMES.length;
                setImage(ANIMATION_FRAMES[animationFrame]);
            }
        }
    }

    public boolean isReadyToExplode() {
        return !exploded && timer <= 0;
    }

    public boolean isExploded() {
        return exploded;
    }

    public List<Flame> explode(GameMap map) {
        List<Flame> flames = new ArrayList<>();
        if (exploded) {
            return flames;
        }
        exploded = true;

        int r = getRow();
        int c = getCol();
        flames.add(newFlame(r, c, map));

        for (Direction d : Direction.values()) {
            for (int i = 1; i <= blastRange; i++) {
                int nr = r + d.dy * i;
                int nc = c + d.dx * i;

                if (!map.inBounds(nr, nc)) {
                    break;
                }
                Entity tile = map.getTile(nr, nc);

                if (tile instanceof Wall) {
                    break;
                }

                flames.add(newFlame(nr, nc, map));

                if (tile instanceof Brick brick) {
                    brick.onFlameHit();
                    if (brick.getDrop() == null) {
                        map.removeTile(nr, nc);
                    } else {
                        map.setTile(nr, nc, brick.getDrop());
                    }
                    break;
                }

                chainExplode(nr, nc, map, flames);
            }
        }
        return flames;
    }

    private void chainExplode(int r, int c, GameMap map, List<Flame> flames) {
        for (Bomb other : map.getBombs()) {
            if (other != this && !other.exploded && other.getRow() == r && other.getCol() == c) {
                flames.addAll(other.explode(map));
            }
        }
    }

    private Flame newFlame(int r, int c, GameMap map) {
        return new Flame(c * GameMap.TILE_SIZE, r * GameMap.TILE_SIZE, map);
    }
}
