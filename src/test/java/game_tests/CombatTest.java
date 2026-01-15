package game_tests;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Combat;
import tbs_game.game.Game;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class CombatTest {

    private Game game;
    private AxialPos attackerPos;
    private AxialPos defenderPos;

    @BeforeEach
    void init() {
        game = Game.allPlains(10, 10, 2);

        attackerPos = new OffsetPos(5, 5).toAxial();
        defenderPos = new OffsetPos(6, 5).toAxial();
    }

    @AfterEach
    void reset() {
        game = null;
        attackerPos = null;
        defenderPos = null;
    }

    private Unit setUpBattle() {
        Unit attacker = new Unit(UnitType.WARRIOR, game.getPlayer(0));
        Unit defender = new Unit(UnitType.WARRIOR, game.getPlayer(1));

        game.placeUnitAt(attackerPos, attacker);
        game.placeUnitAt(defenderPos, defender);

        return defender;
    }

    // ----- attack -----
    @Test
    void testAttackDealsDamage() {
        Unit defender = setUpBattle();
        int startHP = defender.getHealth();

        Combat.attack(game.getState(), attackerPos, defenderPos);

        assertTrue(defender.getHealth() < startHP);
    }

    @Test
    void testAttackDealsExactDamage() {
        Unit defender = setUpBattle();
        Unit attacker = game.getUnitAt(attackerPos);

        int expectedDamage = attacker.getType().attackDamage;
        int startHP = defender.getHealth();

        Combat.attack(game.getState(), attackerPos, defenderPos);

        assertEquals(startHP - expectedDamage, defender.getHealth());
    }

    @Test
    void testAttackMarksAttackerAsUsed() {
        setUpBattle();
        Unit attacker = game.getUnitAt(attackerPos);

        Combat.attack(game.getState(), attackerPos, defenderPos);

        assertTrue(attacker.hasAttacked());
    }

    @Test
    void testAttackDoesNotKillSurvivor() {
        setUpBattle();
        Combat.attack(game.getState(), attackerPos, defenderPos);

        Unit defender = game.getUnitAt(defenderPos);
        assertNotNull(defender);
        assertEquals(game.getPlayer(1), defender.getOwner());
    }

    @Test
    void testAttackKillsAndCapturesUnit() {
        Unit defender = setUpBattle();
        defender.dealDamage(defender.getHealth()); // ensure death

        Combat.attack(game.getState(), attackerPos, defenderPos);

        assertNull(game.getUnitAt(attackerPos));
        assertNotNull(game.getUnitAt(defenderPos));
        assertEquals(game.getPlayer(0), game.getUnitAt(defenderPos).getOwner());
    }
}
