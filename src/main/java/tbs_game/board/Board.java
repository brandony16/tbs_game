package tbs_game.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tbs_game.hexes.HexPos;

public class Board {

    private final int width;
    private final int height;
    private final Map<HexPos, Tile> tiles;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new HashMap<>();
        initializeTiles();
    }

    public Collection<HexPos> getPositions() {
        return tiles.keySet();
    }

    public Tile getTile(HexPos pos) {
        return tiles.get(pos);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isOnBoard(HexPos pos) {
        return tiles.get(pos) != null;
    }

    private void initializeTiles() {
        // Fill tile array with default terrain (PLAINS)
        // Replace with actualy generation at some point 
        int top = -height / 2;
        int bottom = height / 2;
        int left = -width / 2;
        int right = width / 2;
        for (int r = top; r <= bottom; r++) { // pointy top
            int r_offset = (int) Math.floor(r / 2.0); // or r>>1
            for (int q = left - r_offset; q <= right - r_offset; q++) {
                tiles.put(new HexPos(q, r), new Tile(Terrain.PLAINS));
            }
        }
    }
}
