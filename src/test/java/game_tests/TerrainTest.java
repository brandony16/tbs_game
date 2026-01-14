package game_tests;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.game.Game;
import tbs_game.game.Move;
import tbs_game.game.Movement;
import tbs_game.game.Rules;
import tbs_game.hexes.AxialPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

class TerrainTest {

    private Game game;
    private Board board;
    private AxialPos start;

    @BeforeEach
    void init() {
        game = Game.allPlains(10, 10, 2);
        board = game.getBoard();
        start = new AxialPos(0, 0);

        Unit unit = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        game.placeUnitAt(start, unit);
    }

    // ----- BASIC TERRAIN COST TESTS -----
    @Test
    void testCannotMoveOntoImpassableTerrain() {
        AxialPos water = new AxialPos(1, 0);
        board.getTile(water).setTerrain(Terrain.WATER);
        AxialPos mountain = new AxialPos(0, 1);
        board.getTile(mountain).setTerrain(Terrain.MOUNTAIN);

        assertFalse(Rules.canMove(game.getState(), start, water));
        assertFalse(Rules.canMove(game.getState(), start, mountain));
    }

    @Test
    void testLowCostTerrainConsumesMovement() {
        AxialPos plains = new AxialPos(1, 0);
        board.getTile(plains).setTerrain(Terrain.PLAINS);

        Move move = Movement.planMove(game.getState(), start, plains);

        assertNotNull(move);
        assertEquals(1, move.cost);
    }

    @Test
    void testHighCostTerrainConsumesMoreMovement() {
        AxialPos forest = new AxialPos(1, 0);
        board.getTile(forest).setTerrain(Terrain.FOREST);

        Move move = Movement.planMove(game.getState(), start, forest);

        assertNotNull(move);
        assertEquals(2, move.cost);
    }

    // ----- PATH SELECTION TESTS -----
    @Test
    void testCheapestPathIsChosen() {
        /*
         * Direct path:
         * (0,0) -> (1,0) -> (2,0) -> (3,0)  total: 5
         * (1,0) & (2, 0) are forest (cost 2 each)
         *
         * Alternate path:
         * (0,0) -> (0,1) -> (1,1) -> (2,1) -> (3,0) total: 4
         * all plains (cost 1 each)
         */
        AxialPos forest1 = new AxialPos(1, 0);
        AxialPos forest2 = new AxialPos(1, 0);
        board.getTile(forest1).setTerrain(Terrain.FOREST);
        board.getTile(forest2).setTerrain(Terrain.FOREST);

        AxialPos target = new AxialPos(3, 0);

        Move move = Movement.planMove(game.getState(), start, target);

        assertNotNull(move);
        assertEquals(4, move.cost); // Longer but cheaper path
    }

    @Test
    void testNoPathIfAllRoutesBlocked() {
        AxialPos target = new AxialPos(2, 0);

        // Surround target by impassable water
        for (AxialPos neighbor : target.getNeighbors()) {
            board.getTile(neighbor).setTerrain(Terrain.WATER);
        }

        assertNull(Movement.planMove(game.getState(), start, target));
    }

    // ----- ReachableHexes -----
    @Test
    void testReachableHexesRespectTerrainCost() {
        AxialPos forest = new AxialPos(1, 0);
        board.getTile(forest).setTerrain(Terrain.FOREST);
        AxialPos mountain = new AxialPos(0, 1);
        board.getTile(mountain).setTerrain(Terrain.MOUNTAIN);

        Unit unit = game.getUnitAt(start);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // 1 movement point left

        Set<AxialPos> reachable = Movement.getReachableHexes(game.getState(), start);

        for (AxialPos pos : start.getNeighbors()) {
            if (pos.equals(forest)) {
                assertFalse(reachable.contains(forest));
            } else if (pos.equals(mountain)) {
                assertFalse(reachable.contains(mountain));
            } else {
                assertTrue(reachable.contains(pos));
            }
        }
    }
}
