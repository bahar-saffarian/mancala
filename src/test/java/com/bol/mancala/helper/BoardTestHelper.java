package com.bol.mancala.helper;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.service.BoardServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class BoardTestHelper {
    @Mock
    private static BoardRepository boardRepository;

    private final static int defaultNumberOfEachPlayerPits = 6;
    private final static int defaultNumberOfEachPitStones = 6;
    private final static BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, defaultNumberOfEachPlayerPits, defaultNumberOfEachPitStones);

    public static Board initializeTowPlayerBoard() {
        Set<Player> players = Set.of(
                new Player("A"),
                new Player("B")
        );
        Integer eachPlayerPitNum = 6;
        Integer eachPitStoneNum = 6;

        return boardService.initiateGameBoard(players, eachPlayerPitNum, eachPitStoneNum);
    }
}
