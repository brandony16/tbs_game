package tbs_game.game.actions;

import tbs_game.game.Game;
import tbs_game.game.Move;
import tbs_game.gui.GameGUI;

public class MoveAction implements Action {

    private final Game game;
    private final Move move;

    public MoveAction(Game game, Move move) {
        this.game = game;
        this.move = move;
    }

    @Override
    public void execute(GameGUI gui, Runnable onFinish) {
        game.resolveAction(move.from, move.to);

        gui.animateAIMove(move, onFinish);
    }

}
