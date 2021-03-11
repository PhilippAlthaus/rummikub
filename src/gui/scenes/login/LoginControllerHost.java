package gui.scenes.login;

import gui.utils.AnimationManager;
import gui.utils.SceneLoader;
import gui.utils.SoundManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.Player;
import model.Rummikub;
import model.RummikubGame;
import networking.Client;

/**
 * Controller for login scene form the host.
 */
public class LoginControllerHost extends LoginController implements Initializable {

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    SceneLoader.loadAnimationSceneOnVBox(super.pane);
    this.nameTextField.getStyleClass().add("noerror");
    this.ageTextField.getStyleClass().add("noerror");
  }

  /**
   * Executed by pressing the create game button. Checks if the name and age text field inputs are
   * valid and creates the game.
   */
  @FXML
  private void handleCreateGameButtonClick() {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);

    setUpValidation(this.nameTextField);
    setUpValidation(this.ageTextField);

    String name = this.nameTextField.getText();
    String age = this.ageTextField.getText();

    if (!isNameValid(name) || !isAgeValid(age)) {
      return;
    }

    Player player = createPlayerFromInput(name, age);
    Rummikub game = RummikubGame.create();

    try {
      Client.createSingletonHost(game, player);
    } catch (IOException | IllegalStateException e) {
      // Only reachable if the host create games rapidly
      System.out.println(e.getMessage());
    }

    AnimationManager.applyFadeAnimationOn(super.pane, (eventHandler -> {
      loadLobbyScene();
    }));
  }

}
