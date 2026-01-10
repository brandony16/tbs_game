package tbs_game.player;

import java.util.ArrayList;
import java.util.Random;

import tbs_game.game.Game;
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
        ArrayList<HexPos> unitPositions = new ArrayList<>(game.getPositionsForPlayer(player));

        for (HexPos pos : unitPositions) {
            ArrayList<HexPos> reachable = new ArrayList<>(game.getReachableHexes(pos));
            int randIdx = random.nextInt(reachable.size());
            HexPos dest = reachable.get(randIdx);

            Action move = new MoveAction(game, Movement.planMove(game.getState(), pos, dest));
            game.getActionQueue().addAction(move);
        }

        game.getActionQueue().addAction(new EndTurnAction(game));
    }
}
