package tbs_game.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tbs_game.HexPos;

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
        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                tiles.put(new HexPos(q, r), new Tile(Terrain.PLAINS));
            }
        }
        // int q0 = -width / 2;
        // int r0 = -height / 2;

        // for (int q = q0; q < q0 + width; q++) {
        //     for (int r = r0; r < r0 + height; r++) {
        //         tiles.put(new HexPos(q, r), new Tile(Terrain.PLAINS));
        //     }
        // }
    }
}
