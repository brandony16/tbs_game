package tbs_game.game;

import tbs_game.hexes.AxialPos;
import tbs_game.units.Unit;

public class Combat {

    public static void attack(GameState state, AxialPos attackSq, AxialPos defenseSq) {
        Unit attacker = state.getUnitAt(attackSq);
        Unit defender = state.getUnitAt(defenseSq);

        int attackDamage = attacker.getType().attackDamage;
        defender.dealDamage(attackDamage);
        attacker.markAttacked();

        if (defender.isDead()) {
            state.captureUnit(attackSq, defenseSq);
        }
    }
}
