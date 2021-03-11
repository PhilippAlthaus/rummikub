package model;

import java.util.List;

/**
 * This interface provides all methods that are necessary to implement the game Rummikub.
 */
public interface Rummikub {

  /**
   * Adds a player to the current game.
   * 
   * @param p the player to be added
   * @return {@code true} if the player could be added to the game i.e., the game is not full (4
   *         players) and did not start yet
   */
  boolean addPlayer(Player p);

  /**
   * Starts a new game.
   * 
   * @throws IllegalStateException if one player tries to start the game alone as the game is
   *         intended to be played by at least two players
   */
  void start();

  /**
   * Returns the current player.
   */
  Player getCurrentPlayer();

  /**
   * Returns all players of this game.
   */
  List<Player> getPlayers();

  /**
   * Removes a player from the current game.
   * 
   * @param p the player to be removed
   * @return {@code true} if this player was part of the game
   */
  boolean removePlayer(Player p);

  /**
   * Returns {@code true} if the game has been won. A game is won if a player has no tiles left on
   * his rack or only one player is left i.e., {@code getPlayers().size == 1}. This method should be
   * called after each round so the winner can be identified by {@link #getCurrentPlayer()}. If this
   * method returns {@code true} no further actions should be done by any player so all controllers
   * should prohibit any actions. Furthermore the winner should be set with
   * {@link #setWinner(Player)} to the current winner in order to make him accessible by
   * {@link #getWinner()}.
   * 
   * <p><strong>If this method is not called after each round it will lead to inconsistencies, as
   * the current player might not be the actual winner. It is also strongly recommended to set the
   * winner as soon as this method returns {@code true}</strong>
   */
  boolean isWon();

  /**
   * Returns the tile at the specified position.
   * 
   * @param x x-Axis coordinate
   * @param y y-Axis coordinate
   */
  Tile getTile(int x, int y);

  /**
   * Retrieves and removes the tile at the specified position.
   * 
   * @param x x-Axis coordinate
   * @param y y-Axis coordinate
   * @param player who wants to use the method
   * @return the removed tile
   * @throws IllegalStateException if it's not the players turn
   */
  Tile pollTile(int x, int y, Player player);

  /**
   * Sets a {@code Tile} at the specified position on the board.
   * 
   * @param x x-Axis coordinate
   * @param y y-Axis coordinate
   * @param tile the {@code Tile} to be set
   * @param player who wants to use the method
   * @throws IllegalStateException if it's not the players turn i.e.,
   *         {@code player != currentPlayer}
   */
  void setTile(int x, int y, Tile tile, Player player);

  /**
   * Returns {@code true} if the current state of the game is valid.
   */
  boolean isValid();

  /**
   * Ends the current round by switching the current player to the next one.
   */
  void endCurrentRound();

  /**
   * Sets the winner to a given player.
   * 
   * @param p the player that will be set as winner
   */
  void setWinner(Player p);

  /**
   * Returns the winner of this game. Do <strong>NOT</strong> call this method without checking if
   * the game indeed has a winner otherwise this method will throw an {@code IllegalStateException}.
   * Only access this method in the following manner:
   * 
   * <p>{@code if (isWon()) Player winner = getWinner();}
   * 
   * <p>If the game is won but the winner was not set properly this method will return {@code null}
   * so beware of setting the winner immediately if the game is won.
   * 
   * @return the winner of this game
   * @throws IllegalStateException if the game has no winner yet i.e., {@code isWon() == false}
   */
  Player getWinner();

  /**
   * Pulls a tile from the pool.
   * 
   * <p>Note: This method does not add the pulled tile to the player's rack it only removes a tile
   * from the pool. In order to add this tile to a player's rack the method
   * {@link Player#pullTileFromPool(Tile t)} is needed. This should be done by a GUI controller in
   * the following way:
   * 
   * <p>{@code player.pullTileFromPool(game.pullTile())}.
   * 
   * @return the pulled tile
   * @throws NoSuchElementException if the pool is empty
   */
  Tile pullTile();

  /**
   * Returns {@code true} if this game has started.
   */
  boolean hasStarted();

  /**
   * Returns the rounds played during this game.
   */
  int getRound();

  /**
   * Returns the width of this game's board.
   */
  int getBoardWidth();

  /**
   * Returns the height of this game's board.
   */
  int getBoardHeight();

}
