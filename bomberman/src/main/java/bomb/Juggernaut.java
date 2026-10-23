package bomb;

import java.util.ArrayDeque;
import java.util.Queue;

public class Juggernaut extends Enemy {
    private static final double SPEED = 100;
    private static final int DETECT_RANGE = 8;

    public Juggernaut(double x, double y) {
        super(x, y, SPEED);
    }

    @Override
    public Direction targetSearch(Player player) {
        if (player == null || !player.isAlive()) {
            return wander();
        }
        int sr = getRow();
        int sc = getCol();
        int tr = player.getRow();
        int tc = player.getCol();
        if (sr == tr && sc == tc) {
            return null;
        }

        Direction step = bfsFirstStep(sr, sc, tr, tc);
        return step != null ? step : wander();
    }

    private Direction bfsFirstStep(int sr, int sc, int tr, int tc) {
        int rows = map.getRows();
        int cols = map.getColumns();
        int[][] dist = new int[rows][cols];
        Direction[][] first = new Direction[rows][cols];
        for (int[] row : dist) {
            java.util.Arrays.fill(row, -1);
        }

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {sr, sc});
        dist[sr][sc] = 0;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0];
            int c = cur[1];
            if (dist[r][c] >= DETECT_RANGE) {
                continue;
            }
            for (Direction d : Direction.values()) {
                int nr = r + d.dy;
                int nc = c + d.dx;
                if (!map.inBounds(nr, nc) || dist[nr][nc] != -1) {
                    continue;
                }
                boolean isTarget = nr == tr && nc == tc;
                if (!isTarget && (map.isBlockedAt(nr, nc) || map.hasBombAt(nr, nc))) {
                    continue;
                }
                dist[nr][nc] = dist[r][c] + 1;
                first[nr][nc] = (r == sr && c == sc) ? d : first[r][c];
                if (isTarget) {
                    return first[nr][nc];
                }
                queue.add(new int[] {nr, nc});
            }
        }
        return null;
    }
}