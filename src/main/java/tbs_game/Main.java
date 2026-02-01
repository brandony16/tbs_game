package tbs_game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tbs_game.game.Game;
import tbs_game.gui.GameGUI;
import tbs_game.gui.camera.CameraPlayground;

public class Main extends Application {

  private static final int WINDOW_WIDTH = 1960;
  private static final int WINDOW_HEIGHT = 1080;

  private boolean isBusy = false;

  private boolean playGame = true;

  @Override
  public void start(Stage stage) {
    if (playGame) {
      setupGame(stage);
    } else {
      setupCameraPlayground(stage);
    }
  }

  public static void main(String[] args) {
    launch();
  }

  private void setupGame(Stage stage) {
    Game game = new Game(25, 20, 4);
    game.setUpGame();
    // Game game = Game.battleSim(25, 20, 5);

    GameGUI gui = new GameGUI(game, WINDOW_WIDTH, WINDOW_HEIGHT);
    Scene scene = new Scene(gui.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

    stage.setTitle("Budget Polytopia");
    scene.getStylesheets().add(getClass().getResource("/hud.css").toExternalForm());

    scene.widthProperty().addListener((obs, oldVal, newVal) -> {
      gui.updateSceneWidth(newVal.doubleValue());
    });
    scene.heightProperty().addListener((obs, oldVal, newVal) -> {
      gui.updateSceneHeight(newVal.doubleValue());
    });

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

  private void setupCameraPlayground(Stage stage) {
    CameraPlayground cameraPlayground = new CameraPlayground();
    cameraPlayground.setUpBasic();
    Scene scene = new Scene(cameraPlayground.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
    cameraPlayground.setSceneSize(scene.getWidth(), scene.getHeight());

    stage.setTitle("Budget Polytopia");
    scene.getStylesheets().add(getClass().getResource("/hud.css").toExternalForm());

    // stage.setFullScreen(true);
    stage.setScene(scene);
    stage.show();

    cameraPlayground.getRoot().setFocusTraversable(true);
    cameraPlayground.getRoot().requestFocus();

    stage.widthProperty().addListener((obs, oldVal, newVal) -> {
      cameraPlayground.updateSceneWidth(newVal.doubleValue());
    });
    stage.heightProperty().addListener((obs, oldVal, newVal) -> {
      cameraPlayground.updateSceneHeight(newVal.doubleValue());
    });
  }
}
