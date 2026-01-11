package tbs_game.player;

import java.util.ArrayList;
import java.util.Random;

import tbs_game.game.ActionHandler;
import tbs_game.game.Game;
import tbs_game.game.GameState;
import tbs_game.game.Move;
import tbs_game.game.Movement;
import tbs_game.game.actions.Action;
import tbs_game.game.actions.EndTurnAction;
import tbs_game.game.actions.MoveAction;
import tbs_game.hexes.HexPos;

public class RandomAI implements AI {

    Random random = new Random(Game.SEED * 7);

    public RandomAI() {

    }

    @Override
    public void doTurn(Game game, Player player) {

        GameState simState = game.getState().createSimluationCopy();
        ArrayList<HexPos> unitPositions = new ArrayList<>(simState.getUnitPositionsForPlayer(player));

        for (HexPos pos : unitPositions) {
            if (simState.getUnitAt(pos) == null) {
                continue;
            }

            ArrayList<HexPos> reachable = new ArrayList<>(Movement.getReachableHexes(simState, pos));
            if (reachable.isEmpty()) {
                continue;
            }

            int randIdx = random.nextInt(reachable.size());
            HexPos dest = reachable.get(randIdx);

            Move simMove = Movement.planMove(simState, pos, dest);
            if (simMove == null) {
                continue;
            }

            ActionHandler.resolveAction(simState, pos, dest);

            Action move = new MoveAction(game, simMove);
            game.getActionQueue().addAction(move);
        }

        game.getActionQueue().addAction(new EndTurnAction(game));
    }
}
