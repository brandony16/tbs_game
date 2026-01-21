package tbs_game.gui.board.layers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
import tbs_game.gui.board.renderers.UnitRenderer;
import tbs_game.gui.coord_systems.WorldPos;
import tbs_game.hexes.AxialPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class UnitLayer {

  private final Game game;
  private final Group unitRoot = new Group();
  private final Map<AxialPos, Group> unitElements = new HashMap<>();

  static final WorldPos[] DIR_PIXEL_OFFSETS = new WorldPos[AxialPos.directions.size()];

  static {
    for (int i = 0; i < AxialPos.directions.size(); i++) {
      AxialPos d = AxialPos.directions.get(i);
      DIR_PIXEL_OFFSETS[i] = HexMath.axialToWorldPos(d);
    }
  }

  public UnitLayer(Game game) {
    this.game = game;
  }

  public Group getRoot() {
    return this.unitRoot;
  }

  public Map<AxialPos, Group> getUnitElements() {
    return this.unitElements;
  }

  public void moveUnitElement(AxialPos from, AxialPos to) {
    Group unitElement = unitElements.get(from);

    unitElements.remove(from);
    unitElements.put(to, unitElement);
  }

  public void drawUnits() {
    unitRoot.getChildren().clear();
    unitElements.clear();

    for (AxialPos pos : game.getUnitPositions()) {
      Group unitElement = drawUnitElement(pos);
      if (game.getUnitAt(pos).getType() == UnitType.SETTLER) {
        unitElement = drawUnitElement(pos);
      }
      unitElements.put(pos, unitElement);
      unitRoot.getChildren().add(unitElement);
    }
  }

  private Group drawUnitElement(AxialPos pos) {
    WorldPos hexCenter = HexMath.axialToWorldPos(pos);

    Group group = new Group();
    Unit unit = game.getUnitAt(pos);

    Node unitSprite = UnitRenderer.renderUnit(unit, hexCenter);
    Node healthBar = UnitRenderer.renderHealthBar(unit, hexCenter);

    group.getChildren().addAll(unitSprite, healthBar);
    return group;
  }

  public SequentialTransition buildMoveAnimation(List<AxialPos> path) {
    AxialPos start = path.get(0);
    Group unitElement = unitElements.get(start);

    SequentialTransition sequence = new SequentialTransition(unitElement);

    for (int i = 1; i < path.size(); i++) {
      AxialPos a = path.get(i - 1);
      AxialPos b = path.get(i);

      AxialPos offsetDir = wrapDelta(a, b);
      WorldPos offset = DIR_PIXEL_OFFSETS[getOffsetIndex(offsetDir)];

      TranslateTransition tt = new TranslateTransition(Duration.millis(200));
      tt.setByX(offset.x());
      tt.setByY(offset.y());

      sequence.getChildren().add(tt);
    }

    return sequence;
  }

  private AxialPos wrapDelta(AxialPos a, AxialPos b) {
    int width = game.getBoard().getWidth();

    int dq = b.q() - a.q();
    int dr = b.r() - a.r();

    // Account for wrapping width. Height is not wrapped
    if (dq > width / 2) {
      dq -= width;
    } else if (dq < -width / 2) {
      dq += width;
    }

    return new AxialPos(dq, dr);
  }

  private int getOffsetIndex(AxialPos offset) {
    for (int i = 0; i < AxialPos.directions.size(); i++) {
      if (AxialPos.directions.get(i).equals(offset)) {
        return i;
      }
    }
    throw new IllegalStateException("Invalid move delta: " + offset);
  }
}
