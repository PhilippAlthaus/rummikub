package model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Sequence tests.")
class SequenceTest {

  private Player juergen;

  @BeforeEach
  void setUp() throws NoSuchFieldException, SecurityException {
    juergen = RummikubPlayer.of("Juergen", 17);
  }

  /**
   * Returns a stream of valid runs.
   */
  private static Stream<Arguments> provideValidRuns() {
    Tile blackNine = RummikubTile.createTile(Color.BLACK, 9);
    Tile blackTen = RummikubTile.createTile(Color.BLACK, 10);
    Tile blackEleven = RummikubTile.createTile(Color.BLACK, 11);
    Tile redOne = RummikubTile.createTile(Color.RED, 1);
    Tile redTwo = RummikubTile.createTile(Color.RED, 2);
    Tile redThree = RummikubTile.createTile(Color.RED, 3);
    Tile firstJoker = RummikubTile.createJoker();
    Tile secondJoker = RummikubTile.createJoker();

    Sequence validBlackRun = Sequence.create();
    validBlackRun.addTile(blackNine);
    validBlackRun.addTile(blackTen);
    validBlackRun.addTile(blackEleven);

    Sequence validBlackRunWithJokers = Sequence.create();
    validBlackRunWithJokers.addTile(blackEleven);
    validBlackRunWithJokers.addTile(firstJoker);
    validBlackRunWithJokers.addTile(secondJoker);

    Sequence validRedRun = Sequence.create();
    validRedRun.addTile(redOne);
    validRedRun.addTile(redTwo);
    validRedRun.addTile(redThree);

    Sequence validRedRunWithJoker = Sequence.create();
    validRedRunWithJoker.addTile(firstJoker);
    validRedRunWithJoker.addTile(redTwo);
    validRedRunWithJoker.addTile(redThree);

    return Stream.of(Arguments.of(validBlackRun), Arguments.of(validBlackRunWithJokers),
        Arguments.of(validRedRun), Arguments.of(validRedRunWithJoker));
  }

  @ParameterizedTest(name = "{index}: isValid({0}) == true")
  @DisplayName("Test valid runs.")
  @MethodSource("provideValidRuns")
  void testIsValidForValidRuns(Sequence s) {
    assertTrue(s.isValid(juergen));
  }

  /**
   * Returns a stream of invalid runs.
   */
  private static Stream<Arguments> provideInvalidRuns() {
    Tile orangeOne = RummikubTile.createTile(Color.ORANGE, 1);
    Tile orangeTwo = RummikubTile.createTile(Color.ORANGE, 2);
    Tile blueThree = RummikubTile.createTile(Color.BLUE, 3);
    Tile blueTwelve = RummikubTile.createTile(Color.BLUE, 12);
    Tile blueThirteen = RummikubTile.createTile(Color.BLUE, 13);
    Tile firstJoker = RummikubTile.createJoker();

    Sequence invalidOrangeRun = Sequence.create();
    invalidOrangeRun.addTile(orangeOne);
    invalidOrangeRun.addTile(orangeTwo);
    invalidOrangeRun.addTile(blueThree);

    Sequence invalidOrangeRunWithJoker = Sequence.create();
    invalidOrangeRunWithJoker.addTile(firstJoker);
    invalidOrangeRunWithJoker.addTile(orangeOne);
    invalidOrangeRunWithJoker.addTile(orangeTwo);

    Sequence invalidBlueRunWithJoker = Sequence.create();
    invalidBlueRunWithJoker.addTile(blueTwelve);
    invalidBlueRunWithJoker.addTile(blueThirteen);
    invalidBlueRunWithJoker.addTile(firstJoker);

    Sequence invalidBlueRun = Sequence.create();
    invalidBlueRun.addTile(blueTwelve);
    invalidBlueRun.addTile(blueTwelve);

    return Stream.of(Arguments.of(invalidOrangeRun), Arguments.of(invalidOrangeRunWithJoker),
        Arguments.of(invalidBlueRunWithJoker), Arguments.of(invalidBlueRun));
  }

  @ParameterizedTest(name = "{index}: isValid({0}) == false")
  @DisplayName("Test invalid runs.")
  @MethodSource("provideInvalidRuns")
  void testIsValidForInvalidRuns(Sequence s) {
    assertFalse(s.isValid(juergen));
  }

  /**
   * Returns a stream of valid groups.
   */
  private static Stream<Arguments> provideValidGroups() {
    Tile blackNine = RummikubTile.createTile(Color.BLACK, 9);
    Tile redNine = RummikubTile.createTile(Color.RED, 9);
    Tile orangeNine = RummikubTile.createTile(Color.ORANGE, 9);
    Tile blackThirteen = RummikubTile.createTile(Color.BLACK, 13);
    Tile redThirteen = RummikubTile.createTile(Color.RED, 13);
    Tile orangeThirteen = RummikubTile.createTile(Color.ORANGE, 13);
    Tile blueThirteen = RummikubTile.createTile(Color.BLUE, 13);
    Tile firstJoker = RummikubTile.createJoker();
    Tile secondJoker = RummikubTile.createJoker();

    Sequence validNineGroup = Sequence.create();
    validNineGroup.addTile(blackNine);
    validNineGroup.addTile(redNine);
    validNineGroup.addTile(orangeNine);

    Sequence validNineGroupWithJokers = Sequence.create();
    validNineGroupWithJokers.addTile(blackNine);
    validNineGroupWithJokers.addTile(firstJoker);
    validNineGroupWithJokers.addTile(secondJoker);

    Sequence validThirteenGroup = Sequence.create();
    validThirteenGroup.addTile(blackThirteen);
    validThirteenGroup.addTile(redThirteen);
    validThirteenGroup.addTile(orangeThirteen);
    validThirteenGroup.addTile(blueThirteen);

    Sequence validThirteenGroupWithJoker = Sequence.create();
    validThirteenGroupWithJoker.addTile(firstJoker);
    validThirteenGroupWithJoker.addTile(redThirteen);
    validThirteenGroupWithJoker.addTile(blueThirteen);
    validThirteenGroupWithJoker.addTile(blackThirteen);

    return Stream.of(Arguments.of(validNineGroup), Arguments.of(validNineGroupWithJokers),
        Arguments.of(validThirteenGroup), Arguments.of(validThirteenGroupWithJoker));
  }

  @ParameterizedTest(name = "{index}: isValid({0}) == true")
  @DisplayName("Test valid groups.")
  @MethodSource("provideValidGroups")
  void testIsValidForValidGroups(Sequence s) {
    assertTrue(s.isValid(juergen));
  }

  /**
   * Returns a stream of invalid groups.
   */
  private static Stream<Arguments> provideInvalidGroups() {
    Tile blackNine = RummikubTile.createTile(Color.BLACK, 9);
    Tile redNine = RummikubTile.createTile(Color.RED, 9);
    Tile blackThirteen = RummikubTile.createTile(Color.BLACK, 13);
    Tile redThirteen = RummikubTile.createTile(Color.RED, 13);
    Tile blueThirteen = RummikubTile.createTile(Color.BLUE, 13);
    Tile firstJoker = RummikubTile.createJoker();
    Tile secondJoker = RummikubTile.createJoker();

    Sequence invalidNineGroup = Sequence.create();
    invalidNineGroup.addTile(blackNine);
    invalidNineGroup.addTile(redNine);
    invalidNineGroup.addTile(redThirteen);

    Sequence invalidNineGroupWithJoker = Sequence.create();
    invalidNineGroupWithJoker.addTile(blackNine);
    invalidNineGroupWithJoker.addTile(firstJoker);
    invalidNineGroupWithJoker.addTile(redThirteen);

    Sequence invalidThirteenGroup = Sequence.create();
    invalidThirteenGroup.addTile(blackThirteen);
    invalidThirteenGroup.addTile(redThirteen);
    invalidThirteenGroup.addTile(redThirteen);

    Sequence invalidThirteenGroupWithJokers = Sequence.create();
    invalidThirteenGroupWithJokers.addTile(firstJoker);
    invalidThirteenGroupWithJokers.addTile(redThirteen);
    invalidThirteenGroupWithJokers.addTile(blueThirteen);
    invalidThirteenGroupWithJokers.addTile(blackThirteen);
    invalidThirteenGroupWithJokers.addTile(secondJoker);

    return Stream.of(Arguments.of(invalidNineGroup), Arguments.of(invalidNineGroupWithJoker),
        Arguments.of(invalidThirteenGroup), Arguments.of(invalidThirteenGroupWithJokers));
  }

  @ParameterizedTest(name = "{index}: isValid({0}) == false")
  @DisplayName("Test invalid groups.")
  @MethodSource("provideInvalidGroups")
  void testIsValidForInvalidGroups(Sequence s) {
    assertFalse(s.isValid(juergen));
  }

}
