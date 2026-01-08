package tbs_game.game.actions;

import tbs_game.game.Game;

public class EndTurnAction implements Action {

    private final Game game;

    public EndTurnAction(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.endTurn();
    }

}
