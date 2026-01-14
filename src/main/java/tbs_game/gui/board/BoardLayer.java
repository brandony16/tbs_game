package tbs_game.gui.board;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
import tbs_game.hexes.AxialPos;

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

        for (AxialPos pos : board.getPositions()) {
            double cx = HexMath.axialToPixelX(pos);
            double cy = HexMath.axialToPixelY(pos);

            // Visible outline hex
            Polygon outlineHex = HexFactory.createHex(cx, cy);
            outlineHex.setFill(Color.TRANSPARENT);
            outlineHex.setStroke(Color.BLACK);
            outlineHex.setStrokeType(StrokeType.INSIDE);

            // Separate hex for clipping
            Polygon clipHex = HexFactory.createHex(cx, cy);

            Terrain terrain = board.getTile(pos).getTerrain();
            Node baseTerrain = TerrainRenderer.renderBaseTerrain(terrain, cx, cy);
            baseTerrain.setClip(clipHex);

            baseLayer.getChildren().addAll(baseTerrain, outlineHex);

            Node overlayTerrain = TerrainRenderer.renderOverlayTerrain(terrain, cx, cy);
            if (overlayTerrain != null) {
                overlayTerrain.setUserData(cy);
                overlayLayer.getChildren().add(overlayTerrain);
            }
        }

        sortOverlayLayer();
    }

    private void sortOverlayLayer() {
        List<Node> sorted = new ArrayList<>(overlayLayer.getChildren());

        sorted.sort(Comparator.comparingDouble(node
                -> (double) node.getUserData()
        ));

        overlayLayer.getChildren().setAll(sorted);
    }
}
