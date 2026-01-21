package tbs_game.gui.board;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
import tbs_game.gui.camera.coord_systems.WorldPos;
import tbs_game.hexes.AxialPos;

public class BoardLayer {

  private final Game game;

  private final Group baseLayer = new Group();
  private final Group overlayLayer = new Group();

  public BoardLayer(Game game) {
    this.game = game;
  }

  public Group getBaseRoot() {
    return this.baseLayer;
  }

  public Group getOverlayRoot() {
    return this.overlayLayer;
  }

  public void setTranslateX(double offset) {
    baseLayer.setTranslateX(offset);
    overlayLayer.setTranslateX(offset);
  }

  public void drawBoard() {
    baseLayer.getChildren().clear();
    overlayLayer.getChildren().clear();

    Board board = game.getBoard();

    for (AxialPos pos : board.getPositions()) {
      WorldPos hexCenter = HexMath.axialToWorldPos(pos);

      // Visible outline hex
      Polygon outlineHex = HexFactory.createHex(hexCenter);
      outlineHex.setFill(Color.TRANSPARENT);
      outlineHex.setStroke(Color.BLACK);
      outlineHex.setStrokeType(StrokeType.INSIDE);

      // Separate hex for clipping
      Polygon clipHex = HexFactory.createHex(hexCenter);

      Terrain terrain = board.getTile(pos).getTerrain();
      Node baseTerrain = TerrainRenderer.renderBaseTerrain(terrain, hexCenter);
      baseTerrain.setClip(clipHex);

      baseLayer.getChildren().addAll(baseTerrain, outlineHex);

      Node overlayTerrain = TerrainRenderer.renderOverlayTerrain(terrain, hexCenter);
      if (overlayTerrain != null) {
        overlayTerrain.setUserData(hexCenter.y());
        overlayLayer.getChildren().add(overlayTerrain);
      }
    }

    sortOverlayLayer();
    cacheLayers();
  }

  private void cacheLayers() {
    // baseLayer.setCache(true);
    // baseLayer.setCacheHint(CacheHint.SPEED);

    // overlayLayer.setCache(true);
    // overlayLayer.setCacheHint(CacheHint.SPEED);
  }

  private void sortOverlayLayer() {
    List<Node> sorted = new ArrayList<>(overlayLayer.getChildren());

    sorted.sort(Comparator.comparingDouble(node -> (double) node.getUserData()));

    overlayLayer.getChildren().setAll(sorted);
  }
}
