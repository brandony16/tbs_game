package tbs_game.game.actions;

import tbs_game.game.Game;
import tbs_game.gui.GameGUI;

public class EndTurnAction implements Action {

    private final Game game;

    public EndTurnAction(Game game) {
        this.game = game;
    }

    @Override
    public void execute(GameGUI gui, Runnable onFinish) {
        game.endTurn();
        onFinish.run();
    }

}
