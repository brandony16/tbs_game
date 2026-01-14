package tbs_game.gui.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
import tbs_game.hexes.AxialPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class UnitLayer {

    private final Game game;
    private final Group unitRoot = new Group();
    private final Map<AxialPos, Group> unitElements = new HashMap<>();

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
        double cx = HexMath.axialToPixelX(pos);
        double cy = HexMath.axialToPixelY(pos);

        Group group = new Group();
        Unit unit = game.getUnitAt(pos);

        Node unitSprite = UnitRenderer.renderUnit(unit, cx, cy);
        Node healthBar = UnitRenderer.renderHealthBar(unit, cx, cy);

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

            double dx = HexMath.axialToPixelX(b) - HexMath.axialToPixelX(a);
            double dy = HexMath.axialToPixelY(b) - HexMath.axialToPixelY(a);

            TranslateTransition tt = new TranslateTransition(Duration.millis(200));
            tt.setByX(dx);
            tt.setByY(dy);

            sequence.getChildren().add(tt);
        }

        return sequence;
    }
}
