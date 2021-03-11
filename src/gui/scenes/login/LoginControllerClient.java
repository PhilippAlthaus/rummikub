package gui.scenes.login;

import gui.RummikubAlert;
import gui.utils.AnimationManager;
import gui.utils.SceneLoader;
import gui.utils.SoundManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import model.Player;
import networking.Client;

/**
 * Controller for login scene from the client.
 */
public class LoginControllerClient extends LoginController implements Initializable {

  @FXML
  private TextField ipTextField;

  @FXML
  private TextField ipLocalTextField;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    SceneLoader.loadAnimationSceneOnVBox(super.pane);
    this.nameTextField.getStyleClass().add("noerror");
    this.ageTextField.getStyleClass().add("noerror");
    this.ipTextField.getStyleClass().add("noerror");
    this.ipLocalTextField.getStyleClass().add("noerror");

    try {
      this.ipLocalTextField.setText(InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      // Do nothing
    }
  }

  /**
   * Executed by pressing the join game button. Checks if the name, age and IP address inputs are
   * valid and creates a {@code Client} instance.
   */
  @FXML
  private void handleJoinButtonClick() {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);

    setUpValidation(this.nameTextField);
    setUpValidation(this.ageTextField);
    setUpValidation(this.ipTextField);
    setUpValidation(this.ipLocalTextField);

    String name = this.nameTextField.getText();
    String age = this.ageTextField.getText();
    String ip = this.ipTextField.getText();
    String localIp = this.ipLocalTextField.getText();

    if (!isNameValid(name) || !isAgeValid(age) || !isIpValid(ip) || !isIpValid(localIp)) {
      return;
    }

    Player player = createPlayerFromInput(name, age);
    createClient(player, ip, localIp);
  }

  /**
   * Creates a {@code Client} instance if the input is valid or shows with a red boarder which
   * input/text field is invalid. If the IP address is not of the host, the game is already full or
   * started, an alert is shown.
   *
   * @param player is the {@code Player} instance which is associated with the Client
   * @param ipAddress is the IP address of the Host
   * @param ipLocal is the local Client IP address
   */
  private void createClient(Player player, String ipAddress, String ipLocal) {
    try {
      Client.createSingletonClient(player, ipAddress, ipLocal);
      // Shows an alert if there is no host with this IP address
    } catch (IOException e) {
      String errorTitle = "Invalid IP!";
      String errorMessage = "There is no game available at " + this.ipTextField.getText() + ".";
      errorMessage(errorTitle, errorMessage);
      return;
      // Shows an alert if the game is full or already started
    } catch (IllegalStateException e) {
      String errorTitle = "The game is not ready!";
      String errorMessage = "Maybe the game is full or already started?";
      errorMessage(errorTitle, errorMessage);
      return;
    }

    // If the client can be created switch to lobby scene
    AnimationManager.applyFadeAnimationOn(super.pane, (eventHandler -> {
      loadLobbyScene();
    }));
  }

  /**
   * Creates an {@link RummikubAlert} instance, which displays an error message to the user.
   *
   * @param errorTitle displayed error title
   * @param errorContent displayed error content
   */
  private void errorMessage(String errorTitle, String errorContent) {
    super.pane.setEffect(new GaussianBlur());
    RummikubAlert alert = new RummikubAlert(AlertType.WARNING, errorContent);
    alert.showAndWait();
    super.pane.setEffect(null);
  }

}

