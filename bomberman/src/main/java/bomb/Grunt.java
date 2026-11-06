package bomb;

import javafx.scene.image.Image;

public class Grunt extends Enemy {
    private static final double SPEED = 60;
    private static final Image MOVING_IMAGE = new Image(
            Grunt.class.getResource("/animation/enemy1_animation/moving.gif")
                    .toExternalForm());
    private static final Image DEAD_IMAGE = new Image(
            Grunt.class.getResource("/animation/enemy1_animation/dead.png")
                    .toExternalForm());

    public Grunt(double x, double y) {
        super(x, y, SPEED);
        setAnimationImages(MOVING_IMAGE, DEAD_IMAGE);
    }

    @Override
    public Direction targetSearch(Player player) {
        return wander();
    }
}
