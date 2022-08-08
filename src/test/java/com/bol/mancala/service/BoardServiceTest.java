package com.bol.mancala.service;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Player;
import com.bol.mancala.model.Stone;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PitRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.repository.StoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class BoardServiceTest {

    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PitRepository pitRepository;
    @Mock
    private StoneRepository stoneRepository;
    @Autowired
    private PitService pitService;

    @Value("${board.numberOfEachPlayerPits}") private int defaultNumberOfEachPlayerPits;
    @Value("${board.numberOfEachPitStones}") private int defaultNumberOfEachPitStones;

    @BeforeEach
    void setUp() {
        boardService = new BoardServiceImpl(boardRepository, playerRepository, pitRepository, stoneRepository, pitService, defaultNumberOfEachPlayerPits, defaultNumberOfEachPitStones);
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
        board.getPits().stream().filter(pit -> !pit.isMancala()).forEach(pit -> assertThat(pit.getStones().size()).isEqualTo(eachPitStoneNum));
        board.getPits().stream().filter(Pit::isMancala).forEach(pit -> assertThat(pit.getStones().size()).isEqualTo(0));
        assertThat(board.getTurn()).isNotNull();

        players.forEach(player -> assertThat(player.getMancala()).isNotNull());
        players.forEach(player -> assertThat(player.getFirstPitIndex()).isIn(0, eachPlayerPitNum+1)); // Plus one is for Mancala pit
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
        board.getPits().stream().filter(pit -> !pit.isMancala()).forEach(pit -> assertThat(pit.getStones().size()).isGreaterThan(players.size()));
        board.getPits().stream().filter(Pit::isMancala).forEach(pit -> assertThat(pit.getStones().size()).isEqualTo(0));
        assertThat(board.getTurn()).isNotNull();

        players.forEach(player -> assertThat(player.getMancala()).isNotNull());
        players.forEach(player -> assertThat(player.getPitCount()).isGreaterThan(1));

        assertThat(players.contains(board.getTurn())).isTrue();
    }

    //TODO    moveAllRemainStonesToTheOwnersMancala
    @Test
    void moveAllRemainStonesToTheOwnersMancala() {
        //Given
        final Random rand = new Random();
        Set<Player> players = Set.of(
                new Player("A"),
                new Player("B")
        );
        Board board = boardService.initiateGameBoard(players, null, null);
        int totalScore = board.getPits().stream().map(pit -> pit.getStones().size()).reduce(0, Integer::sum);//all stones count
        IntStream.range(0, 4).forEach(i -> { //Do some movement randomly
                    Pit pit = board.getPits().get(rand.nextInt(board.getPits().size()));
                    if (!pit.isMancala()) {
                        Set<Stone> stones = pit.getStones();
                        pit.setStones(new HashSet<>());
                        Pit mancala = players.stream().toList().get(rand.nextInt(players.size())).getMancala();
                        stones.forEach(stone -> stone.setPit(mancala));
                        mancala.getStones().addAll(stones);
                    }
                }
        );

        //When
        boardService.moveAllRemainStonesToTheOwnersMancala(board);

        //Then
        assertThat(board.getPits().stream()
                .filter(Pit::isMancala).map(pit -> pit.getStones().size()).reduce(0, Integer::sum))
                .isEqualTo(totalScore); //Sum of stones in mancala is equal to total some
        assertThat(board.getPits().stream()
                .filter(pit -> !pit.isMancala())
                .map(pit -> pit.getStones().size()).reduce(0, Integer::sum)).isEqualTo(0); //All normal pit are empty
    }

    @Test
    void determineWinnerIfTheGameIsOver() {
        //Given
        Set<Player> players = Set.of(
                new Player("A"),
                new Player("B")
        );
        Board board = boardService.initiateGameBoard(players, null, null);
        Player ownerPlayer = board.getTurn();
        board.getPits().stream() //Move all owner stones to the owner mancala
                .filter(pit -> !pit.isMancala() && pit.getOwner().getName().equals(ownerPlayer.getName()))
                .forEach(pit -> {
                    Set<Stone> stones = pit.getStones();
                    pit.setStones(new HashSet<>());
                    Pit mancala = ownerPlayer.getMancala();
                    stones.forEach(stone -> stone.setPit(mancala));
                    mancala.getStones().addAll(stones);
                });
        //Move all stones of a pit of other to owner player mancala so that the winner will be the owner player
        Pit oneOfTheOtherPlayerPit = board.getPits().stream()
                .filter(pit -> !pit.isMancala() && !pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
        Set<Stone> otherPlayerStones = oneOfTheOtherPlayerPit.getStones();
        oneOfTheOtherPlayerPit.setStones(new HashSet<>());
        Pit ownerMancala = ownerPlayer.getMancala();
        otherPlayerStones.forEach(stone -> stone.setPit(ownerMancala));
        ownerMancala.getStones().addAll(otherPlayerStones);

        int totalOwnerStones = board.getPits().stream()
                .filter(pit -> pit.getOwner().getName().equals(ownerPlayer.getName()))
                .map(pit -> pit.getStones().size()).reduce(0, Integer::sum);
        int totalOtherStones = board.getPits().stream()
                .filter(pit -> !pit.getOwner().getName().equals(ownerPlayer.getName()))
                .map(pit -> pit.getStones().size()).reduce(0, Integer::sum);


        //When
        boardService.determineWinnerIfTheGameIsOver(board);

        //Then
        assertThat(board.isFinished()).isTrue();
        assertThat(board.getWinner().size()).isEqualTo(1);
        assertThat(board.getWinner().get(0).getName()).isEqualTo(ownerPlayer.getName());
        assertThat(ownerPlayer.getMancala().getStones().size()).isEqualTo(totalOwnerStones);
        assertThat(ownerPlayer.getNextPlayer().getMancala().getStones().size()).isEqualTo(totalOtherStones);

    }

}
