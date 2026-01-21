package game_tests;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.game.game_helpers.GameState;
import tbs_game.game.game_helpers.MovementPlanner;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class PlannerTest {

    // All tests are under the assumption that movement cost = hex distance, 
    // which is true on maps with tiles that all have a movement cost of 1.
    // Tests for different terrain are in TerrainTest.
    private GameState state;
    private MovementPlanner planner;

    private Player player1;
    private Player player2;

    private AxialPos unitPos;
    private AxialPos rightEdgePos;
    private AxialPos leftEdgePos;

    @BeforeEach
    void init() {
        int width = 20;
        int height = 20;
        Game game = Game.allPlains(width, height, 2);

        this.state = game.copyState();
        this.planner = new MovementPlanner(state);

        this.player1 = game.getPlayer(0);
        this.player2 = game.getPlayer(1);

        this.unitPos = new OffsetPos(width / 2, height / 2).toAxial();
        this.leftEdgePos = new OffsetPos(0, 4).toAxial();
        this.rightEdgePos = new OffsetPos(width - 1, 4).toAxial();
    }

    @AfterEach
    void reset() {
        this.state = null;
        this.planner = null;

        this.player1 = null;
        this.player2 = null;

        this.unitPos = null;
        this.rightEdgePos = null;
        this.leftEdgePos = null;
    }

    private void setUpSoloUnit(AxialPos pos) {
        Unit unit = new Unit(UnitType.CAVALRY, player1);
        state.placeUnitAt(pos, unit);
    }

    private void setUpFriendlyBlocker(AxialPos pos) {
        Unit friendly = new Unit(UnitType.CAVALRY, player1);
        state.placeUnitAt(pos, friendly);
    }

    private void setUpEnemyUnit(AxialPos pos) {
        Unit enemy = new Unit(UnitType.CAVALRY, player2);
        state.placeUnitAt(pos, enemy);
    }

    // ----- getReachableHexes -----
    @Test
    void testGetReachableHexesNoUnit() {
        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);
        assertTrue(reachable.isEmpty());
    }

    @Test
    void testGetReachableHexesDoesNotIncludeOrigin() {
        setUpSoloUnit(unitPos);
        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);

        assertFalse(reachable.contains(unitPos));
    }

    @Test
    void testGetReachableHexesWithinRange() {
        setUpSoloUnit(unitPos);
        Unit unit = state.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);

        for (AxialPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) <= range);
        }
    }

    @Test
    void testGetReachableHexesExcludesFriendlyUnits() {
        setUpSoloUnit(unitPos);
        AxialPos friendlyPos = unitPos.neighbor(4);
        setUpFriendlyBlocker(friendlyPos);

        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);

        assertFalse(reachable.contains(friendlyPos));
    }

    @Test
    void testGetReachableHexesIncludesEnemyUnits() {
        setUpSoloUnit(unitPos);
        AxialPos enemyPos = unitPos.neighbor(4);
        setUpEnemyUnit(enemyPos);

        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);

        assertTrue(reachable.contains(enemyPos));
    }

    @Test
    void testGetReachableHexesRespectsMovementPoints() {
        setUpSoloUnit(unitPos);
        Unit unit = state.getUnitAt(unitPos);

        // Spend all but 1 movement
        unit.spendMovementPoints(unit.getMovementPoints() - 1);

        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);

        assertEquals(6, reachable.size());
        for (AxialPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) == 1);
        }
    }

    @Test
    void testGetReachableHexesCantMoveThroughOpponenets() {
        setUpSoloUnit(unitPos);

        // Surround unit
        for (AxialPos pos : unitPos.getNeighbors()) {
            setUpEnemyUnit(pos);
        }

        Set<AxialPos> reachable = planner.getReachableHexes(unitPos);

        // Should be just the neighbors of the tile
        assertEquals(6, reachable.size());
        for (AxialPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) == 1);
        }
    }

    @Test
    void testReachableHexesWrapHorizontally() {
        setUpSoloUnit(leftEdgePos);

        Unit unit = state.getUnitAt(leftEdgePos);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // Set to 1 movement point for ease
        Set<AxialPos> reachable = planner.getReachableHexes(leftEdgePos);

        // Wrapped neighbor on the left side
        AxialPos wrappedNeighbor = leftEdgePos.neighbor(3); // Neighbor 3 is directly to left
        wrappedNeighbor = state.wrap(wrappedNeighbor);

        assertTrue(reachable.contains(wrappedNeighbor));
    }

    @Test
    void testAllReachableHexesAreCanonical() {
        setUpSoloUnit(rightEdgePos);

        Set<AxialPos> reachable = planner.getReachableHexes(rightEdgePos);

        for (AxialPos pos : reachable) {
            assertEquals(pos, state.wrap(pos));
        }
    }

    // ----- findPath ----- ADD MORE TESTS 
    @Test
    void testPathUsesWrappedShortcut() {
        setUpSoloUnit(leftEdgePos);

        AxialPos end = leftEdgePos.add(new AxialPos(-3, 0)); // 3 to left
        end = state.wrap(end);

        ArrayList<AxialPos> path = planner.findPath(leftEdgePos, end);

        assertNotNull(path);
        // Distance + 1 because path includes the starting tile
        assertEquals(state.distanceBetween(leftEdgePos, end) + 1, path.size());
    }
}
