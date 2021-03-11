package model;

import java.util.List;

/**
 * Interface for all classes representing a player for the game Rummikub. Each player has his own
 * rack from which he can set tiles to the board. A player can also pull a tile from the pool.
 */
public interface Player {

  /**
   * Returns the player's rack.
   */
  List<Tile> getRack();

  /**
   * Returns the tile at the specified index of the player's rack and removes it from the player's
   * rack.
   * 
   * <p>Note: This method only removes a tile from the player's rack and returns it. In order to set
   * it to the board the tile must be temporarily saved and used within the method
   * {@link Rummikub#setTile(int, int, Tile)}, for example:
   * 
   * <p>{@code game.setTile(x, y, player.getTileFromRack(index))}
   * 
   * @param index the index of the tile (counted from left to right)
   * @return the tile at {@code index}
   * @throws IndexOutOfBoundsException if the specified index is out of range
   */
  Tile getTileFromRack(int index);

  /**
   * Pulls a tile from the pool and adds it to the player's rack.
   * 
   * <p><strong>Note:</strong> After any invocation of this method the round of this player should
   * be immediately finished otherwise this player is able to illegally alter the game's state.
   * 
   * @param t the tile to be added to the player's rack
   */
  void pullTileFromPool(Tile t);

  /**
   * Sorts the rack by colors and the values in ascending order.
   */
  void sortRack();

  /**
   * Returns the player's name.
   */
  String getName();

  /**
   * Returns the player's age.
   */
  int getAge();

  /**
   * Resets this player. The rack and set tiles will be emptied everything else will not change.
   * This should only be called for restarting a new game.
   */
  void reset();

  /**
   * Returns the minus points of the players current rack.
   */
  int getMinusPoints();

}
