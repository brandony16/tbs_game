package game_tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.board.Tile;
import tbs_game.game.Game;
import tbs_game.game.SetupHandler;
import tbs_game.hexes.HexPos;

class SetupTest {

    private Board board;
    private Game game;

    @BeforeEach
    void setUp() {
        game = Game.allPlains(7, 7, 3);
        board = game.getBoard();
    }

    @Test
    void findSpawnableHexes_onlyReturnsPassableTiles() {
        // Make some tiles impassable
        HexPos water = new HexPos(0, 0);
        board.putTile(water, new Tile(Terrain.WATER));

        ArrayList<HexPos> spawnable = SetupHandler.findSpawnableHexes(board);

        assertFalse(spawnable.contains(water));
        assertTrue(spawnable.size() < board.getPositions().size());
    }

    @Test
    void generateSpawnSpots_returnsCorrectNumberOfSpawns() {
        ArrayList<HexPos> spawns = SetupHandler.generateSpawnSpots(game, 1234);

        assertEquals(game.getNumPlayers(), spawns.size());
    }

    @Test
    void generateSpawnSpots_respectsMinimumSpawnDistance() {
        ArrayList<HexPos> spawns = SetupHandler.generateSpawnSpots(game, 5678);

        for (int i = 0; i < spawns.size(); i++) {
            for (int j = i + 1; j < spawns.size(); j++) {
                int dist = spawns.get(i).distanceTo(spawns.get(j));
                assertTrue(
                        dist >= SetupHandler.MIN_SPAWN_DIST,
                        "Spawn distance violated: " + dist
                );
            }
        }
    }

    @Test
    void generateSpawnSpots_hasNoDuplicatePositions() {
        ArrayList<HexPos> spawns = SetupHandler.generateSpawnSpots(game, 42);

        Set<HexPos> unique = new HashSet<>(spawns);
        assertEquals(spawns.size(), unique.size());
    }

    @Test
    void generateSpawnSpots_isDeterministicForSameSeed() {
        ArrayList<HexPos> first = SetupHandler.generateSpawnSpots(game, 999);
        ArrayList<HexPos> second = SetupHandler.generateSpawnSpots(game, 999);

        assertEquals(first, second);
    }
}
