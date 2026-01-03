package tbs_game.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tbs_game.game.Game;
import tbs_game.hexes.HexPos;

public class Board {

    private final Random random;

    private final int width;
    private final int height;
    private final Map<HexPos, Tile> tiles;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new HashMap<>();

        this.random = new Random(Game.SEED);

        initializeTiles();
    }

    public Collection<HexPos> getPositions() {
        return tiles.keySet();
    }

    public Tile getTile(HexPos pos) {
        return tiles.get(pos);
    }

    public Tile putTile(HexPos pos, Tile tile) {
        return tiles.put(pos, tile);
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
        int top = -height / 2;
        int bottom = height / 2;
        int left = -width / 2;
        int right = width / 2;
        for (int r = top; r <= bottom; r++) { // pointy top
            int r_offset = (int) Math.floor(r / 2.0); // or r>>1
            for (int q = left - r_offset; q <= right - r_offset; q++) {
                Terrain type = Terrain.PLAINS;
                if (random.nextDouble() < 0.4) {
                    type = Terrain.WATER;
                }
                tiles.put(new HexPos(q, r), new Tile(type));
            }
        }

        // Smooth water out
        for (int i = 0; i < 2; i++) {
            for (Map.Entry<HexPos, Tile> entry : tiles.entrySet()) {
                HexPos pos = entry.getKey();
                Tile tile = entry.getValue();

                int waterNeighbors = countNeighbors(pos, Terrain.WATER);
                if (waterNeighbors >= 4) {
                    tile.setTerrain(Terrain.WATER);
                } else if (waterNeighbors < 2) {
                    tile.setTerrain(Terrain.PLAINS);
                }
            }
        }

        for (Map.Entry<HexPos, Tile> entry : tiles.entrySet()) {
            HexPos pos = entry.getKey();
            Tile tile = entry.getValue();

            if (tile.getTerrain() == Terrain.PLAINS) {
                double baseChance = 0.15;
                int forestNeighbors = countNeighbors(pos, Terrain.FOREST);

                double forestChance = baseChance + forestNeighbors * 0.1;
                if (random.nextDouble() < forestChance) {
                    tile.setTerrain(Terrain.FOREST);
                }
            }
        }

        for (Map.Entry<HexPos, Tile> entry : tiles.entrySet()) {
            HexPos pos = entry.getKey();
            Tile tile = entry.getValue();

            if (tile.getTerrain() == Terrain.PLAINS) {
                double baseChance = 0.1;
                int mountainNeighbors = countNeighbors(pos, Terrain.MOUNTAIN);
                int waterNeighbors = countNeighbors(pos, Terrain.WATER);

                double mountainChance = baseChance + mountainNeighbors * 0.15 - waterNeighbors * 0.1;
                if (random.nextDouble() < mountainChance) {
                    tile.setTerrain(Terrain.MOUNTAIN);
                }
            }
        }

    }

    public boolean isPassable(HexPos pos) {
        Tile tile = tiles.get(pos);
        return tile != null && tile.getTerrain().passable;
    }

    public int countNeighbors(HexPos pos, Terrain type) {
        int count = 0;

        for (HexPos direction : HexPos.directions) {
            HexPos neighbor = pos.add(direction);
            if (!isOnBoard(neighbor)) {
                continue;
            }

            if (tiles.get(neighbor).getTerrain() == type) {
                count++;
            }
        }

        return count;
    }
}
