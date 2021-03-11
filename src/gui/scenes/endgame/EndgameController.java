package gui.scenes.endgame;

import gui.AbstractController;
import gui.utils.AnimationManager;
import gui.utils.SoundManager;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import model.Player;
import model.Rummikub;
import model.RummikubGame;
import networking.Client;

public class EndgameController extends AbstractController implements Initializable {

  private SanFranciscoFireworks sanFranciscoFireworks;

  private ChangeListener<Rummikub> gameChangeListener;

  private Client client = Client.getInstance();

  @FXML
  private VBox buttonsPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initClientsListener();
    String ranking = getRankingText();
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() {
        sanFranciscoFireworks = new SanFranciscoFireworks(primaryScreenBounds.getWidth(),
            primaryScreenBounds.getHeight() - 120, ranking);
        Platform.runLater(() -> pane.getChildren().add(0, sanFranciscoFireworks));
        sanFranciscoFireworks.play();
        SoundManager.stopBackgroundMusic();
        SoundManager.playFireworksSound();
        return null;
      }
    };
    Thread th = new Thread(task);
    th.setDaemon(true);
    th.start();

    if (client != null && !client.isHost()) {
      buttonsPane.getChildren().remove(0);
    }
  }

  private String getRankingText() {
    Player winner = null;
    List<Player> ranking = null;
    try {
      Rummikub currentGame = client.getCurrentGame();
      winner = currentGame.getWinner();
      ranking = currentGame.getPlayers();
    } catch (Exception e) {
      System.out.println(e);
    }
    if (winner == null) {
      return "Draw!";
    }
    Collections.sort(ranking, (p1, p2) -> {
      if (p1.getMinusPoints() == p2.getMinusPoints()) {
        if (p1.getRack().size() == p2.getRack().size()) {
          return p2.getAge() - p1.getAge();
        }
        return p1.getRack().size() - p2.getRack().size();
      }
      return p2.getMinusPoints() - p1.getMinusPoints();
    });
    String rankingText = winner.getName() + " wins!\n\nRanking:\n";
    int n = 1;
    for (Player p : ranking) {
      rankingText += n + ". " + p.getName() + ": " + p.getMinusPoints() + "\n";
      n++;
    }
    return rankingText;
  }

  @FXML
  private void handleRestartClick() {
    Rummikub oldGame = client.getCurrentGame();
    Rummikub newGame = RummikubGame.create();
    for (Player player : oldGame.getPlayers()) {
      player.reset();
      newGame.addPlayer(player);
    }
    client.restartGame(newGame);
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
  }

  @FXML
  private void handleQuitClick() {
    if (client.isHost()) {
      client.terminateGame();
    }
    client.leaveGame();
    stopFireworks();
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(pane, event -> loadMenuScene());
  }

  private void initClientsListener() {
    if (client == null) {
      return;
    }
    gameChangeListener = (observable, oldValue, newValue) -> {
      stopFireworks();
      if (client.isHost()) {
        AnimationManager.applyFadeAnimationOn(pane, event -> loadLobbyScene());
      } else {
        AnimationManager.applyFadeAnimationOn(pane, event -> loadGameScene());
      }
    };
    client.currentGameProperty().addListener(gameChangeListener);
  }

  private void stopFireworks() {
    client.currentGameProperty().removeListener(gameChangeListener);
    sanFranciscoFireworks.stop();
    SoundManager.stopFireworksSound();
    SoundManager.playBackgroundMusic();
  }
}
