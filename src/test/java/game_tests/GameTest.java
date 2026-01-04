package game_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class GameTest {

    private Game game;

    @BeforeEach
    void init() {
        game = new Game(10, 10, 2);
        game.getBoard().makeAllPlains();
    }

    @Test
    void placeUnit() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);

        assertNull(game.getUnitAt(from));
        game.placeUnitAt(from, unit);
        assertNotNull(game.getUnitAt(from));
        assertEquals(unit, game.getUnitAt(from));
    }

    @Test
    void moveCorrectlyUpdatesState() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        HexPos to = new HexPos(1, 0);
        assertTrue(game.canMove(from, to));
        assertTrue(game.moveUnit(from, to));

        assertNull(game.getUnitAt(from));
        assertEquals(unit, game.getUnitAt(to));
    }

    @Test
    void unitCannotMoveOnOtherPlayersTurn() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        HexPos to = new HexPos(1, 0);

        assertFalse(game.canMove(to, from));
        assertFalse(game.moveUnit(from, to));
    }

    @Test
    void unitCannotMoveWithoutMovementPoints() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        HexPos to = new HexPos(1, 0);
        unit.spendMovementPoints(unit.getMovementPoints());

        assertFalse(game.canMove(from, to));
        assertFalse(game.moveUnit(from, to));
    }

    @Test
    void unitCannotAttackTwiceInOneTurn() {
        HexPos attackerPos = new HexPos(0, 0);
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(attackerPos, attacker);

        HexPos defenderPos = new HexPos(0, 1);
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(defenderPos, defender);

        assertTrue(game.canAttack(attackerPos, defenderPos));
        assertTrue(game.attackUnit(attackerPos, defenderPos));

        assertTrue(attacker.hasAttacked());
        assertFalse(game.canAttack(attackerPos, defenderPos));
    }

    @Test
    void killingAUnitRemovesItFromBoard() {
        HexPos attackerPos = new HexPos(0, 0);
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(attackerPos, attacker);

        HexPos defenderPos = new HexPos(0, 1);
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(defenderPos, defender);

        defender.dealDamage(defender.getHealth());
        assertTrue(defender.isDead());

        assertTrue(game.attackUnit(attackerPos, defenderPos));

        assertNull(game.getUnitAt(attackerPos));
        assertNotNull(game.getUnitAt(defenderPos));
        assertEquals(attacker, game.getUnitAt(defenderPos));
    }

    @Test
    void endingTurnResetsUnits() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        unit.spendMovementPoints(unit.getMovementPoints());
        unit.markAttacked();
        assertFalse(unit.canAct());

        game.endTurn();
        game.endTurn(); // Back to original player

        assertTrue(unit.canAct());
    }
}
