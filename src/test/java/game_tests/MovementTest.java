package game_tests;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.game.Movement;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class MovementTest {

    // All tests are under the assumption that movement cost = hex distance, 
    // which is true on maps with tiles that all have a movement cost of 1.
    // Tests for different terrain are in TerrainTest.
    private Game game;
    private HexPos unitPos;

    @BeforeEach
    void init() {
        game = Game.allPlains(10, 10, 2);
        unitPos = new HexPos(0, 0);
    }

    @AfterEach
    void reset() {
        game = null;
        unitPos = null;
    }

    private void setUpSoloUnit() {
        Unit unit = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);
    }

    private void setUpFriendlyBlocker(HexPos pos) {
        Unit friendly = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        game.placeUnitAt(pos, friendly);
    }

    private void setUpEnemyUnit(HexPos pos) {
        Unit enemy = new Unit(UnitType.CAVALRY, game.getPlayer(1));
        game.placeUnitAt(pos, enemy);
    }

    // ----- move -----
    @Test
    void testMoveUpdatesPosition() {
        setUpSoloUnit();
        HexPos target = new HexPos(1, 0);

        Movement.move(game.getState(), unitPos, target);

        assertNull(game.getUnitAt(unitPos));
        assertNotNull(game.getUnitAt(target));
    }

    @Test
    void testMoveSpendsMovementPoints() {
        setUpSoloUnit();
        Unit unit = game.getUnitAt(unitPos);
        int startMP = unit.getMovementPoints();

        HexPos target = new HexPos(1, 0);
        int dist = unitPos.distanceTo(target);

        Movement.move(game.getState(), unitPos, target);

        assertEquals(startMP - dist, unit.getMovementPoints());
    }

    @Test
    void testMoveExactDistanceSpent() {
        setUpSoloUnit();
        Unit unit = game.getUnitAt(unitPos);

        HexPos target = new HexPos(unit.getMaxMovementPoints(), 0);
        int dist = unitPos.distanceTo(target);

        Movement.move(game.getState(), unitPos, target);

        assertEquals(unit.getMaxMovementPoints() - dist, unit.getMovementPoints());
    }

    // ----- getReachableHexes -----
    @Test
    void testGetReachableHexesNoUnit() {
        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);
        assertTrue(reachable.isEmpty());
    }

    @Test
    void testGetReachableHexesDoesNotIncludeOrigin() {
        setUpSoloUnit();
        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);

        assertFalse(reachable.contains(unitPos));
    }

    @Test
    void testGetReachableHexesWithinRange() {
        setUpSoloUnit();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);

        for (HexPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) <= range);
        }
    }

    @Test
    void testGetReachableHexesExcludesFriendlyUnits() {
        setUpSoloUnit();
        HexPos friendlyPos = new HexPos(1, 0);
        setUpFriendlyBlocker(friendlyPos);

        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);

        assertFalse(reachable.contains(friendlyPos));
    }

    @Test
    void testGetReachableHexesIncludesEnemyUnits() {
        setUpSoloUnit();
        HexPos enemyPos = new HexPos(1, 0);
        setUpEnemyUnit(enemyPos);

        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);

        assertTrue(reachable.contains(enemyPos));
    }

    @Test
    void testGetReachableHexesRespectsMovementPoints() {
        setUpSoloUnit();
        Unit unit = game.getUnitAt(unitPos);

        // Spend all but 1 movement
        unit.spendMovementPoints(unit.getMovementPoints() - 1);

        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);

        assertEquals(6, reachable.size());
        for (HexPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) == 1);
        }
    }

    @Test
    void testGetReachableHexesCantMoveThroughOpponenets() {
        setUpSoloUnit();

        // Surround unit
        for (HexPos pos : unitPos.getNeighbors()) {
            setUpEnemyUnit(pos);
        }

        Set<HexPos> reachable = Movement.getReachableHexes(game.getState(), unitPos);

        // Should be just the neighbors of the tile
        assertEquals(6, reachable.size());
        for (HexPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) == 1);
        }
    }
}
