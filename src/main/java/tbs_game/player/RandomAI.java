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
import tbs_game.hexes.AxialPos;

public class RandomAI implements AI {

    Random random = new Random(Game.SEED * 7);

    public RandomAI() {

    }

    @Override
    public void doTurn(Game game, Player player) {

        // Create copy of state to simulate moves
        GameState simState = game.getState().createSimluationCopy();
        ArrayList<AxialPos> unitPositions = new ArrayList<>(simState.getUnitPositionsForPlayer(player));

        for (AxialPos pos : unitPositions) {
            if (simState.getUnitAt(pos) == null) {
                throw new Error("No unit at position. Unit positions is incorrect.");
            }

            ArrayList<AxialPos> reachable = new ArrayList<>(Movement.getReachableHexes(simState, pos));
            if (reachable.isEmpty()) {
                continue;
            }

            // Choose random tile of reachable to move to
            int randIdx = random.nextInt(reachable.size());
            AxialPos dest = reachable.get(randIdx);

            // Create and simulate the move
            Move simMove = Movement.planMove(simState, pos, dest);
            if (simMove == null) {
                throw new Error("Issue planning move. No move created.");
            }

            ActionHandler.resolveAction(simState, pos, dest);

            // Create move action. Use actual game so when the actions are executed, they update the actual game
            Action move = new MoveAction(game, simMove);
            game.getActionQueue().addAction(move);
        }

        game.getActionQueue().addAction(new EndTurnAction(game));
    }
}
