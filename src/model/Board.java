package model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The board is a grid, each position has either a tile or is empty. Tiles on the board can be
 * accessed by their Cartesian coordinate.
 */
public class Board implements Serializable {

  private static final long serialVersionUID = 7188850072671702009L;

  /**
   * The width of this board.
   */
  private static final int WIDTH = 22;

  /**
   * The height of this board.
   */
  private static final int HEIGHT = 8;

  /**
   * Coordinate system to save the positions of tiles on the board.
   */
  private Tile[][] grid;

  /**
   * Initializes a new {@code Board}.
   */
  private Board() {
    this.grid = new Tile[WIDTH][HEIGHT];
  }

  /**
   * Returns a new {@code Board}.
   */
  static Board create() {
    return new Board();
  }

  /**
   * Sets the given {@code Tile} at the specified position to the board.
   *
   * @param x the X-coordinate of the position
   * @param y the Y-coordinate of the position
   * @param tile the tile to be set to the board
   * @return {@code true} if the tile could be set
   * @throws IndexOutOfBoundsException if the specified position is out of range
   */
  boolean setTile(int x, int y, Tile tile) {
    if (this.grid[x][y] != null) {
      return false;
    }
    this.grid[x][y] = tile;
    return true;
  }

  /**
   * Deletes the {@code Tile} at the specified position.
   *
   * @param x the X-coordinate of the position
   * @param y the Y-coordinate of the position
   * @throws IndexOutOfBoundsException if the specified position is out of range
   */
  void removeTile(int x, int y) {
    this.grid[x][y] = null;
  }

  /**
   * Returns the {@code Tile} at the specified position. If the specified position holds no
   * {@code Tile} {@code null} is returned instead.
   *
   * @param x the X-coordinate of the position
   * @param y the Y-coordinate of the position
   * @throws IndexOutOfBoundsException if the specified position is out of range
   */
  Tile getTile(int x, int y) {
    return this.grid[x][y];
  }

  /**
   * Returns the width of this board.
   */
  int getWidth() {
    return grid.length;
  }

  /**
   * Returns the height of this board.
   */
  int getHeight() {
    return grid[0].length;
  }

  /**
   * Returns a list of all sequences on this board.
   */
  List<Sequence> identifySequences() {
    List<Sequence> sequencesOnBoard = new LinkedList<>();
    for (int i = 0; i < getHeight(); i++) {
      Sequence sequenceTemp = Sequence.create();
      for (int j = 0; j < getWidth(); j++) {

        Tile current = grid[j][i];
        if (current == null) {
          if (!sequenceTemp.isEmpty()) {
            // the end of a sequence is reached, thus it has to be added to the list
            sequencesOnBoard.add(sequenceTemp);
            sequenceTemp = Sequence.create();
          }
        } else {
          // current != null, the current position holds a tile
          sequenceTemp.addTile(current);
        }
      }
      if (!sequenceTemp.isEmpty()) {
        // the last position at the row (index getWidth - 1) holds a tile and is thus part of a
        // sequence
        sequencesOnBoard.add(sequenceTemp);
      }
    }
    return sequencesOnBoard;
  }

  /**
   * Removes the markings for all tiles on this board.
   */
  void removeTileMarkings() {
    for (int i = 0; i < getHeight(); i++) {
      for (int j = 0; j < getWidth(); j++) {
        Tile current = grid[j][i];
        if (current != null) {
          ((RummikubTile) current).removeMarking();
        }
      }
    }
  }

  /**
   * Returns a copy of the specified board.
   * 
   * @param other the board to be copied
   * @return a copy of the original board
   */
  static Board copyOf(Board other) {
    Board copy = create();
    for (int i = 0; i < other.getHeight(); i++) {
      for (int j = 0; j < other.getWidth(); j++) {
        copy.setTile(j, i, (RummikubTile.copyOf((RummikubTile) other.getTile(j, i))));
      }
    }
    return copy;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < getHeight(); i++) {
      for (int j = 0; j < getWidth(); j++) {
        if (grid[j][i] != null) {
          s.append("(" + j + "," + i + "): " + grid[j][i] + "\n");
        }
      }
    }
    return s.toString();
  }

}
