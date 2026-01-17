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
        Game game = new Game(25, 20, 4);
        game.setUpGame();
        // Game game = Game.battleSim(25, 20, 5);

        GameGUI gui = new GameGUI(game, WINDOW_WIDTH, WINDOW_HEIGHT);

        Scene scene = new Scene(gui.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle("Budget Polytopia");
        scene.getStylesheets().add(getClass().getResource("/hud.css").toExternalForm());

        // stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double sceneWidth = newVal.doubleValue();
            gui.setSceneWidth(sceneWidth);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            double sceneHeight = newVal.doubleValue();
            gui.setSceneHeight(sceneHeight);
        });

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
