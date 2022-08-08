package com.bol.mancala.util;

import com.bol.mancala.helper.BoardTestHelper;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MancalaRulesUtilTest extends BoardTestHelper {

    @BeforeEach
    void setUp() {
        initializeBoardService();
    }

    @Test
    void isTheGameOverInInitStateTest() {
        //Given
        Board board = initializeTowPlayerBoard();

        //When
        boolean theGaveOver = MancalaRulesUtil.isTheGaveOver(board);

        //Then
        assertThat(theGaveOver).isFalse();

    }

    @Test
    void isTheGameOverWhenNoStoneRemainTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        board.getPits().stream().filter(pit -> !pit.isMancala()).forEach(pit -> pit.setStones(new HashSet<>()));

        //When
        boolean theGaveOver = MancalaRulesUtil.isTheGaveOver(board);

        //Then
        assertThat(theGaveOver).isTrue();

    }

    @Test
    void isTheGameOverWhenOnePlayerAllPitsIsEmpty() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player player = board.getPlayers().stream().findFirst().get();
        board.getPits().stream().filter(pit -> !pit.isMancala() && Objects.equals(pit.getOwner().getName(), player.getName())).forEach(pit -> pit.setStones(new HashSet<>()));

        //When
        boolean theGaveOver = MancalaRulesUtil.isTheGaveOver(board);

        //Then
        assertThat(theGaveOver).isTrue();

    }

    @Test
    void isTheGameOverWhenOnePlayerSomePitsIsEmpty() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player player = board.getPlayers().stream().findFirst().get();
        board.getPits().stream()
                .filter(pit -> !pit.isMancala() && Objects.equals(pit.getOwner().getName(), player.getName()) && (pit.getPitIndexInBoard() % 2) == 0)
                .forEach(pit -> pit.setStones(new HashSet<>()));

        //When
        boolean theGaveOver = MancalaRulesUtil.isTheGaveOver(board);

        //Then
        assertThat(theGaveOver).isFalse();

    }
}
