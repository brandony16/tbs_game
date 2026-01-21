package tbs_game.gui.board.layers;

import java.util.Set;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import tbs_game.game.Game;
import tbs_game.game.board.Board;
import tbs_game.gui.HexMath;
import tbs_game.gui.board.HexFactory;
import tbs_game.gui.coord_systems.WorldPos;
import tbs_game.hexes.AxialPos;

public class HighlightLayer {

  private final Game game;
  private final Group highlightRoot = new Group();

  public HighlightLayer(Game game) {
    this.game = game;
  }

  public Group getRoot() {
    return this.highlightRoot;
  }

  public void drawHighlights(AxialPos selectedPos, Set<AxialPos> reachableHexes) {
    highlightRoot.getChildren().clear();

    if (selectedPos == null) {
      return;
    }

    // Draw outline of selected pos
    WorldPos selectedCenter = HexMath.axialToWorldPos(selectedPos);
    Polygon outline = HexFactory.createHex(selectedCenter);

    outline.setFill(Color.TRANSPARENT);
    outline.setStroke(Color.GOLD);
    outline.setStrokeWidth(2);
    outline.setStrokeType(StrokeType.INSIDE);
    outline.setMouseTransparent(true);

    highlightRoot.getChildren().add(outline);

    // Draw outline for reachable hexes
    for (AxialPos pos : reachableHexes) {
      WorldPos reachableCenter = HexMath.axialToWorldPos(pos);
      WorldPos[] corners = HexFactory.hexCorners(reachableCenter);

      for (int edge = 0; edge < 6; edge++) {
        AxialPos rawNeighbor = pos.neighbor(edge);
        AxialPos neighbor = game.wrap(rawNeighbor);

        if (reachableHexes.contains(neighbor)) {
          continue; // interior edge
        }

        WorldPos a = corners[edge];
        WorldPos b = corners[(edge + 1) % 6];

        Line line = new Line(a.x(), a.y(), b.x(), b.y());
        line.setStroke(Color.LIGHTBLUE);
        line.setStrokeWidth(4);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        highlightRoot.getChildren().add(line);
      }
    }
  }
}
