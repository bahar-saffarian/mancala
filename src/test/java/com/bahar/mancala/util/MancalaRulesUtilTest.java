package com.bahar.mancala.util;

import com.bahar.mancala.helper.BoardTestHelper;
import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Player;
import com.bahar.mancala.model.Stone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @Test
    void whoIsTheWinner() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player winnerPlayer = board.getTurn();

        board.getPits().stream().filter(pit -> !pit.isMancala()).forEach(pit -> {
            Set<Stone> stones = pit.getStones();
            stones.forEach(stone -> stone.setPit(winnerPlayer.getMancala()));
            winnerPlayer.getMancala().setStones(stones);
            pit.setStones(new HashSet<>());
        });

        //When
        List<Player> winners = MancalaRulesUtil.getWinners(board);

        //Then
        assertThat(winners.size()).isEqualTo(1);
        assertThat(winners.get(0).getName()).isEqualTo(winnerPlayer.getName());

    }

    @Test
    void bothWinWhenThePointsAreEqual() {
        //Given
        Board board = initializeTowPlayerBoard();

        board.getPlayers().forEach(player ->
                board.getPits().stream()
                        .filter(pit -> !pit.isMancala() && pit.getOwner().getName().equals(player.getName()))
                        .forEach(pit -> {
                            Set<Stone> stones = pit.getStones();
                            stones.forEach(stone -> stone.setPit(player.getMancala()));
                            player.getMancala().setStones(stones);
                            pit.setStones(new HashSet<>());
                        })
        );


        //When
        List<Player> winners = MancalaRulesUtil.getWinners(board);

        //Then
        assertThat(winners.size()).isEqualTo(board.getPlayers().size());
        assertThat(winners.stream().map(Player::getName).toList()
                    .containsAll(board.getPlayers().stream().map(Player::getName).toList()))
                .isTrue();

    }
}
