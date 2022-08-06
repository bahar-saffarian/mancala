package com.bol.mancala.util;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.bol.mancala.util.PlayBoardValidator.*;

public interface PlayBoardValidator extends Function<Board, List<ValidationResult>> {
    enum ValidationResult {
        INDEX_NOT_IN_BOARD_RANGE,
        MANKALA_IS_NOT_ACCEPTABLE_AS_START_POINT,
        VIOLATE_PLAYER_TURN,
        START_PIT_IS_EMPTY,
    }

    static PlayBoardValidator validateIndexInRange(int startIndex) {
        return board ->
                startIndex < board.getPits().size() ?
                        List.of() : List.of(ValidationResult.INDEX_NOT_IN_BOARD_RANGE);
    }

    static PlayBoardValidator validateNotSelectMankalaAsStartPoint(int startIndex) {
        return board ->
                board.getPits().get(startIndex).isMankala() ?
                        List.of(ValidationResult.MANKALA_IS_NOT_ACCEPTABLE_AS_START_POINT) : List.of();
    }

    static PlayBoardValidator validateToNotViolatePlayerTurn(int startIndex) {
        return board ->
                board.getPits().get(startIndex).getOwner().getName().equals(board.getTurn().getName()) ?
                        List.of() : List.of(ValidationResult.VIOLATE_PLAYER_TURN);
    }

    static PlayBoardValidator validateNotSelectEmptyPitAsStartPoint(int startIndex) {
        return board ->
                board.getPits().get(startIndex).getStones().size() > 0 ?
                        List.of() : List.of(ValidationResult.START_PIT_IS_EMPTY);
    }

    default PlayBoardValidator and(PlayBoardValidator other) {
        return board -> {
            List<ValidationResult> validationResults = new ArrayList<>(this.apply(board));
            if (!validationResults.contains(ValidationResult.INDEX_NOT_IN_BOARD_RANGE)) {
                List<ValidationResult> otherValidationResults = other.apply(board);
                validationResults.addAll(otherValidationResults);
            }

            return validationResults;
        };
    }

    static PlayBoardValidator validatePlayFromPit(int pitIndex) {

        return validateIndexInRange(pitIndex).and(
                validateNotSelectEmptyPitAsStartPoint(pitIndex)).and(
                validateNotSelectMankalaAsStartPoint(pitIndex)).and(
                validateToNotViolatePlayerTurn(pitIndex));
    }
}
