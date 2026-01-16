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
import tbs_game.game.ActionPath;
import tbs_game.game.Game;
import tbs_game.game.GameState;
import tbs_game.game.Rules;
import tbs_game.game.game_helpers.MovementPlanner;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

class TerrainTest {

    private GameState state;
    private MovementPlanner planner;
    private Board board;
    private AxialPos start;

    @BeforeEach
    void init() {
        Game game = Game.allPlains(20, 20, 2);

        this.state = game.copyState();
        this.planner = new MovementPlanner(state);
        this.board = state.getBoard();
        this.start = new OffsetPos(10, 10).toAxial();

        Unit unit = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        state.placeUnitAt(start, unit);
    }

    // ----- BASIC TERRAIN COST TESTS -----
    @Test
    void testCannotMoveOntoImpassableTerrain() {
        AxialPos water = start.neighbor(0);
        board.getTile(water).setTerrain(Terrain.WATER);

        AxialPos mountain = start.neighbor(1);
        board.getTile(mountain).setTerrain(Terrain.MOUNTAIN);

        assertFalse(Rules.isValidMove(state, start, water));
        assertFalse(Rules.isValidMove(state, start, mountain));
    }

    @Test
    void testLowCostTerrainConsumesMovement() {
        AxialPos plains = start.neighbor(0);
        board.getTile(plains).setTerrain(Terrain.PLAINS);

        ActionPath move = planner.planAction(start, plains);

        assertNotNull(move);
        assertEquals(1, move.cost);
    }

    @Test
    void testHighCostTerrainConsumesMoreMovement() {
        AxialPos forest = start.neighbor(0);
        board.getTile(forest).setTerrain(Terrain.FOREST);

        ActionPath move = planner.planAction(start, forest);

        assertNotNull(move);
        assertEquals(2, move.cost);
    }

    // ----- PATH SELECTION TESTS -----
    @Test
    void testCheapestPathIsChosen() {
        /*
         * Direct path:
         * start -> (1,0) -> (2,0) -> (3,0)  total: 5
         * (1,0) & (2, 0) are forest (cost 2 each)
         *
         * Alternate path - longer but cheaper:
         * start -> (0,1) -> (1,1) -> (2,1) -> (3,0) total: 4
         * all plains (cost 1 each)
         */
        AxialPos forest1 = start.add(new AxialPos(1, 0));
        AxialPos forest2 = start.add(new AxialPos(2, 0));
        board.getTile(forest1).setTerrain(Terrain.FOREST);
        board.getTile(forest2).setTerrain(Terrain.FOREST);

        AxialPos target = start.add(new AxialPos(3, 0));

        ActionPath move = planner.planAction(start, target);

        assertNotNull(move);
        assertEquals(4, move.cost); // Longer but cheaper path
    }

    @Test
    void testNoPathIfAllRoutesBlocked() {
        AxialPos target = start.diagonalNeighbor(0);

        // Surround target by impassable water
        for (AxialPos neighbor : target.getNeighbors()) {
            board.getTile(neighbor).setTerrain(Terrain.WATER);
        }

        assertNull(planner.planAction(start, target));
    }

    // ----- ReachableHexes -----
    @Test
    void testReachableHexesRespectTerrainCost() {
        AxialPos forest = start.neighbor(0);
        board.getTile(forest).setTerrain(Terrain.FOREST);
        AxialPos mountain = start.neighbor(1);
        board.getTile(mountain).setTerrain(Terrain.MOUNTAIN);

        Unit unit = state.getUnitAt(start);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // 1 movement point left

        Set<AxialPos> reachable = planner.getReachableHexes(start);

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
