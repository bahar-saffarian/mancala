package com.bahar.mancala.helper;

import com.bahar.mancala.repository.StoneRepository;
import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Player;
import com.bahar.mancala.repository.BoardRepository;
import com.bahar.mancala.repository.PitRepository;
import com.bahar.mancala.repository.PlayerRepository;
import com.bahar.mancala.service.BoardServiceImpl;
import com.bahar.mancala.service.PitService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

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
