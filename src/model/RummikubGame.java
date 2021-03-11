package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * The main class that coordinates the game Rummikub.
 */
public class RummikubGame implements Rummikub, Serializable {

  private static final long serialVersionUID = -8898273371515022588L;

  /**
   * The minimal number of players necessary to start the game.
   */
  private static final int MINIMUM_PLAYERS = 2;

  /**
   * The maximal number of players in one game. It is not allowed to add more players to one game.
   */
  private static final int MAXIMUM_PLAYERS = 4;

  /**
   * The number of tiles each player gets at the start of the game.
   */
  private static final int NUMBER_OF_TILES_AT_START = 14;

  /**
   * All players in the current game. This list is ordered by the player's ages so the order
   * represents the actual order of the players.
   */
  private final List<Player> players;

  /**
   * This game's board. The board is visible for all players unlike the players rack.
   */
  private final Board board;

  /**
   * The pool of tiles i.e., the tiles that can be pulled.
   */
  private final Deque<Tile> poolOfTiles;

  /**
   * The current player.
   */
  private Player currentPlayer;

  /**
   * The winner of this game. This variable is {@code null} as long as the game is running i.e.,
   * {@code isWon() == false}.
   */
  private Player winner;

  /**
   * A counter for the turns played.
   */
  private int turns;

  /**
   * Initializes a new {@code RummikubGame}.
   */
  private RummikubGame() {
    this.players = new LinkedList<>();
    this.board = Board.create();
    this.poolOfTiles = new LinkedList<>();
  }

  /**
   * Initializes a new {@code RummikubGame}.
   * 
   * @param board the board with which the game should be initialized
   */
  private RummikubGame(Board board) {
    this.players = new LinkedList<>();
    this.board = board;
    this.poolOfTiles = new LinkedList<>();
  }

  /**
   * Returns a new {@code RummikubGame} without players. To start the game the method
   * {@link #start()} is necessary.
   */
  public static RummikubGame create() {
    return new RummikubGame();
  }

  @Override
  public boolean addPlayer(Player p) {
    if (players.size() == MAXIMUM_PLAYERS || turns != 0) {
      // game is full or has already started
      return false;
    }
    players.add(p);
    return true;
  }

  @Override
  public void start() {
    if (players.size() < MINIMUM_PLAYERS) {
      throw new IllegalStateException("Not enough players.");
    }
    fillPoolOfTiles();
    handOutTiles();
    Collections.sort(players, (p1, p2) -> p1.getAge() - p2.getAge());
    currentPlayer = players.get(0);
    turns = 1;
  }

  /**
   * The method fills the pool of tiles. At the beginning the pool consists of each tile twice and
   * two additional jokers.
   */
  private void fillPoolOfTiles() {
    LinkedList<Tile> listOfTiles = new LinkedList<>();
    for (Color colorOfTile : Color.values()) {
      for (int j = RummikubTile.LOWEST_VALUE; j <= RummikubTile.HIGHEST_VALUE; j++) {
        Tile firstTileToAdd = RummikubTile.createTile(colorOfTile, j);
        Tile secondTileToAdd = RummikubTile.createTile(colorOfTile, j);
        listOfTiles.add(firstTileToAdd);
        listOfTiles.add(secondTileToAdd);
      }
    }
    Tile firstjokerToAdd = RummikubTile.createJoker();
    Tile secondjokerToAdd = RummikubTile.createJoker();
    listOfTiles.add(firstjokerToAdd);
    listOfTiles.add(secondjokerToAdd);
    Collections.shuffle(listOfTiles);
    this.poolOfTiles.addAll(listOfTiles);
  }

  /**
   * Hands out 14 tiles to each player.
   */
  private void handOutTiles() {
    for (int i = 0; i < NUMBER_OF_TILES_AT_START; i++) {
      players.forEach(player -> ((RummikubPlayer) player).pullTileAtStart(pullTile()));
    }
  }

  @Override
  public Tile getTile(int x, int y) {
    return board.getTile(x, y);
  }

  @Override
  public Tile pollTile(int x, int y, Player player) {
    if (player != currentPlayer) {
      throw new IllegalStateException("It's not this players turn.");
    }
    Tile tile = board.getTile(x, y);
    board.removeTile(x, y);
    return tile;
  }

  @Override
  public void setTile(int x, int y, Tile tile, Player player) {
    if (player != currentPlayer) {
      throw new IllegalStateException("It's not this players turn.");
    }
    board.setTile(x, y, tile);
  }

  @Override
  public boolean isValid() {
    List<Sequence> sequencesOnBoard = board.identifySequences();
    for (Sequence s : sequencesOnBoard) {
      if (!s.isValid(currentPlayer)) {
        return false;
      }
    }
    return ((RummikubPlayer) currentPlayer).validateRound();
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  @Override
  public List<Player> getPlayers() {
    return players;
  }

  @Override
  public boolean removePlayer(Player p) {
    if (!isPartOfGame(p)) {
      return false;
    }
    players.remove(p);
    return true;
  }

  /**
   * Returns {@code true} if the player is part of this game.
   */
  private boolean isPartOfGame(Player checkedPlayer) {
    for (Player p : players) {
      if (checkedPlayer == p) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isWon() {
    for (Player p : players) {
      if (p.getRack().isEmpty()) {
        return true;
      }
    }
    return players.size() < MINIMUM_PLAYERS;
  }

  @Override
  public void endCurrentRound() {
    // the player list is already properly sorted so only the index needs to be converted
    currentPlayer = players.get((++turns - 1) % players.size());
  }

  @Override
  public void setWinner(Player p) {
    winner = p;
  }

  @Override
  public Player getWinner() {
    if (!isWon()) {
      throw new IllegalStateException();
    }
    return winner;
  }

  @Override
  public Tile pullTile() {
    return this.poolOfTiles.pop();
  }

  @Override
  public boolean hasStarted() {
    return turns != 0;
  }

  @Override
  public int getRound() {
    return ((turns - 1) / players.size()) + 1;
  }

  @Override
  public int getBoardWidth() {
    return board.getWidth();
  }

  @Override
  public int getBoardHeight() {
    return board.getHeight();
  }

  /**
   * Removes all markings for all tiles on this game's board.
   */
  public void removeTileMarkings() {
    board.removeTileMarkings();
  }

  /**
   * Marks all wrongly set tiles on this game's board.
   */
  public void markWrongTiles() {
    List<Sequence> sequencesOnBoard = board.identifySequences();
    for (Sequence s : sequencesOnBoard) {
      if (!s.isValid(currentPlayer)) {
        s.markAsFalse();
      }
    }
  }

  /**
   * Returns a copy of the game with the specified values.
   * 
   * @param other the game to be copied
   * @return copy of the original game
   */
  public static RummikubGame copyOf(RummikubGame other) {
    RummikubGame copy = new RummikubGame(Board.copyOf(other.board));
    for (Player player : other.players) {
      copy.players.add(RummikubPlayer.copyOf((RummikubPlayer) player));
    }
    for (Tile tile : other.poolOfTiles) {
      copy.poolOfTiles.add(RummikubTile.copyOf((RummikubTile) tile));
    }
    return copy;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("Players: " + players.toString() + "\n");
    s.append("Number of tiles in pool: " + poolOfTiles.size() + "\n");
    s.append("Current player: " + currentPlayer + "\n");
    s.append("Current round: " + turns + "\n");
    s.append("Board: " + board.toString());
    return s.toString();
  }

}
