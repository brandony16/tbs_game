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
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class UnitLayer {

    private final Game game;
    private final Group unitRoot = new Group();
    private final Map<HexPos, Group> unitElements = new HashMap<>();

    public UnitLayer(Game game) {
        this.game = game;
    }

    public Group getRoot() {
        return this.unitRoot;
    }

    public Map<HexPos, Group> getUnitElements() {
        return this.unitElements;
    }

    public void moveUnitElement(HexPos from, HexPos to) {
        Group unitElement = unitElements.get(from);

        unitElements.remove(from);
        unitElements.put(to, unitElement);
    }

    public void drawUnits() {
        unitRoot.getChildren().clear();
        unitElements.clear();

        for (HexPos pos : game.getUnitPositions()) {
            Group unitElement = drawUnitElement(pos);
            if (game.getUnitAt(pos).getType() == UnitType.SETTLER) {
                unitElement = drawUnitElement(pos);
            }
            unitElements.put(pos, unitElement);
            unitRoot.getChildren().add(unitElement);
        }
    }

    private Group drawUnitElement(HexPos pos) {
        double cx = HexMath.hexToPixelX(pos);
        double cy = HexMath.hexToPixelY(pos);

        Group group = new Group();
        Unit unit = game.getUnitAt(pos);

        Node unitSprite = UnitRenderer.renderUnit(unit, cx, cy);
        Node healthBar = UnitRenderer.renderHealthBar(unit, cx, cy);

        group.getChildren().addAll(unitSprite, healthBar);
        return group;
    }

    public SequentialTransition buildMoveAnimation(List<HexPos> path) {
        HexPos start = path.get(0);
        Group unitElement = unitElements.get(start);

        SequentialTransition sequence = new SequentialTransition(unitElement);

        for (int i = 1; i < path.size(); i++) {
            HexPos a = path.get(i - 1);
            HexPos b = path.get(i);

            double dx = HexMath.hexToPixelX(b) - HexMath.hexToPixelX(a);
            double dy = HexMath.hexToPixelY(b) - HexMath.hexToPixelY(a);

            TranslateTransition tt = new TranslateTransition(Duration.millis(200));
            tt.setByX(dx);
            tt.setByY(dy);

            sequence.getChildren().add(tt);
        }

        return sequence;
    }
}
