package tbs_game.game.actions;

import tbs_game.gui.GameGUI;

public interface Action {
  public void execute(GameGUI gui, Runnable onFinish);
}
