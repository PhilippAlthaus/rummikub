package gui.utils;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public final class SceneLoader {

  public static final String LOGIN_HOST_VIEW_PATH = "/gui/scenes/login/LoginHostView.fxml";
  public static final String MENU_VIEW_PATH = "/gui/scenes/menu/MenuView.fxml";
  public static final String LOBBY_VIEW_PATH = "/gui/scenes/lobby/LobbyView.fxml";
  public static final String GAME_VIEW_PATH = "/gui/scenes/game/GameView.fxml";
  public static final String RULES_VIEW_PATH = "/gui/scenes/rules/RulesView.fxml";
  public static final String LOGIN_CLIENT_VIEW_PATH = "/gui/scenes/login/LoginClientView.fxml";
  public static final String ENDGAME_VIEW_PATH = "/gui/scenes/endgame/EndgameView.fxml";
  public static final String ANIMATION_VIEW_PATH = "/gui/scenes/animation/AnimationView.fxml";
  public static final String SETTINGS_VIEW_PATH = "/gui/scenes/settings/SettingsView.fxml";

  // ensure non-instantiability
  private SceneLoader() {
    throw new AssertionError();
  }

  /**
   * Method to load a specific Scene.
   * 
   * @param path the path to the scene which is loaded
   * @return the new specified Scene.
   */
  public static Scene getSceneForPath(String path) {
    FXMLLoader fxmlLoader = new FXMLLoader();
    URL url = SceneLoader.class.getResource(path);
    fxmlLoader.setLocation(url);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    Parent root = fxmlLoader.getRoot();
    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();

    return new Scene(root, bounds.getWidth(), bounds.getHeight());
  }

  /**
   * Method to load an animation based onto an existing fxml.
   * 
   * @param parent the parent node of the fxml
   */
  public static void loadAnimationSceneOnVBox(VBox parent) {
    URL url = SceneLoader.class.getResource(SceneLoader.ANIMATION_VIEW_PATH);
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(url);
    try {
      HBox animationBox = loader.load();
      parent.getChildren().add(0, animationBox);
      for (Node image : animationBox.getChildren()) {
        AnimationManager.matrixAnimation(image);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
