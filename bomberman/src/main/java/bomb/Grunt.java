package bomb;

public class Grunt extends Enemy {
    private static final double SPEED = 60;

    public Grunt(double x, double y) {
        super(x, y, SPEED);
    }

    @Override
    public Direction targetSearch(Player player) {
        return wander();
    }
}