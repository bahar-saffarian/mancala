package com.bol.mancala.util;

import com.bol.mancala.helper.BoardTestHelper;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PlayBoardValidatorTest extends BoardTestHelper {

    @BeforeEach
    void setUp() {
        initializeBoardService();
    }

    @Test
    void gameOverValidationTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        board.setFinished(true);
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validateGameOver().apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.THE_GAME_IS_OVER)).isTrue();
    }

    @Test
    void inBoundRangeTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validateIndexInRange(0).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.isEmpty()).isTrue();
    }

    @Test
    void outOfBoundRangeTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validateIndexInRange(board.getPits().size()).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.INDEX_NOT_IN_BOARD_RANGE)).isTrue();
    }

    @Test
    void startFromEmptyPitTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        board.getPits().get(0).setStones(new HashSet<>());
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validateNotSelectEmptyPitAsStartPoint(0).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.START_PIT_IS_EMPTY)).isTrue();
    }

    @Test
    void startFromMancalaTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        int MancalaPitIndex = board.getPits().stream().filter(Pit::isMancala).findFirst().get().getPitIndexInBoard();
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validateNotSelectMancalaAsStartPoint(MancalaPitIndex).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.MANCALA_IS_NOT_ACCEPTABLE_AS_START_POINT)).isTrue();
    }

    @Test
    void startFromOutOfTurnPlayerPit() {
        Board board = initializeTowPlayerBoard();
        Player inTurnPlayer = board.getTurn();
        int outOfTurnPlayerPitIndex = board.getPits().stream().filter(pit -> !pit.getOwner().getName().equals(inTurnPlayer.getName())).findFirst().get().getPitIndexInBoard();
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validateToNotViolatePlayerTurn(outOfTurnPlayerPitIndex).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.VIOLATE_PLAYER_TURN)).isTrue();
    }

    @Test
    void validatePlayFromPitPositiveTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player inTurnPlayer = board.getTurn();
        int pitIndex = board.getPits().stream().filter(pit -> !pit.isMancala() && pit.getOwner().getName().equals(inTurnPlayer.getName())).findFirst().get().getPitIndexInBoard();
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validatePlayFromPit(pitIndex).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.isEmpty()).isTrue();
    }

    @Test
    void validatePlayFromOutOfBoundPitTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validatePlayFromPit(board.getPits().size()).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.INDEX_NOT_IN_BOARD_RANGE)).isTrue();
    }

    @Test
    void validatePlayPitWhenTheGameIsOverWhitPriorityTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        board.setFinished(true);
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validatePlayFromPit(board.getPits().size()).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.size()).isEqualTo(1);
        assertThat(errors.contains(PlayBoardValidator.ValidationResult.THE_GAME_IS_OVER)).isTrue();
    }

    @Test
    void validatePlayFromInBoundPitTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player inTurnPlayer = board.getTurn();
        int pitIndex = board.getPits().stream().filter(pit -> pit.isMancala() && !pit.getOwner().getName().equals(inTurnPlayer.getName())).findFirst().get().getPitIndexInBoard();
        board.getPits().get(pitIndex).setStones(new HashSet<>());
        //When
        List<PlayBoardValidator.ValidationResult> errors = PlayBoardValidator.validatePlayFromPit(pitIndex).apply(board);

        //Then
        assertThat(errors).isNotNull();
        assertThat(errors.containsAll(
                List.of(PlayBoardValidator.ValidationResult.MANCALA_IS_NOT_ACCEPTABLE_AS_START_POINT,
                        PlayBoardValidator.ValidationResult.START_PIT_IS_EMPTY,
                        PlayBoardValidator.ValidationResult.VIOLATE_PLAYER_TURN))
        ).isTrue();
    }
}
