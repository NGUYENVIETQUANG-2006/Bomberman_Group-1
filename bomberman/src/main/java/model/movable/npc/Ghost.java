package model.movable.npc;

import component.Enemy;
import core.input.Direction;
import model.movable.Player;

/** An enemy that wanders through walls and bricks, but still avoids bombs and map edges. */
public class Ghost extends Enemy {
    private static final double SPEED = 75;

    public Ghost(double x, double y) {
        super(x, y, SPEED);
    }

    @Override
    public Direction targetSearch(Player player) {
        return wander();
    }

    @Override
    protected boolean canEnter(Direction direction) {
        int row = getRow() + direction.dy;
        int column = getCol() + direction.dx;
        return map.inBounds(row, column) && !map.hasBombAt(row, column);
    }
}
