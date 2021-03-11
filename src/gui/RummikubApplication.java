package gui;

import gui.utils.SceneLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.Client;

public class RummikubApplication extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Scene scene = SceneLoader.getSceneForPath(SceneLoader.MENU_VIEW_PATH);
    primaryStage.setTitle("Rummikub");
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    Client client = Client.getInstance();
    if (client != null && client.getCurrentGame() != null) {
      if (client.isHost()) {
        client.terminateGame();
      } else {
        client.leaveGame();
      }
    }
    super.stop();
  }

}
