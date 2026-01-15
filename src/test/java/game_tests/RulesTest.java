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
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class RulesTest {

    private Game game;
    private AxialPos unitPos = new OffsetPos(10, 10).toAxial();
    private AxialPos otherPos;

    @BeforeEach
    void init() {
        this.game = Game.allPlains(20, 20, 2);
    }

    @AfterEach
    void reset() {
        this.game = Game.allPlains(20, 20, 2);
        this.unitPos = new OffsetPos(10, 10).toAxial();
        this.otherPos = null;
    }

    void setUpSolo() {
        reset();

        Unit unit = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);
    }

    void setUpBattle() {
        reset();

        Unit unit = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);

        otherPos = unitPos.neighbor(0);
        Unit aiUnit = new Unit(UnitType.WARRIOR, game.getPlayer(1));
        game.placeUnitAt(otherPos, aiUnit);
    }

    void setUpBattleMoveThenAttack() {
        reset();

        Unit unit = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);

        otherPos = unitPos.diagonalNeighbor(1); // 2 moves away
        Unit aiUnit = new Unit(UnitType.WARRIOR, game.getPlayer(1));
        game.placeUnitAt(otherPos, aiUnit);
    }

    void setUpBattleTooFar() {
        reset();

        Unit unit = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);

        otherPos = unitPos.add(new AxialPos(unit.getMaxMovementPoints() + 1, 0));
        Unit aiUnit = new Unit(UnitType.WARRIOR, game.getPlayer(1));
        game.placeUnitAt(otherPos, aiUnit);
    }

    void setUpFriendly() {
        reset();

        Unit unit = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        game.placeUnitAt(unitPos, unit);

        otherPos = unitPos.neighbor(3);
        Unit friendlyUnit = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        game.placeUnitAt(otherPos, friendlyUnit);
    }

    void canMoveManyTiles(AxialPos from, List<AxialPos> tiles) {
        for (AxialPos pos : tiles) {
            assertTrue(Rules.canMove(game.getState(), from, pos));
        }
    }

    // ----- canMove -----
    @Test
    void testCanMoveNoMover() {
        setUpSolo();
        // No unit at 0,0
        assertFalse(Rules.canMove(game.getState(), new AxialPos(0, 0), new AxialPos(1, 0)));
    }

    @Test
    void testCanMoveWrongTurn() {
        setUpBattle();
        // Not that units turn
        assertFalse(Rules.canMove(game.getState(), otherPos, otherPos.neighbor(0)));
    }

    @Test
    void testCanMoveIntoOther() {
        setUpFriendly();
        assertFalse(Rules.canMove(game.getState(), unitPos, otherPos));

        setUpBattle();
        assertFalse(Rules.canMove(game.getState(), unitPos, otherPos));
    }

    @Test
    void testCanMoveNoActionsRemaining() {
        setUpSolo();
        game.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.canMove(game.getState(), unitPos, unitPos.neighbor(0)));

        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        unit.spendMovementPoints(unit.getMovementPoints());
        assertFalse(Rules.canMove(game.getState(), unitPos, unitPos.neighbor(0)));
    }

    @Test
    void testCanMoveTooFar() {
        setUpSolo();

        Unit unit = game.getUnitAt(unitPos);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // 1 movement pt left

        assertFalse(Rules.canMove(game.getState(), unitPos, unitPos.diagonalNeighbor(2)));
        assertFalse(Rules.canMove(game.getState(), unitPos, unitPos.diagonalNeighbor(4)));
    }

    @Test
    void testCanMoveValid() {
        setUpSolo();
        Set<AxialPos> reachableSet = Movement.getReachableHexes(game.getState(), unitPos);
        List<AxialPos> movable = new ArrayList<>(reachableSet);

        canMoveManyTiles(unitPos, movable);
    }

    // ----- canAttack -----
    @Test
    void testCanAttackNoAttacker() {
        setUpBattle();
        // No unit at 2,2
        AxialPos emptyPos = new AxialPos(2, 2);
        assertFalse(Rules.canAttack(game.getState(), emptyPos, otherPos));
    }

    @Test
    void testCanAttackNoTarget() {
        setUpSolo();
        // No unit at neighbor 0
        assertFalse(Rules.canAttack(game.getState(), unitPos, unitPos.neighbor(0)));
    }

    @Test
    void testCanAttackFriendly() {
        setUpFriendly();
        assertFalse(Rules.canAttack(game.getState(), unitPos, otherPos));
    }

    @Test
    void testCanAttackAlreadyAttacked() {
        setUpBattle();
        game.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.canAttack(game.getState(), unitPos, otherPos));
    }

    @Test
    void testCanAttackOutOfRange() {
        setUpBattleMoveThenAttack();

        assertFalse(Rules.canAttack(game.getState(), unitPos, unitPos));
    }

    @Test
    void testCanAttackValid() {
        setUpBattle();
        assertTrue(Rules.canAttack(game.getState(), unitPos, otherPos));
    }

    // ----- canUnitMoveDistance -----
    @Test
    void testCanUnitMoveDistanceZeroDistance() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        assertTrue(Rules.canUnitMoveDistance(game.getState(), unit, unitPos, unitPos));
    }

    @Test
    void testCanUnitMoveDistanceWithinRange() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        // Move within movement points
        AxialPos target = new AxialPos(unitPos.q() + range, unitPos.r());
        assertTrue(Rules.canUnitMoveDistance(game.getState(), unit, unitPos, target));
    }

    @Test
    void testCanUnitMoveDistanceExactRange() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMaxMovementPoints();

        // Move exactly max distance
        AxialPos target = new AxialPos(unitPos.q() + range, unitPos.r());
        assertTrue(Rules.canUnitMoveDistance(game.getState(), unit, unitPos, target));
    }

    @Test
    void testCanUnitMoveDistanceBeyondRange() {
        setUpSolo();
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        // Move one further than allowed
        AxialPos target = new AxialPos(unitPos.q() + range + 1, unitPos.r());
        assertFalse(Rules.canUnitMoveDistance(game.getState(), unit, unitPos, target));
    }

    // ----- canDoAction -----
    @Test
    void testCanDoActionNoUnitAtSource() {
        setUpBattle();
        AxialPos empty = otherPos.neighbor(0); // No unit there
        assertFalse(Rules.canDoAction(game.getState(), empty, otherPos));
    }

    @Test
    void testCanDoActionMoveOnly() {
        setUpSolo();
        assertTrue(Rules.canDoAction(game.getState(), unitPos, unitPos.neighbor(0)));
    }

    @Test
    void testCanDoActionAttackInRange() {
        setUpBattle();
        assertTrue(Rules.canDoAction(game.getState(), unitPos, otherPos));
    }

    @Test
    void testCanDoActionEnemyOutOfRangeButReachableToAttack() {
        setUpBattleMoveThenAttack();

        // Should be able to move closer and then attack
        assertTrue(Rules.canDoAction(game.getState(), unitPos, otherPos));
    }

    @Test
    void testCanDoActionEnemyTooFarToReachAttackRange() {
        setUpBattleTooFar();

        assertFalse(Rules.canDoAction(game.getState(), unitPos, unitPos));
    }

    @Test
    void testCanDoActionFriendlyTarget() {
        setUpFriendly();
        assertFalse(Rules.canDoAction(game.getState(), unitPos, otherPos));
    }

    @Test
    void testCanDoActionWrongTurn() {
        setUpBattle();

        // other unit trying to attack
        assertFalse(Rules.canDoAction(game.getState(), otherPos, unitPos));
    }

    @Test
    void testCanDoActionUnitAlreadyAttacked() {
        setUpBattle();
        game.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.canDoAction(game.getState(), unitPos, otherPos));
    }
}
