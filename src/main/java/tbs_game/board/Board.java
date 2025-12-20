package tbs_game.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tbs_game.HexPos;

public class Board {

    private final int radius;
    private final Map<HexPos, Tile> tiles;

    public Board(int radius) {
        this.radius = radius;
        this.tiles = new HashMap<>();
        initializeTiles();
    }

    public Collection<HexPos> getPositions() {
        return tiles.keySet();
    }

    public Tile getTile(HexPos pos) {
        return tiles.get(pos);
    }

    public int getRadius() {
        return radius;
    }

    private void initializeTiles() {
        // Fill tile array with default terrain (PLAINS)
        // Replace with actualy generation at some point 
        for (int q = -radius; q <= radius; q++) {
            for (int r = -radius; r <= radius; r++) {
                int s = -q - r; // "3rd" axis of hex
                if (Math.abs(s) <= radius) {
                    tiles.put(new HexPos(q, r), new Tile(Terrain.PLAINS));
                }
            }
        }
    }
}
