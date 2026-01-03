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
    private SetupHandler setup;

    @BeforeEach
    void setUp() {
        game = new Game(7, 7, 3); // 3 players
        board = game.getBoard();

        // Create a simple 7x7 land grid
        for (int q = -3; q <= 3; q++) {
            for (int r = -3; r <= 3; r++) {
                HexPos pos = new HexPos(q, r);
                board.putTile(pos, new Tile(Terrain.PLAINS));
            }
        }

        setup = new SetupHandler();
    }

    @Test
    void findSpawnableHexes_onlyReturnsPassableTiles() {
        // Make some tiles impassable
        HexPos water = new HexPos(0, 0);
        board.putTile(water, new Tile(Terrain.WATER));

        ArrayList<HexPos> spawnable = setup.findSpawnableHexes(board);

        assertFalse(spawnable.contains(water));
        assertTrue(spawnable.size() < board.getPositions().size());
    }

    @Test
    void generateSpawnSpots_returnsCorrectNumberOfSpawns() {
        ArrayList<HexPos> spawns = setup.generateSpawnSpots(game, 1234);

        assertEquals(game.getNumPlayers(), spawns.size());
    }

    @Test
    void generateSpawnSpots_respectsMinimumSpawnDistance() {
        ArrayList<HexPos> spawns = setup.generateSpawnSpots(game, 5678);

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
        ArrayList<HexPos> spawns = setup.generateSpawnSpots(game, 42);

        Set<HexPos> unique = new HashSet<>(spawns);
        assertEquals(spawns.size(), unique.size());
    }

    @Test
    void generateSpawnSpots_isDeterministicForSameSeed() {
        ArrayList<HexPos> first = setup.generateSpawnSpots(game, 999);
        ArrayList<HexPos> second = setup.generateSpawnSpots(game, 999);

        assertEquals(first, second);
    }
}
