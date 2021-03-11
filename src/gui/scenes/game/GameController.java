package gui.scenes.game;

import gui.AbstractController;
import gui.RummikubAlert;
import gui.utils.AnimationManager;
import gui.utils.RummikubTimer;
import gui.utils.SoundManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import model.Player;
import model.Rummikub;
import model.RummikubGame;
import model.Tile;
import networking.Client;


public class GameController extends AbstractController implements Initializable {

  private static final double BOARD_SCALE = 0.8;
  private static final double CONTROL_BUTTONS_SCALE = 0.8;
  private static final double PLAYERS_TABLE_VIEW_SCALE = 0.9;
  private static final double PLAYERS_TABLE_VIEW_CELL_SIZE = 50;
  private static final double MAX_PLAYERS_NUMBER = 4;
  private static final int RACK_ROW_NUMBER = 2;
  private final int rackColumnNumber;
  private final int boardColumnNumber;
  private final int boardRowNumber;
  private final Rectangle2D primaryScreenBounds;
  private final List<TileView> falseSequenceTileViews;
  private final BooleanProperty isBoardChanged;
  private final Client client;
  private RummikubTimer rummikubTimer;
  private double imageWidth;
  private double imageHeight;
  private TileView selectedTileView = null;
  private Tile selectedTile = null;
  private ArrayList<ArrayList<TileView>> boardTileViews;
  private ArrayList<ArrayList<TileView>> rackTileViews;
  private ChangeListener<Rummikub> gameChangeListener;
  private ChangeListener<Boolean> terminateListener;
  @FXML
  private BorderPane gamePane;
  @FXML
  private GridPane boardPane;
  @FXML
  private GridPane rackPane;
  @FXML
  private VBox controlPane;
  @FXML
  private Label roundLabel;
  @FXML
  private Label timeLabel;
  @FXML
  private TableView<Player> playersTableView;
  @FXML
  private Button cancelButton;
  @FXML
  private Button sortButton;
  @FXML
  private Button doneButton;

  /**
   * Initializes variables.
   */
  public GameController() {
    primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    isBoardChanged = new SimpleBooleanProperty(false);
    this.client = Client.getInstance();
    boardColumnNumber = client.getCurrentGame().getBoardWidth();
    boardRowNumber = client.getCurrentGame().getBoardHeight();
    rackColumnNumber = boardColumnNumber;
    falseSequenceTileViews = new LinkedList<>();
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    initBoardPane();
    initRackPane();
    initControlPane();
    updateView(client.getCurrentGame());
    gameChangeListener = (observable, oldGame, newGame) -> {
      SoundManager.playSoundEffect(SoundManager.ROUND_PATH);
      updateView(newGame);
      if (!oldGame.getCurrentPlayer().equals(newGame.getCurrentPlayer())) {
        rummikubTimer.reset();
      }
    };
    client.currentGameProperty().addListener(gameChangeListener);
    initTimer();
    initTerminatePropertyListener();
  }

  /**
   * Initializes Board. Sets size, constraints and ImageViews to each cell.
   */
  private void initBoardPane() {
    double boardPaneWidth = primaryScreenBounds.getWidth() * BOARD_SCALE;
    double boardPaneHeight = primaryScreenBounds.getHeight() * BOARD_SCALE;
    boardPane.setPrefWidth(boardPaneWidth);
    boardPane.setPrefHeight(boardPaneHeight);
    imageWidth = boardPane.getPrefWidth() / boardColumnNumber;
    imageHeight = boardPane.getPrefHeight() / boardRowNumber;
    boardTileViews = new ArrayList<>();

    setConstraints(boardPane, boardColumnNumber, boardRowNumber);
    setImageViewsInCells(boardPane, boardColumnNumber, boardRowNumber, boardTileViews);
  }

  /**
   * Initializes Rack. Sets size, constraints and ImageViews to each cell.
   */
  private void initRackPane() {
    double rackPaneWidth = primaryScreenBounds.getWidth() * BOARD_SCALE;
    double rackPaneHeight = imageHeight * RACK_ROW_NUMBER;
    rackPane.setPrefWidth(rackPaneWidth);
    rackPane.setPrefHeight(rackPaneHeight);
    rackTileViews = new ArrayList<>();

    setConstraints(rackPane, rackColumnNumber, RACK_ROW_NUMBER);
    setImageViewsInCells(rackPane, rackColumnNumber, RACK_ROW_NUMBER, rackTileViews);
  }

  /**
   * Sets constraints for each row and column.
   *
   * @param gridPane to set.
   * @param columnNumber number of columns.
   * @param rowNumber number of rows.
   */
  private void setConstraints(GridPane gridPane, int columnNumber, int rowNumber) {
    for (int column = 0; column < columnNumber; column++) {
      ColumnConstraints columnConstraint = new ColumnConstraints();
      columnConstraint.setPercentWidth(100.f / columnNumber);
      columnConstraint.setHalignment(HPos.CENTER);
      gridPane.getColumnConstraints().add(columnConstraint);
    }

    for (int row = 0; row < rowNumber; row++) {
      RowConstraints rowConstraint = new RowConstraints();
      rowConstraint.setPercentHeight(100.f / rowNumber);
      rowConstraint.setValignment(VPos.CENTER);
      gridPane.getRowConstraints().add(rowConstraint);
    }
  }

  /**
   * Adds ImageView for representing Tiles to each cell.
   *
   * @param gridPane to set.
   * @param columnNumber number of columns.
   * @param rowNumber number of rows.
   */
  private void setImageViewsInCells(GridPane gridPane, int columnNumber, int rowNumber,
      ArrayList<ArrayList<TileView>> tileViews) {
    for (int column = 0; column < columnNumber; column++) {
      ArrayList<TileView> rowImageViews = new ArrayList<>();
      for (int row = 0; row < rowNumber; row++) {
        TileView tileView = createTileView();
        rowImageViews.add(tileView);
        gridPane.add(tileView, column, row);
        tileView.setOpacity(0);
      }
      tileViews.add(rowImageViews);
    }
  }

  /**
   * Initializes pane that contains playersTableView and control buttons.
   */
  private void initControlPane() {
    double controlPaneWidth = primaryScreenBounds.getWidth() * (1 - BOARD_SCALE);
    double controlPaneHeight = primaryScreenBounds.getHeight() * BOARD_SCALE;
    controlPane.setPrefWidth(controlPaneWidth);
    controlPane.setPrefHeight(controlPaneHeight);

    initControlButtons();
    initPlayersTableView();
  }

  /**
   * Initializes TableView that shows players.
   */
  private void initPlayersTableView() {
    double controlPaneHeight = MAX_PLAYERS_NUMBER * PLAYERS_TABLE_VIEW_CELL_SIZE;
    playersTableView.prefWidthProperty()
        .bind(controlPane.widthProperty().multiply(PLAYERS_TABLE_VIEW_SCALE));
    playersTableView.setPrefHeight(controlPaneHeight);
    playersTableView.setSelectionModel(null);
    //hide header
    playersTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
      Pane header = (Pane) playersTableView.lookup("TableHeaderRow");
      if (header.isVisible()) {
        header.setMaxHeight(0);
        header.setMinHeight(0);
        header.setPrefHeight(0);
        header.setVisible(false);
      }
    });

    TableColumn<Player, String> playersCol = new TableColumn<>();
    PropertyValueFactory<Player, String> lastNameCellValueFactory = new PropertyValueFactory<>(
        "name");
    playersCol.setCellValueFactory(lastNameCellValueFactory);
    playersCol.prefWidthProperty().bind(playersTableView.widthProperty().multiply(1));
    playersCol.setResizable(false);
    playersTableView.getColumns().add(playersCol);
  }

  /**
   * Sets size of control buttons.
   */
  private void initControlButtons() {
    cancelButton.prefWidthProperty()
        .bind(controlPane.widthProperty().multiply(CONTROL_BUTTONS_SCALE));
    sortButton.prefWidthProperty()
        .bind(controlPane.widthProperty().multiply(CONTROL_BUTTONS_SCALE));
    doneButton.prefWidthProperty()
        .bind(controlPane.widthProperty().multiply(CONTROL_BUTTONS_SCALE));
    isBoardChanged.addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
      if (newValue) {
        doneButton.setText("Done");
      } else {
        doneButton.setText("Pull");
      }
    }));
  }

  /**
   * Closes gameController if game was terminated by host.
   */
  private void initTerminatePropertyListener() {
    terminateListener = (observable, oldValue, newValue) -> {
      if (newValue && !client.isHost()) {
        Platform.runLater(() -> {
          removeListeners();
          AnimationManager.applyFadeAnimationOn(gamePane, e -> loadMenuScene(gamePane));
        });
      }
    };
    client.terminateProperty().addListener(terminateListener);
  }

  /**
   * Sets players' names in PlayersTableView. Highlights current player's row.
   *
   * @param players to be shown.
   */
  private void setPlayersTableViewWith(List<Player> players) {
    players = FXCollections.observableList(players);
    playersTableView.getItems().clear();
    playersTableView.getItems().addAll(players);
    playersTableView.setRowFactory(tv -> new TableRow<Player>() {
      @Override
      public void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);
        Player currentPlayer = client.getCurrentGame().getCurrentPlayer();
        if (player != null && player.equals(currentPlayer)) {
          setStyle("-fx-background-color: tomato;");
        } else {
          setStyle("-fx-background-color: -fx-table-cell-border-color, #616161;");
        }
      }
    });
  }

  /**
   * Updates timeLabel with current turn time.
   */
  private void initTimer() {
    rummikubTimer = new RummikubTimer();
    rummikubTimer.start(event -> timeLabel.setText("Time: " + rummikubTimer.getTime()));
  }

  /**
   * Removes all listeners. Should be called before closing gameController.
   */
  private void removeListeners() {
    client.terminateProperty().removeListener(terminateListener);
    client.currentGameProperty().removeListener(gameChangeListener);
    rummikubTimer.stop();
  }


  /**
   * Sorts player's rack.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void handleSortButton(ActionEvent event) {
    if (selectedTile != null) {
      return;
    }
    SoundManager.playSoundEffect(SoundManager.SORT_PATH);
    client.getPlayer().sortRack();
    updateRackPane(client.getPlayer().getRack());
  }

  /**
   * Requests backup from server and cancel current turn.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void handleCancelButton(ActionEvent event) {
    unhighlightAsFalseSequence();
    isBoardChanged.setValue(false);
    resetSelectedTile();
    client.getBackup();
  }

  /**
   * Handles the event player is clicking the end turn icon. This results in sending the game back
   * to the server.
   */
  @FXML
  private void handleDoneButton(ActionEvent event) {
    unhighlightAsFalseSequence();
    if (selectedTile != null) {
      return;
    }
    if (!isBoardChanged.getValue()) {
      pullTile();
    } else {
      highlightAsFalseSequence();
    }
    client.updateGame();
  }


  /**
   * Shows alert for asking if player wants to quit current game.
   *
   * @param event keyEvent.
   */
  @FXML
  private void handleKeyPressed(KeyEvent event) {
    if (event.getCode() != KeyCode.ESCAPE) {
      return;
    }
    gamePane.setEffect(new GaussianBlur());

    RummikubAlert alert = new RummikubAlert(AlertType.CONFIRMATION,
        "Do you want to quit the game?");
    alert.setHeaderText("Quit");
    Optional<ButtonType> result = alert.showAndWait();
    gamePane.setEffect(null);
    if (result.isPresent() && result.get() == ButtonType.OK) {
      removeListeners();
      if (client.isHost()) {
        client.terminateGame();
      } else {
        client.leaveGame();
      }
      AnimationManager.applyFadeAnimationOn(gamePane, e -> loadMenuScene(gamePane));
    }
  }

  /**
   * Updates view.
   */
  private void updateView(Rummikub game) {
    if (game.isWon()) {
      removeListeners();
      AnimationManager.applyFadeAnimationOn(gamePane, event -> loadEndgameScene(gamePane));
    }
    isBoardChanged.setValue(false);
    resetSelectedTile();
    setDisableControlButtons(!client.isMyTurn());
    Platform.runLater(() -> {
      roundLabel.setText("Round " + game.getRound());
      setPlayersTableViewWith(game.getPlayers());
      updateRackPane(client.getPlayer().getRack());
      updateBoardPane(game);
    });
  }

  /**
   * Sets images of tiles in boardPane.
   *
   * @param game current value.
   */
  private void updateBoardPane(Rummikub game) {
    clearPane(boardTileViews);
    for (int col = 0; col < boardColumnNumber; col++) {
      for (int row = 0; row < boardRowNumber; row++) {
        Tile tile = game.getTile(col, row);
        if (tile != null) {
          TileView tileView = boardTileViews.get(col).get(row);
          tileView.setImage(getImage(tile));
          tileView.setOpacity(1);
        }
      }
    }
  }

  /**
   * Highlights all tiles in false Sequences.
   */
  private void highlightAsFalseSequence() {
    boolean isWrong = false;
    Rummikub game = client.getCurrentGame();
    ((RummikubGame) game).markWrongTiles();
    for (int col = 0; col < boardColumnNumber; col++) {
      for (int row = 0; row < boardRowNumber; row++) {
        Tile tile = game.getTile(col, row);
        if (tile != null && !tile.isInCorrectSequence()) {
          TileView tileView = boardTileViews.get(col).get(row);
          falseSequenceTileViews.add(tileView);
          tileView.highlightAsFalseSequence();
          isWrong = true;
        }
      }
    }
    if (isWrong) {
      SoundManager.playSoundEffect(SoundManager.ERROR_PATH);
    }
  }

  /**
   * Unhighlights all tiles in false Sequences.
   */
  private void unhighlightAsFalseSequence() {
    falseSequenceTileViews.forEach(TileView::unhighlight);
    falseSequenceTileViews.clear();
    ((RummikubGame) client.getCurrentGame()).removeTileMarkings();
  }

  /**
   * Sets images of tiles in rackPane.
   *
   * @param rack of player that is stored in client.
   */
  private void updateRackPane(List<Tile> rack) {
    clearPane(rackTileViews);
    int count = 0;
    for (Tile tile : rack) {
      int indexRow = count / rackColumnNumber;
      int indexCol = count - indexRow * rackColumnNumber;
      TileView imageView = rackTileViews.get(indexCol).get(indexRow);
      imageView.setImage(getImage(tile));
      imageView.setOpacity(1);
      count++;
    }
  }

  /**
   * Removes all images from rack or board pane.
   */
  private void clearPane(ArrayList<ArrayList<TileView>> pane) {
    for (ArrayList<TileView> column : pane) {
      for (TileView imageView : column) {
        imageView.setImage(getImage(null));
        imageView.setOpacity(0);
      }
    }
  }

  /**
   * Picking a tile from the pool.
   */
  private void pullTile() {
    Rummikub game = client.getCurrentGame();
    try {
      Tile tile = game.pullTile();
      client.getPlayer().pullTileFromPool(tile);
      updateRackPane(client.getPlayer().getRack());
    } catch (NoSuchElementException e) {
      RummikubAlert alert = new RummikubAlert(AlertType.ERROR, "Pool is empty!");
      alert.show();
    }
  }

  /**
   * Sets control buttons disable for true.
   *
   * @param bool value.
   */
  private void setDisableControlButtons(boolean bool) {
    sortButton.setDisable(bool);
    cancelButton.setDisable(bool);
    doneButton.setDisable(bool);
  }

  /**
   * Creates TileView with Image for specific tile. Adds EventHandler for MouseEvents to each
   * TileView.
   *
   * @return TileView.
   */
  private TileView createTileView() {
    TileView imageView = new TileView(getImage(null));
    imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
      if (!client.isMyTurn()) {
        return;
      }
      unhighlightAsFalseSequence();
      Node source = (Node) event.getSource();
      boolean isBoardPane = source.getParent().equals(boardPane);
      //coordinate of cell in boardPane
      int colIndex = GridPane.getColumnIndex(source);
      int rowIndex = GridPane.getRowIndex(source);

      Rummikub game = client.getCurrentGame();
      Player player = client.getPlayer();
      Tile currentTile = null;
      if (isBoardPane) {
        currentTile = game.getTile(colIndex, rowIndex);
      } else {
        int index = rowIndex * rackColumnNumber + colIndex;
        if (index < player.getRack().size()) {
          currentTile = player.getRack().get(rowIndex);
        }
      }

      //selects tile if there was not any selected yet and current cell is not empty.
      if (selectedTile == null && currentTile != null) {
        SoundManager.playSoundEffect(SoundManager.PLACE_PATH);
        selectedTileView = imageView;
        imageView.highlightAsSelected();
        if (isBoardPane) {
          selectedTile = game.pollTile(colIndex, rowIndex, player);
        } else {
          int index = rowIndex * rackColumnNumber + colIndex;
          selectedTile = player.getTileFromRack(index);
        }
        return;
      }
      //if tile is selected and user clicks on other cell.
      //its not allowed to put tile in not empty cell
      if (selectedTile != null && currentTile == null && !selectedTileView.equals(imageView)
          && isBoardPane) {
        SoundManager.playSoundEffect(SoundManager.PLACE_PATH);
        imageView.setImage(selectedTileView.getImage());
        imageView.setOpacity(1);
        game.setTile(colIndex, rowIndex, selectedTile, player);
        selectedTileView.setOpacity(0);
        selectedTileView.setImage(getImage(null));
        isBoardChanged.setValue(true);
        if (selectedTileView.getParent().equals(rackPane)) {
          updateRackPane(player.getRack());
        }
        resetSelectedTile();
        return;
      }
      //if tile is selected and user clicks on same cell.
      if (selectedTile != null && selectedTileView.equals(imageView)) {
        SoundManager.playSoundEffect(SoundManager.PLACE_PATH);
        if (isBoardPane) {
          game.setTile(colIndex, rowIndex, selectedTile, player);
        } else {
          int index = rowIndex * rackColumnNumber + colIndex;
          client.getPlayer().getRack().add(index, selectedTile);
        }
        resetSelectedTile();
      }
    });

    return imageView;
  }

  /**
   * Unselects current tile.
   */
  private void resetSelectedTile() {
    if (selectedTileView != null) {
      selectedTileView.unhighlight();
    }
    selectedTile = null;
    selectedTileView = null;
  }

  /**
   * Returns image of tile.
   *
   * @param tile to represent.
   * @return image.
   */
  private Image getImage(Tile tile) {
    String imagePath = getImagePath(tile);
    return new Image(imagePath, imageWidth, imageHeight, true, true);
  }

  /**
   * Returns path of image for tile.
   *
   * @param tile to load.
   * @return path.
   */
  private String getImagePath(Tile tile) {
    String fileName;
    if (tile == null) {
      fileName = "BACK";
    } else if (!tile.isJoker()) {
      String tileColor = tile.getColor().name();
      String tileValue = String.valueOf(tile.getValue());
      fileName = tileColor + tileValue;
    } else {
      fileName = "Joker";
    }

    return "/resources/images/tiles/" + fileName + ".png";
  }
}