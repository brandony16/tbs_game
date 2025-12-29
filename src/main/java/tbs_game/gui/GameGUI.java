package tbs_game.gui;

import javafx.scene.layout.StackPane;
import tbs_game.game.Game;

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
        boardView.redraw();
        hudView.updateHUD(boardView.getSelected());
    }

    public StackPane getRoot() {
        return root;
    }

    private void handleClick(double mouseX, double mouseY) {
        ClickResult result = boardView.handleClick(mouseX, mouseY);

        if (result == ClickResult.SELECTION_CHANGED) {
            hudView.updateHUD(boardView.getSelected());
        }
    }
}
