package tbs_game.gui.board;

import java.util.Set;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
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

        Board board = game.getBoard();

        for (AxialPos pos : board.getPositions()) {
            double cx = HexMath.axialToPixelX(pos);
            double cy = HexMath.axialToPixelY(pos);

            if (selectedPos != null) {
                if (selectedPos.equals(pos)) {
                    Polygon outline = HexFactory.createHex(cx, cy);

                    outline.setFill(Color.TRANSPARENT);
                    outline.setStroke(Color.GOLD);
                    outline.setStrokeWidth(2);
                    outline.setStrokeType(StrokeType.INSIDE);
                    outline.setMouseTransparent(true);

                    highlightRoot.getChildren().add(outline);
                } else if (reachableHexes.contains(pos)) {
                    Point2D[] corners = HexFactory.hexCorners(cx, cy);

                    for (int edge = 0; edge < 6; edge++) {
                        AxialPos neighbor = pos.neighbor(edge);

                        if (reachableHexes.contains(neighbor)) {
                            continue; // interior edge
                        }

                        Point2D a = corners[edge];
                        Point2D b = corners[(edge + 1) % 6];

                        Line line = new Line(a.getX(), a.getY(), b.getX(), b.getY());
                        line.setStroke(Color.LIGHTBLUE);
                        line.setStrokeWidth(4);
                        line.setStrokeLineCap(StrokeLineCap.ROUND);

                        highlightRoot.getChildren().add(line);
                    }
                }
            }
        }
    }
}
