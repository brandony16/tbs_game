package tbs_game.game.game_helpers;

import java.util.ArrayList;

import tbs_game.game.ActionPath;
import tbs_game.game.GameState;
import tbs_game.hexes.AxialPos;
import tbs_game.units.Unit;

public class ActionExecutor {

    private final GameState state;
    private ActionPath lastExecuted;

    public ActionExecutor(GameState state) {
        this.state = state;
    }

    public ActionPath getLastExecuted() {
        return this.lastExecuted;
    }

    public void moveThenAttack(ActionPath path) {
        Unit mover = state.getUnitAt(path.from);

        ArrayList<AxialPos> totalPath = path.path;
        AxialPos prev = totalPath.get(0);
        for (int i = 1; i < totalPath.size() - 1; i++) {
            AxialPos step = totalPath.get(i);
            int cost = state.getBoard().getTile(step).cost();

            if (mover.getMovementPoints() < cost) {
                return;
            }

            state.moveUnitInternal(prev, step);
            mover.spendMovementPoints(cost);
            prev = step;
        }

        attack(prev, path.to);
        this.lastExecuted = path;
    }

    public void move(ActionPath path) {
        Unit mover = state.getUnitAt(path.from);

        ArrayList<AxialPos> totalPath = path.path;
        AxialPos prev = totalPath.get(0);
        for (int i = 1; i < totalPath.size(); i++) {
            AxialPos step = totalPath.get(i);
            int cost = state.getBoard().getTile(step).cost();

            if (mover.getMovementPoints() < cost) {
                break;
            }

            state.moveUnitInternal(prev, step);
            mover.spendMovementPoints(cost);
            prev = step;
        }
        this.lastExecuted = path;
    }

    public void attack(AxialPos from, AxialPos to) {
        Unit attacker = state.getUnitAt(from);
        Unit defender = state.getUnitAt(to);

        int attackDamage = attacker.getType().attackDamage;
        defender.dealDamage(attackDamage);
        attacker.markAttacked();

        if (defender.isDead()) {
            state.captureUnit(from, to);
        }

        ArrayList<AxialPos> path = new ArrayList<>();
        path.add(from);
        path.add(to);
        this.lastExecuted = new ActionPath(from, to, path, -1);
    }
}
