package testing;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Player;
import model.Rummikub;
import model.RummikubGame;
import model.RummikubPlayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for RummikubGame.")
class RummikubGameTest {

  private Rummikub testGame;

  private Player olderPlayer;

  private Player youngerPlayer;

  @BeforeEach
  void setUp() {
    testGame = RummikubGame.create();
    olderPlayer = RummikubPlayer.of("Peter", 17);
    youngerPlayer = RummikubPlayer.of("Hans", 14);
    testGame.addPlayer(olderPlayer);
    testGame.addPlayer(youngerPlayer);
  }

  @DisplayName("Game creation.")
  @Test
  void testCreate() {
    assertNotNull(testGame);
  }

  @DisplayName("Player addition.")
  @Test
  void testAddPlayer() {
    assertTrue(testGame.addPlayer(olderPlayer));
    // getPlayers method or similar should be tested here after implementation
  }

  @DisplayName("Player addition for full game.")
  @Test
  void testAddPlayerForFullGame() {
    testGame.addPlayer(olderPlayer);
    testGame.addPlayer(olderPlayer);
    assertFalse(testGame.addPlayer(youngerPlayer));
  }

  @DisplayName("Player removal.")
  @Test
  void testRemovePlayer() {
    assertAll(
        () -> assertTrue(testGame.removePlayer(olderPlayer)),
        () -> assertFalse(testGame.getPlayers().contains(olderPlayer)));
  }

  @DisplayName("Player removal for unknown player.")
  @Test
  void testRemovePlayerForUnknownPlayer() {
    Player p = RummikubPlayer.of("Fritz", 7);
    assertFalse(testGame.removePlayer(p));
  }

  @DisplayName("Game start.")
  @Test
  void testStart() {
    testGame.start();
    assertAll(
        () -> assertEquals(testGame.getCurrentPlayer(), youngerPlayer),
        () -> assertEquals(youngerPlayer.getRack().size(), 14));
  }

  @DisplayName("Start with one player.")
  @Test
  void testStartWithOnePlayer() {
    Rummikub invalidGame = RummikubGame.create();
    invalidGame.addPlayer(olderPlayer);
    assertThrows(IllegalStateException.class, () -> invalidGame.start());
  }

  @DisplayName("Win conditions for player win.")
  @Test
  void testIsWon() {
    testGame.start();
    for (int i = 0; i < 14; i++) {
      youngerPlayer.getTileFromRack(0);
    }
    assertTrue(testGame.isWon());
  }

  @DisplayName("Win conditions for surrender.")
  @Test
  void testIsWonForSurrenders() {
    testGame.start();
    testGame.removePlayer(youngerPlayer);
    assertTrue(testGame.isWon());
  }

  @DisplayName("Win conditions for unfinished game.")
  @Test
  void testIsWonForUnfinishedGame() {
    testGame.start();
    assertFalse(testGame.isWon());
  }

  @DisplayName("Ending round.")
  @Test
  void testEndCurrentRound() {
    testGame.start();
    testGame.endCurrentRound();
    assertEquals(olderPlayer, testGame.getCurrentPlayer());
  }


  @DisplayName("Getting winner.")
  @Test
  void testGetWinner() {
    testGame.setWinner(olderPlayer);
    assertEquals(olderPlayer, testGame.getWinner());
  }

  @DisplayName("Getting for unfinished game.")
  @Test
  void testGetWinnerForUnfinishedGame() {
    testGame.start();
    assertThrows(IllegalStateException.class, () -> testGame.getWinner());
  }

}
