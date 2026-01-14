package tbs_game.gui.board;

import javafx.scene.Group;
import javafx.scene.text.Text;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.game.SetupHandler;
import tbs_game.gui.HexMath;
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
            double cx = HexMath.hexToPixelX(pos);
            double cy = HexMath.hexToPixelY(pos);

            Text coord = getTileCoord(pos, cx, cy);
            debugRoot.getChildren().add(coord);
        }
    }

    public void drawSpawnScores() {
        debugRoot.getChildren().clear();

        Board board = game.getBoard();

        for (AxialPos pos : board.getPositions()) {
            double cx = HexMath.hexToPixelX(pos);
            double cy = HexMath.hexToPixelY(pos);

            Text coord = getSpawnScore(pos, cx, cy);
            debugRoot.getChildren().add(coord);
        }
    }

    private Text getTileCoord(AxialPos pos, double cx, double cy) {
        Text coord = new Text();
        coord.setText(pos.q() + " , " + pos.r());
        coord.setX(cx - 10);
        coord.setY(cy);

        return coord;
    }

    private Text getSpawnScore(AxialPos pos, double cx, double cy) {
        Text score = new Text();
        int spawnScore = SetupHandler.getSpawnScore(pos, game.getBoard());
        if (spawnScore == Integer.MIN_VALUE) {
            return score;
        }

        score.setText("" + spawnScore);
        score.setX(cx - 10);
        score.setY(cy);

        return score;
    }

}
