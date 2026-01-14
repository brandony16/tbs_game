package tbs_game.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tbs_game.game.Game;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;

public class Board {

    private final Random random;

    private final int width;
    private final int height;
    private final Map<AxialPos, Tile> tiles;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new HashMap<>();

        this.random = new Random(Game.SEED);

        initializeTiles();
    }

    public Collection<AxialPos> getPositions() {
        return tiles.keySet();
    }

    public Tile getTile(AxialPos pos) {
        return tiles.get(pos);
    }

    public Tile putTile(AxialPos pos, Tile tile) {
        return tiles.put(pos, tile);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isOnBoard(AxialPos pos) {
        return tiles.get(pos) != null;
    }

    public ArrayList<AxialPos> getNeighbors(AxialPos pos) {
        ArrayList<AxialPos> neighbors = new ArrayList<>();

        for (AxialPos dir : AxialPos.directions) {
            AxialPos neighbor = pos.add(dir);
            if (isOnBoard(neighbor)) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    private void initializeTiles() {
        for (int row = 0; row < height; row++) { // pointy top
            for (int col = 0; col < width; col++) {
                Terrain type = Terrain.PLAINS;
                if (random.nextDouble() < 0.4) {
                    type = Terrain.WATER;
                }
                tiles.put(new OffsetPos(col, row).toAxial(), new Tile(type));
            }
        }

        // Smooth water out
        for (int i = 0; i < 2; i++) {
            for (Map.Entry<AxialPos, Tile> entry : tiles.entrySet()) {
                AxialPos pos = entry.getKey();
                Tile tile = entry.getValue();

                int waterNeighbors = countNeighbors(pos, Terrain.WATER);
                if (waterNeighbors >= 4) {
                    tile.setTerrain(Terrain.WATER);
                } else if (waterNeighbors < 2) {
                    tile.setTerrain(Terrain.PLAINS);
                }
            }
        }

        for (Map.Entry<AxialPos, Tile> entry : tiles.entrySet()) {
            AxialPos pos = entry.getKey();
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

        for (Map.Entry<AxialPos, Tile> entry : tiles.entrySet()) {
            AxialPos pos = entry.getKey();
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

    public boolean isPassable(AxialPos pos) {
        Tile tile = tiles.get(pos);
        return tile != null && tile.getTerrain().passable;
    }

    public int countNeighbors(AxialPos pos, Terrain type) {
        int count = 0;

        for (AxialPos direction : AxialPos.directions) {
            AxialPos neighbor = pos.add(direction);
            if (!isOnBoard(neighbor)) {
                continue;
            }

            if (tiles.get(neighbor).getTerrain() == type) {
                count++;
            }
        }

        return count;
    }

    public void makeAllPlains() {
        for (Map.Entry<AxialPos, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            tile.setTerrain(Terrain.PLAINS);
        }
    }

    public void makeAllForest() {
        for (Map.Entry<AxialPos, Tile> entry : tiles.entrySet()) {
            Tile tile = entry.getValue();
            tile.setTerrain(Terrain.FOREST);
        }
    }

    public void createDebugMap() {
        makeAllPlains();
        tiles.get(new AxialPos(1, 0)).setTerrain(Terrain.PLAINS);
        tiles.get(new AxialPos(1, -1)).setTerrain(Terrain.FOREST);
        tiles.get(new AxialPos(0, -1)).setTerrain(Terrain.WATER);
        tiles.get(new AxialPos(-1, 0)).setTerrain(Terrain.MOUNTAIN);
    }
}
