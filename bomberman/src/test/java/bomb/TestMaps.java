package bomb;

/** Tiện ích dựng bản đồ nhỏ cho các test: tường bao quanh, bên trong trống. */
final class TestMaps {
    private TestMaps() {
    }

    static GameMap walled(int rows, int cols) {
        GameMap map = new GameMap(rows, cols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || c == 0 || r == rows - 1 || c == cols - 1) {
                    map.setTile(r, c, new Wall(c * GameMap.TILE_SIZE, r * GameMap.TILE_SIZE));
                }
            }
        }
        return map;
    }

    static Player newPlayer(int row, int col) {
        return new Player(col * GameMap.TILE_SIZE, row * GameMap.TILE_SIZE,
                new InputHandler(), KeysBinding.arrows());
    }
}
