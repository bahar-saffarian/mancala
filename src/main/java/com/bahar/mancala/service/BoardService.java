package com.bahar.mancala.service;

import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Player;

import java.util.Set;

public interface BoardService {
    Board initiateGameBoard(Set<Player> players, Integer numberOfPlayerPits, Integer numberOfPitStones);
    Board playFromPit(Long boardId, int pitIndex);

    Board getBoardById(Long boardId);
}
