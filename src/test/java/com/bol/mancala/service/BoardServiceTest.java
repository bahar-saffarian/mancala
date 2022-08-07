package com.bol.mancala.service;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Player;
import com.bol.mancala.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class BoardServiceTest {

    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PitService pitService;

    @Value("${board.numberOfEachPlayerPits}") private int defaultNumberOfEachPlayerPits;
    @Value("${board.numberOfEachPitStones}") private int defaultNumberOfEachPitStones;

    @BeforeEach
    void setUp() {
        boardService = new BoardServiceImpl(boardRepository, pitService, defaultNumberOfEachPlayerPits, defaultNumberOfEachPitStones);
    }

    @Test
    void canInitiateBoard() {
        //given
        Set<Player> players = Set.of(
                new Player("A"),
                new Player("B")
        );
        int eachPlayerPitNum = 6;
        int eachPitStoneNum = 6;

        //when
        Board board = boardService.initiateGameBoard(players, eachPlayerPitNum, eachPitStoneNum);

        //then
        assertThat(board).isNotNull();
        assertThat(board.getPits().size()).isEqualTo(players.size()*(eachPlayerPitNum+1));

        IntStream.range(0, board.getPits().size()).forEach(index -> assertThat(board.getPits().get(index).getPitIndexInBoard()).isEqualTo(index));
        board.getPits().stream().filter(pit -> !pit.isMankala()).forEach(pit -> assertThat(pit.getStones().size()).isEqualTo(eachPitStoneNum));
        board.getPits().stream().filter(Pit::isMankala).forEach(pit -> assertThat(pit.getStones().size()).isEqualTo(0));
        assertThat(board.getTurn()).isNotNull();

        players.forEach(player -> assertThat(player.getMankala()).isNotNull());
        players.forEach(player -> assertThat(player.getFirstPitIndex()).isIn(0, eachPlayerPitNum+1)); // Plus one is for Mankala pit
        players.forEach(player -> assertThat(player.getPitCount()).isEqualTo(eachPitStoneNum));

        assertThat(players.contains(board.getTurn())).isTrue();
    }

    @Test
    void canInitiateBoarWithDefaultProperties() {
        //given
        Set<Player> players = Set.of(
                new Player("A"),
                new Player("B")
        );

        //when
        Board board = boardService.initiateGameBoard(players, null, null);

        //then
        assertThat(board).isNotNull();
        assertThat(board.getPits().size()).isGreaterThan(players.size()*2);

        IntStream.range(0, board.getPits().size()).forEach(index -> assertThat(board.getPits().get(index).getPitIndexInBoard()).isEqualTo(index));
        board.getPits().stream().filter(pit -> !pit.isMankala()).forEach(pit -> assertThat(pit.getStones().size()).isGreaterThan(players.size()));
        board.getPits().stream().filter(Pit::isMankala).forEach(pit -> assertThat(pit.getStones().size()).isEqualTo(0));
        assertThat(board.getTurn()).isNotNull();

        players.forEach(player -> assertThat(player.getMankala()).isNotNull());
        players.forEach(player -> assertThat(player.getPitCount()).isGreaterThan(1));

        assertThat(players.contains(board.getTurn())).isTrue();
    }
}
