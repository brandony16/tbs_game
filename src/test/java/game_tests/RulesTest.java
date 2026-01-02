package game_tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.game.Movement;
import tbs_game.game.Rules;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class RulesTest {

    private Game game;
    private HexPos unitPos;
    private HexPos otherPos;

    @BeforeEach
    void init() {
        game = new Game(10, 10, 2);
    }

    @AfterEach
    void reset() {
        this.game = new Game(10, 10, 2);
        this.unitPos = null;
        this.otherPos = null;
    }

    void setUpSolo() {
        reset();

        unitPos = new HexPos(0, 0);
        Unit unit = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);
    }

    void setUpBattle() {
        reset();

        unitPos = new HexPos(0, 0);
        Unit unit = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);

        otherPos = new HexPos(0, 1);
        Unit aiUnit = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(otherPos, aiUnit);
    }

    void setUpFriendly() {
        reset();

        unitPos = new HexPos(0, 0);
        Unit unit = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);

        otherPos = new HexPos(0, 1);
        Unit friendlyUnit = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(otherPos, friendlyUnit);
    }

    void canMoveManyTiles(HexPos from, List<HexPos> tiles) {
        for (HexPos pos : tiles) {
            assertTrue(Rules.canMove(game, from, pos));
        }
    }

    // ----- canMove -----
    @Test
    void testCanMoveNoMover() {
        setUpSolo();
        // No unit at 2,2
        assertFalse(Rules.canMove(game, new HexPos(2, 2), otherPos));
    }

    @Test
    void testCanMoveWrongTurn() {
        setUpBattle();
        // Not that units turn
        assertFalse(Rules.canMove(game, otherPos, new HexPos(0, 2)));
    }

    @Test
    void testCanMoveIntoOther() {
        setUpFriendly();
        assertFalse(Rules.canMove(game, unitPos, otherPos));

        setUpBattle();
        assertFalse(Rules.canMove(game, unitPos, otherPos));
    }

    @Test
    void testCanMoveNoActionsRemaining() {
        setUpSolo();
        game.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.canMove(game, unitPos, new HexPos(-1, 0)));

        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        unit.spendMovementPoints(unit.getMovementPoints());
        assertFalse(Rules.canMove(game, unitPos, new HexPos(-1, 0)));
    }

    @Test
    void testCanMoveTooFar() {
        assertFalse(Rules.canMove(game, unitPos, new HexPos(1, 1)));
        assertFalse(Rules.canMove(game, unitPos, new HexPos(2, 0)));
    }

    @Test
    void testCanMoveValid() {
        setUpSolo();
        Set<HexPos> reachableSet = new Movement().getReachableHexes(game, unitPos);
        List<HexPos> movable = new ArrayList<>(reachableSet);

        canMoveManyTiles(unitPos, movable);
    }

    // ----- canAttack -----
    @Test
    void testCanAttackNoAttacker() {
        setUpBattle();
        // No unit at 2,2
        HexPos emptyPos = new HexPos(2, 2);
        assertFalse(Rules.canAttack(game, emptyPos, otherPos));
    }

    @Test
    void testCanAttackNoTarget() {
        setUpSolo();
        // There is no unit at the target
        HexPos target = new HexPos(1, 0);
        assertFalse(Rules.canAttack(game, unitPos, target));
    }

    @Test
    void testCanAttackFriendly() {
        setUpFriendly();
        assertFalse(Rules.canAttack(game, unitPos, otherPos));
    }

    @Test
    void testCanAttackAlreadyAttacked() {
        setUpBattle();
        game.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.canAttack(game, unitPos, otherPos));
    }

    @Test
    void testCanAttackOutOfRange() {
        setUpBattle();
        // Place defender out of attack range
        HexPos farPos = new HexPos(1, 1);
        Unit distantUnit = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(farPos, distantUnit);

        assertFalse(Rules.canAttack(game, unitPos, farPos));
    }

    @Test
    void testCanAttackValid() {
        setUpBattle();
        assertTrue(Rules.canAttack(game, unitPos, otherPos));
    }

    // ----- canUnitMoveDistance -----
    @Test
    void testCanUnitMoveDistanceZeroDistance() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        assertTrue(Rules.canUnitMoveDistance(unit, unitPos, unitPos));
    }

    @Test
    void testCanUnitMoveDistanceWithinRange() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        // Move within movement points
        HexPos target = new HexPos(unitPos.q() + range, unitPos.r());
        assertTrue(Rules.canUnitMoveDistance(unit, unitPos, target));
    }

    @Test
    void testCanUnitMoveDistanceExactRange() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMaxMovementPoints();

        // Move exactly max distance
        HexPos target = new HexPos(unitPos.q() + range, unitPos.r());
        assertTrue(Rules.canUnitMoveDistance(unit, unitPos, target));
    }

    @Test
    void testCanUnitMoveDistanceBeyondRange() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        // Move one further than allowed
        HexPos target = new HexPos(unitPos.q() + range + 1, unitPos.r());
        assertFalse(Rules.canUnitMoveDistance(unit, unitPos, target));
    }

    // ----- canDoAction -----
    @Test
    void testCanDoActionNoUnitAtSource() {
        setUpBattle();
        HexPos empty = new HexPos(2, 2);
        assertFalse(Rules.canDoAction(game, empty, otherPos));
    }

    @Test
    void testCanDoActionMoveOnly() {
        setUpSolo();
        HexPos target = new HexPos(1, 0);
        assertTrue(Rules.canDoAction(game, unitPos, target));
    }

    @Test
    void testCanDoActionAttackInRange() {
        setUpBattle();
        assertTrue(Rules.canDoAction(game, unitPos, otherPos));
    }

    @Test
    void testCanDoActionEnemyOutOfRangeButReachableToAttack() {
        setUpBattle();

        Unit unit = game.getUnitAt(unitPos);
        int attackRange = unit.getType().attackRange;

        // Place enemy just outside attack range
        HexPos farEnemy = new HexPos(attackRange + 1, 0);
        Unit enemy = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(farEnemy, enemy);

        // Should be able to move closer and then attack
        assertTrue(Rules.canDoAction(game, unitPos, farEnemy));
    }

    @Test
    void testCanDoActionEnemyTooFarToReachAttackRange() {
        setUpBattle();

        Unit unit = game.getUnitAt(unitPos);
        int attackRange = unit.getType().attackRange;
        int moveRange = unit.getMovementPoints();

        // Needs more movement than available to reach attack range
        HexPos unreachable = new HexPos(attackRange + moveRange + 1, 0);
        Unit enemy = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(unreachable, enemy);

        assertFalse(Rules.canDoAction(game, unitPos, unreachable));
    }

    @Test
    void testCanDoActionFriendlyTarget() {
        setUpFriendly();
        assertFalse(Rules.canDoAction(game, unitPos, otherPos));
    }

    @Test
    void testCanDoActionWrongTurn() {
        setUpBattle();
        game.endTurn(); // now unitPos unit is not current player
        assertFalse(Rules.canDoAction(game, unitPos, otherPos));
    }

    @Test
    void testCanDoActionUnitAlreadyAttacked() {
        setUpBattle();
        game.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.canDoAction(game, unitPos, otherPos));
    }
}
