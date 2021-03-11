package gui.scenes.lobby;

import gui.AbstractController;
import gui.utils.AnimationManager;
import gui.utils.SceneLoader;
import gui.utils.SoundManager;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Player;
import model.Rummikub;
import networking.Client;

public class LobbyController extends AbstractController implements Initializable {

  @FXML
  private ListView<String> playersList;

  @FXML
  private Button startGame;

  @FXML
  private Label errorLabel;

  private Client client = Client.getInstance();

  private ChangeListener<Rummikub> changeListener;

  private Rummikub game;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    handleStartGameButton();
    SceneLoader.loadAnimationSceneOnVBox(pane);
    if (client != null) {
      this.game = client.getCurrentGame();
      setPlayersInListView(this.game.getPlayers());
      createListenerForCurrentGameProperty();
    }
  }

  /**
   * Adds listener to property currentGame in order to get new value of currentGame if it's set in
   * client.
   */
  private void createListenerForCurrentGameProperty() {
    changeListener = (observable, oldGame, newGame) -> update(newGame);
    client.currentGameProperty().addListener(changeListener);
  }

  private void update(Rummikub newGame) {
    Platform.runLater(() -> {
      this.game = newGame;
      // updates ListView with Players
      setPlayersInListView(newGame.getPlayers());
      // loads gameScene if game has started
      if (newGame.hasStarted() && !client.isHost()) {
        client.currentGameProperty().removeListener(changeListener);
        AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
          loadGameScene();
        }));
      }
    });
  }

  private void handleStartGameButton() {
    if (!isHost()) {
      pane.getChildren().remove(startGame);
    }
  }

  private boolean isHost() {
    return client.isHost();
  }

  /**
   * Event handler for clicking the start game icon. Clicking the button results in switching to the
   * game screen.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void startGame(ActionEvent event) {
    try {
      game.start();
      client.startGame();
      SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
      AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
        loadGameScene();
      }));
    } catch (IllegalStateException e) {
      errorLabel.setVisible(true);
      AnimationManager.applyShakeAnimationOn(startGame);
    }
  }

  /**
   * Event handler for clicking the back icon. Clicking the button results in switching to the menu
   * screen.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void handleBackButton(ActionEvent event) {

    if (isHost()) {
      client.terminateGame();
    } else {
      client.leaveGame();
    }

    AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
      loadMenuScene();
    }));
  }

  /**
   * Displays players' name and age in playersList.
   *
   * @param players list.
   */
  private void setPlayersInListView(List<Player> players) {
    ObservableList<String> observedNames = FXCollections.observableArrayList();
    players.forEach(player -> {
      observedNames.add("Name: " + player.getName() + "\nAge: " + player.getAge());
      if (observedNames.size() > 1 && errorLabel.isVisible()) {
        errorLabel.setVisible(false);
      }
    });
    playersList.setItems(observedNames);
  }

}
