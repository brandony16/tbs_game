package tbs_game.gui.board;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
import tbs_game.hexes.HexPos;

public class BoardLayer {

    private final Game game;
    private final Group boardRoot = new Group();

    private final Group baseLayer = new Group();
    private final Group overlayLayer = new Group();

    public BoardLayer(Game game) {
        this.game = game;
        boardRoot.getChildren().addAll(baseLayer, overlayLayer);
    }

    public Group getRoot() {
        return this.boardRoot;
    }

    public void drawBoard() {
        baseLayer.getChildren().clear();
        overlayLayer.getChildren().clear();

        Board board = game.getBoard();

        for (HexPos pos : board.getPositions()) {
            double cx = HexMath.hexToPixelX(pos);
            double cy = HexMath.hexToPixelY(pos);

            // Visible outline hex
            Polygon outlineHex = HexFactory.createHex(cx, cy);
            outlineHex.setFill(Color.TRANSPARENT);
            outlineHex.setStroke(Color.BLACK);

            // Separate hex for clipping
            Polygon clipHex = HexFactory.createHex(cx, cy);

            Terrain terrain = board.getTile(pos).getTerrain();
            Node baseTerrain = TerrainRenderer.renderBaseTerrain(terrain, cx, cy);
            baseTerrain.setClip(clipHex);

            Node overlayTerrain = TerrainRenderer.renderOverlayTerrain(terrain, cx, cy);

            baseLayer.getChildren().addAll(baseTerrain, outlineHex);
            if (overlayTerrain != null) {
                overlayLayer.getChildren().add(overlayTerrain);
            }

        }
    }
}
