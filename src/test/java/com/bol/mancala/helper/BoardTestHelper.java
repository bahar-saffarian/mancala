package com.bol.mancala.helper;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PitRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.repository.StoneRepository;
import com.bol.mancala.service.BoardServiceImpl;
import com.bol.mancala.service.PitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BoardTestHelper {
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PitService pitService;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PitRepository pitRepository;
    @Mock
    private StoneRepository stoneRepository;

    private final int defaultNumberOfEachPlayerPits = 6;
    private final int defaultNumberOfEachPitStones = 6;
    private BoardServiceImpl boardService;

    public void initializeBoardService() {
        boardService = new BoardServiceImpl(boardRepository, playerRepository, pitRepository, stoneRepository, pitService, defaultNumberOfEachPlayerPits, defaultNumberOfEachPitStones);
    }

    public Board initializeTowPlayerBoard() {
        Set<Player> players = Set.of(
                new Player("A"),
                new Player("B")
        );
        Integer eachPlayerPitNum = 6;
        Integer eachPitStoneNum = 6;

        return boardService.initiateGameBoard(players, eachPlayerPitNum, eachPitStoneNum);
    }
}
