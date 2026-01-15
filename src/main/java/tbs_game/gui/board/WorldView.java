package tbs_game.gui.board;

import java.util.List;
import java.util.Set;

import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import tbs_game.game.Game;
import tbs_game.hexes.AxialPos;

public class WorldView {

    private final BoardLayer board;
    private final HighlightLayer highlights;
    private final UnitLayer units;
    private final DebugLayer debug;

    public WorldView(Game game) {
        this.board = new BoardLayer(game);
        this.highlights = new HighlightLayer(game);
        this.units = new UnitLayer(game);
        this.debug = new DebugLayer(game);

        highlights.getRoot().setManaged(false);
        board.getOverlayRoot().setManaged(false);
        units.getRoot().setManaged(false);
        debug.getRoot().setManaged(false);
    }

    public void setOffsetX(double offset) {
        board.setTranslateX(offset);
        highlights.getRoot().setTranslateX(offset);
        units.getRoot().setTranslateX(offset);
        debug.getRoot().setTranslateX(offset);
    }

    public Node getBaseRoot() {
        return board.getBaseRoot();
    }

    public Node getOverlayRoot() {
        return board.getOverlayRoot();
    }

    public Node getHighlightsRoot() {
        return highlights.getRoot();
    }

    public Node getUnitsRoot() {
        return units.getRoot();
    }

    public Node getDebugRoot() {
        return debug.getRoot();
    }

    // ----- Debug Layer -----
    public void showCoords() {
        debug.drawCoords();
        debug.show();
    }

    public void showSpawnScore() {
        debug.drawSpawnScores();
        debug.show();
    }

    public void hideDebug() {
        debug.hide();
    }

    // ----- Drawing -----
    public void drawInitial(AxialPos selected, Set<AxialPos> reachableHexes) {
        board.drawBoard();
        highlights.drawHighlights(selected, reachableHexes);
        units.drawUnits();
    }

    public void redraw(AxialPos selected, Set<AxialPos> reachableHexes) {
        highlights.drawHighlights(selected, reachableHexes);
        units.drawUnits();
    }

    public SequentialTransition buildMoveAnimation(List<AxialPos> path, Runnable onFinish) {
        AxialPos start = path.get(0);
        AxialPos end = path.get(path.size() - 1);

        SequentialTransition sequence = units.buildMoveAnimation(path);

        sequence.setOnFinished(e -> {
            units.moveUnitElement(start, end);

            onFinish.run();
        });

        return sequence;
    }
}
