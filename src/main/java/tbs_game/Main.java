package tbs_game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tbs_game.game.Game;
import tbs_game.gui.GameGUI;

public class Main extends Application {

    private static final int WINDOW_WIDTH = 1960;
    private static final int WINDOW_HEIGHT = 1080;

    private boolean isBusy = false;

    @Override
    public void start(Stage stage) {
        Game game = new Game(25, 12, 10);
        game.setUpGame();

        GameGUI gui = new GameGUI(game);

        Scene scene = new Scene(gui.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle("Budget Polytopia");
        scene.getStylesheets().add(getClass().getResource("/hud.css").toExternalForm());

        // stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (game.getActionQueue().isEmpty() || isBusy) {
                    return;
                }

                isBusy = true;
                game.getActionQueue().performNextAction(gui, () -> {
                    isBusy = false;
                    gui.updateHUD();
                });
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
