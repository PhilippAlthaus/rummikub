package model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A sequence consists of a fixed set of tiles. A sequence can either be valid or invalid by the
 * game's rules. This class is responsible for validating a sequence, a sequence should only be
 * created for validation and be deleted afterwards.
 * 
 * @see <a href="http://rummikub.com/wp-content/uploads/2017/01/2600-English-manual.pdf">Rummikub
 *      rules</a>
 */
class Sequence {

  /**
   * Minimal number of tiles for a sequence to be valid.
   */
  private static final int MINIMAL_SIZE = 3;

  /**
   * Maximum size of a group.
   */
  private static final int MAXIMUM_GROUP_SIZE = 4;

  /**
   * Maximum size of a run.
   */
  private static final int MAXIMUM_RUN_SIZE = 13;

  /**
   * The highest value a run can end with.
   */
  private static final int MAXIMUM_TILE_VALUE_FOR_RUNS = 13;

  /**
   * The tiles in this sequence.
   */
  private final List<Tile> tileSet;

  /**
   * Initializes an empty sequence.
   */
  private Sequence() {
    tileSet = new LinkedList<>();
  }

  /**
   * Returns a new empty sequence.
   */
  static Sequence create() {
    return new Sequence();
  }

  /**
   * Returns a copy of the sequence specified tile.
   * 
   * @param other the sequence to be copied
   * @return the copy of the original sequence
   */
  static Sequence copyOf(Sequence other) {
    Sequence copy = Sequence.create();
    for (Tile tile : other.tileSet) {
      copy.addTile(RummikubTile.copyOf((RummikubTile) tile));
    }
    return copy;
  }

  /**
   * Adds a tile to this sequence.
   * 
   * @param t the tile to be added
   */
  void addTile(Tile t) {
    tileSet.add(t);
  }

  /**
   * Returns {@code true} if this {@code Sequence} is empty.
   */
  boolean isEmpty() {
    return tileSet.isEmpty();
  }

  /**
   * Marks all tiles in this sequence as false.
   */
  void markAsFalse() {
    tileSet.forEach(tile -> ((RummikubTile) tile).markAsFalse());
  }

  /**
   * Returns {@code true} if this sequence is valid i.e., it is a either a run or a group.
   */
  boolean isValid(Player currentPlayer) {
    if (!((RummikubPlayer) currentPlayer).isFirstMoveDone()) {
      if (!isCorrectFirstMove(currentPlayer)) {
        // during the first move the player is not allowed to use tiles already set on the board by
        // other players
        return false;
      }
    }
    return isRun() || isGroup();
  }

  /**
   * Returns {@code true} if this sequence is a run. A run consists of at least three tiles of the
   * same color and the values have to be successive and in ascending order (e.g. 4, 5, 6).
   */
  private boolean isRun() {
    if (!hasMinimalSize() || tileSet.size() > MAXIMUM_RUN_SIZE) {
      return false;
    }
    // ensure the tile to start with is no joker as a joker has neither color nor value so
    // comparisons won't work
    Tile firstTileWithValue = getFirstTileThatIsNoJoker();
    Color colorOfRun = firstTileWithValue.getColor();
    int value = firstTileWithValue.getValue();
    int indexOfFirstValue = tileSet.indexOf(firstTileWithValue);

    if (indexOfFirstValue != 0) {
      for (int i = indexOfFirstValue - 1; i >= 0; i--) {
        // set previous joker values
        int valueTemp = value;
        ((RummikubTile) tileSet.get(i)).setJokerValue(--valueTemp);
      }
    }

    for (int i = indexOfFirstValue + 1; i < tileSet.size(); i++) {
      Tile currentTile = tileSet.get(i);

      if (value < i) {
        // the sequence started with a joker and currentTile has a lower value than its
        // index/position in the sequence e.g., [Joker, 1, 2] is false as the Joker would count as 0
        // which is forbidden
        return false;
      } else if (currentTile.isJoker()) {
        // set the value of the joker to the value it is supposed to be
        // it is possible that this method will set values that are greater than 13, but this has no
        // impact as the game is not valid in this case, thus the value is not saved
        ((RummikubTile) currentTile).setJokerValue(++value);
      } else {
        Color currentColor = currentTile.getColor();
        int currentValue = currentTile.getValue();

        if (currentColor == colorOfRun && currentValue == value + 1) {
          value++;
        } else {
          return false;
        }
      }
    }
    if (value > MAXIMUM_TILE_VALUE_FOR_RUNS) {
      // the sequence ends with a Joker and this Joker would count as a value that is too high
      // e.g., [12, 13, Joker]
      return false;
    }
    return true;
  }

  /**
   * Returns {@code true} if this sequence is a group. A group consists of three to four tiles and
   * their values have to be identical (e.g. 4, 4, 4). There cannot be two tiles of the same color
   * in a group.
   */
  private boolean isGroup() {
    if (!hasMinimalSize() || tileSet.size() > MAXIMUM_GROUP_SIZE) {
      return false;
    }
    // ensure the tile to start with is no joker as a joker has neither color nor value so
    // comparisons won't work
    Tile firstTileWithValue = getFirstTileThatIsNoJoker();
    Set<Color> usedColors = new HashSet<>();
    usedColors.add(firstTileWithValue.getColor());
    int valueOfGroup = firstTileWithValue.getValue();
    int indexOfFirstValue = tileSet.indexOf(firstTileWithValue);

    if (indexOfFirstValue != 0) {
      for (int i = 0; i < indexOfFirstValue; i++) {
        // set previous joker values
        ((RummikubTile) tileSet.get(i)).setJokerValue(valueOfGroup);
      }
    }

    for (int i = indexOfFirstValue + 1; i < tileSet.size(); i++) {
      Tile currentTile = tileSet.get(i);

      if (currentTile.isJoker()) {
        // set the value of the joker to the value it is supposed to be
        // it is possible that this method will set values that are greater than 13, but this has no
        // impact as the game is not valid in this case, thus the value is not saved
        ((RummikubTile) currentTile).setJokerValue(valueOfGroup);
      } else {
        Color currentColor = currentTile.getColor();
        int currentValue = currentTile.getValue();

        if ((!usedColors.contains(currentColor) && currentValue == valueOfGroup)) {
          usedColors.add(currentColor);
        } else {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns {@code true} if this sequence has the required minimal size of three tiles.
   */
  private boolean hasMinimalSize() {
    return tileSet.size() >= MINIMAL_SIZE;
  }

  /**
   * Returns {@code true} if the player {@code p} made a valid first move. During the first move it
   * is not allowed to use sequences already set to the board by other players.
   */
  private boolean isCorrectFirstMove(Player p) {
    return allTilesByPlayer(p) || allTilesNotByPlayer(p);
  }

  /**
   * Returns {@code true} if the entire sequence was not set by the player {@code p}.
   */
  private boolean allTilesNotByPlayer(Player p) {
    Set<Tile> playerTiles = ((RummikubPlayer) p).getSetTiles();
    for (Tile t : tileSet) {
      if (playerTiles.contains(t)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns {@code true} if the entire sequence was set by the player {@code p}.
   */
  private boolean allTilesByPlayer(Player p) {
    Set<Tile> playerTiles = ((RummikubPlayer) p).getSetTiles();
    for (Tile t : tileSet) {
      if (!playerTiles.contains(t)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the first tile in this sequence that is no joker.
   */
  private Tile getFirstTileThatIsNoJoker() {
    assert tileSet.size() >= MINIMAL_SIZE;

    for (Tile t : tileSet) {
      if (!t.isJoker()) {
        return t;
      }
    }
    // unreachable as there are only two jokers in the game and tileSet.size() > 2 at this point
    throw new AssertionError();
  }

  @Override
  public String toString() {
    return tileSet.toString();
  }

}
