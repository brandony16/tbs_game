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
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

class TerrainTest {

    private Game game;
    private Board board;
    private Movement movement;
    private HexPos start;

    @BeforeEach
    void init() {
        game = Game.allPlains(10, 10, 2);
        board = game.getBoard();
        movement = new Movement();
        start = new HexPos(0, 0);

        Unit unit = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        game.placeUnitAt(start, unit);
    }

    // ----- BASIC TERRAIN COST TESTS -----
    @Test
    void testCannotMoveOntoImpassableTerrain() {
        HexPos water = new HexPos(1, 0);
        board.getTile(water).setTerrain(Terrain.WATER);
        HexPos mountain = new HexPos(0, 1);
        board.getTile(mountain).setTerrain(Terrain.MOUNTAIN);

        assertFalse(Rules.canMove(game, start, water));
        assertFalse(Rules.canMove(game, start, mountain));
    }

    @Test
    void testLowCostTerrainConsumesMovement() {
        HexPos plains = new HexPos(1, 0);
        board.getTile(plains).setTerrain(Terrain.PLAINS);

        Move move = Movement.planMove(game, start, plains);

        assertNotNull(move);
        assertEquals(1, move.cost);
    }

    @Test
    void testHighCostTerrainConsumesMoreMovement() {
        HexPos forest = new HexPos(1, 0);
        board.getTile(forest).setTerrain(Terrain.FOREST);

        Move move = Movement.planMove(game, start, forest);

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
        HexPos forest1 = new HexPos(1, 0);
        HexPos forest2 = new HexPos(1, 0);
        board.getTile(forest1).setTerrain(Terrain.FOREST);
        board.getTile(forest2).setTerrain(Terrain.FOREST);

        HexPos target = new HexPos(3, 0);

        Move move = Movement.planMove(game, start, target);

        assertNotNull(move);
        assertEquals(4, move.cost); // Longer but cheaper path
    }

    @Test
    void testNoPathIfAllRoutesBlocked() {
        HexPos target = new HexPos(2, 0);

        // Surround target by impassable water
        for (HexPos neighbor : target.getNeighbors()) {
            board.getTile(neighbor).setTerrain(Terrain.WATER);
        }

        assertNull(Movement.planMove(game, start, target));
    }

    // ----- ReachableHexes -----
    @Test
    void testReachableHexesRespectTerrainCost() {
        HexPos forest = new HexPos(1, 0);
        board.getTile(forest).setTerrain(Terrain.FOREST);
        HexPos mountain = new HexPos(0, 1);
        board.getTile(mountain).setTerrain(Terrain.MOUNTAIN);

        Unit unit = game.getUnitAt(start);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // 1 movement point left

        Set<HexPos> reachable = movement.getReachableHexes(game, start);

        for (HexPos pos : start.getNeighbors()) {
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
