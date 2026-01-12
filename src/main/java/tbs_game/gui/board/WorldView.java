package tbs_game.gui.board;

import java.util.List;
import java.util.Set;

import javafx.animation.SequentialTransition;
import javafx.scene.Group;
import tbs_game.game.Game;
import tbs_game.hexes.HexPos;

public class WorldView {

    private final Group root = new Group();
    private final BoardLayer board;
    private final HighlightLayer highlights;
    private final UnitLayer units;
    private final DebugLayer debug;

    public WorldView(Game game) {
        this.board = new BoardLayer(game);
        this.highlights = new HighlightLayer(game);
        this.units = new UnitLayer(game);
        this.debug = new DebugLayer(game);

        root.getChildren().addAll(
                board.getRoot(),
                highlights.getRoot(),
                units.getRoot(),
                debug.getRoot()
        );
    }

    public Group getRoot() {
        return root;
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
    public void drawInitial(HexPos selected, Set<HexPos> reachableHexes) {
        board.drawBoard();
        highlights.drawHighlights(selected, reachableHexes);
        units.drawUnits();
    }

    public void redraw(HexPos selected, Set<HexPos> reachableHexes) {
        highlights.drawHighlights(selected, reachableHexes);
        units.drawUnits();
    }

    public SequentialTransition buildMoveAnimation(List<HexPos> path, Runnable onFinish) {
        HexPos start = path.get(0);
        HexPos end = path.get(path.size() - 1);

        SequentialTransition sequence = units.buildMoveAnimation(path);

        sequence.setOnFinished(e -> {
            units.moveUnitElement(start, end);

            onFinish.run();
        });

        return sequence;
    }
}
