package tbs_game.board;

public class Board {

    private final int width;
    private final int height;
    private final Tile[] tiles;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width * height];
        initializeTiles();
    }

    public Tile getTile(int x, int y) {
        return tiles[y * width + x];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private void initializeTiles() {
        // Fill tile array with default terrain (PLAINS)
        // Replace with actualy generation at some point 
        for (int i = 0; i < this.tiles.length; i++) {
            this.tiles[i] = new Tile(Terrain.PLAINS);
        }
    }
}
