package tbs_game.gui.board.layers;

import javafx.scene.Group;
import javafx.scene.text.Text;
import tbs_game.game.Game;
import tbs_game.game.board.Board;
import tbs_game.game.game_helpers.SetupHandler;
import tbs_game.gui.HexMath;
import tbs_game.gui.coord_systems.WorldPos;
import tbs_game.hexes.AxialPos;

public class DebugLayer {

  private final Game game;
  private final Group debugRoot = new Group();

  public DebugLayer(Game game) {
    this.game = game;
    debugRoot.setVisible(false);
  }

  public Group getRoot() {
    return this.debugRoot;
  }

  public void show() {
    debugRoot.setVisible(true);
  }

  public void hide() {
    debugRoot.setVisible(false);
  }

  public void drawCoords() {
    debugRoot.getChildren().clear();

    Board board = game.getBoard();

    for (AxialPos pos : board.getPositions()) {
      WorldPos hexCenter = HexMath.axialToWorldPos(pos);

      Text coord = getTileCoord(pos, hexCenter);
      debugRoot.getChildren().add(coord);
    }
  }

  public void drawSpawnScores() {
    debugRoot.getChildren().clear();

    Board board = game.getBoard();

    for (AxialPos pos : board.getPositions()) {
      WorldPos hexCenter = HexMath.axialToWorldPos(pos);

      Text coord = getSpawnScore(pos, hexCenter);
      debugRoot.getChildren().add(coord);
    }
  }

  private Text getTileCoord(AxialPos pos, WorldPos hexCenter) {
    Text coord = new Text();
    coord.setText(pos.q() + " , " + pos.r());
    coord.setX(hexCenter.x() - 10);
    coord.setY(hexCenter.y());

    return coord;
  }

  private Text getSpawnScore(AxialPos pos, WorldPos hexCenter) {
    Text score = new Text();
    int spawnScore = SetupHandler.getSpawnScore(pos, game.getBoard());
    if (spawnScore == Integer.MIN_VALUE) {
      return score;
    }

    score.setText("" + spawnScore);
    score.setX(hexCenter.x() - 10);
    score.setY(hexCenter.y());

    return score;
  }

}
