package model;

/**
 * An interface for all classes that represent a tile. A tile is either a normal tile with value and
 * color or a joker.
 */
public interface Tile {

  /**
   * Returns the color of this tile. Does not work with jokers i. e. {@code isJoker() == true}.
   * 
   * @throws IllegalArgumentException if this tile is a joker
   */
  Color getColor();

  /**
   * Returns the value of this tile. If this tile is a joker i.e., {@code isJoker() == true}, the
   * respective value the joker represents is returned.
   */
  int getValue();

  /**
   * Returns {@code true} if this tile is a joker.
   */
  boolean isJoker();

  /**
   * Returns {@code true} if this tile is part of a correct sequence on the board.
   */
  boolean isInCorrectSequence();

}
