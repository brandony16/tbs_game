package tbs_game.gui;

import javafx.scene.layout.StackPane;
import tbs_game.game.Game;
import tbs_game.game.Move;
import tbs_game.gui.board.BoardView;

public class GameGUI {

    private final Game game;
    private final BoardView boardView;
    private final HudView hudView;

    private final StackPane root;

    public GameGUI(Game game) {
        this.game = game;
        this.boardView = new BoardView(game);
        this.hudView = new HudView(game);

        this.root = new StackPane();

        root.getChildren().addAll(boardView.getWorldRoot(), hudView.getHudRoot());

        hudView.setOnEndTurn(() -> endTurn());

        boardView.setOnTurnResolved(() -> {
            hudView.updateHUD(boardView.getSelected());
            hudView.hideCombatPreview();
        });

        boardView.setOnHoverChanged(ctx -> {
            if (ctx != null) {
                hudView.showCombatPreview(ctx);
            }
        });

        root.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));
        root.setOnMouseMoved(e -> boardView.handleMouseMoved(e.getX(), e.getY()));

        hudView.initHUD();
        boardView.drawInitial();
        hudView.updateHUD(boardView.getSelected());
    }

    public StackPane getRoot() {
        return root;
    }

    public void animateAIMove(Move move, Runnable onFinish) {
        this.boardView.animateAIMove(move, onFinish);
    }

    public void updateHUD() {
        hudView.updateHUD(boardView.getSelected());
    }

    private void handleClick(double mouseX, double mouseY) {
        ClickResult result = boardView.handleClick(mouseX, mouseY);

        if (result == ClickResult.SELECTION_CHANGED) {
            hudView.updateHUD(boardView.getSelected());
        }
    }

    private void endTurn() {
        if (game.canEndTurn()) {
            game.endTurn();
            boardView.nextTurn();
            hudView.updateHUD(null);
            hudView.hideCombatPreview();
        }
    }
}
