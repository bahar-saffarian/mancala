package com.bol.mancala.util;

import com.bol.mancala.model.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.bol.mancala.util.PlayBoardValidator.*;

public interface PlayBoardValidator extends Function<Board, List<ValidationResult>> {
    enum ValidationResult {
        THE_GAME_IS_OVER,
        INDEX_NOT_IN_BOARD_RANGE,
        MANCALA_IS_NOT_ACCEPTABLE_AS_START_POINT,
        VIOLATE_PLAYER_TURN,
        START_PIT_IS_EMPTY,
    }

    static PlayBoardValidator validateGameOver() {
        return board -> board.isFinished() ? List.of(ValidationResult.THE_GAME_IS_OVER) : List.of();
    }
    static PlayBoardValidator validateIndexInRange(int startIndex) {
        return board ->
                startIndex < board.getPits().size() ?
                        List.of() : List.of(ValidationResult.INDEX_NOT_IN_BOARD_RANGE);
    }

    static PlayBoardValidator validateNotSelectMancalaAsStartPoint(int startIndex) {
        return board ->
                board.getPits().get(startIndex).isMancala() ?
                        List.of(ValidationResult.MANCALA_IS_NOT_ACCEPTABLE_AS_START_POINT) : List.of();
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
            if (!validationResults.contains(ValidationResult.INDEX_NOT_IN_BOARD_RANGE)
                    && !validationResults.contains(ValidationResult.THE_GAME_IS_OVER)) {
                List<ValidationResult> otherValidationResults = other.apply(board);
                validationResults.addAll(otherValidationResults);
            }

            return validationResults;
        };
    }

    static PlayBoardValidator validatePlayFromPit(int pitIndex) {

        return validateGameOver().and(
                validateIndexInRange(pitIndex)).and(
                validateNotSelectEmptyPitAsStartPoint(pitIndex)).and(
                validateNotSelectMancalaAsStartPoint(pitIndex)).and(
                validateToNotViolatePlayerTurn(pitIndex));
    }
}
