package com.bol.mancala.service;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;

import java.util.Set;

public interface BoardService {
    Board initiateGameBoard(Set<Player> players, Integer numberOfPlayerPits, Integer numberOfPitStones);
    Board playFromPit(Long boardId, int pitIndex);

    Board getBoardById(Long boardId);
}
