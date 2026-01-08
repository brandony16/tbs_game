package tbs_game.game.actions;

import tbs_game.game.Game;
import tbs_game.game.Move;

public class MoveAction implements Action {

    private final Game game;
    private final Move move;

    public MoveAction(Game game, Move move) {
        this.game = game;
        this.move = move;
    }

    @Override
    public void execute() {
        game.resolveAction(move.from, move.to);
    }

}
