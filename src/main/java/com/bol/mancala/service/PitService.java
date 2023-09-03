package com.bol.mancala.service;

import com.bol.mancala.dto.LastStoneSowResult;
import com.bol.mancala.dto.SowResult;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Stone;

import java.util.Set;

public interface PitService {
    SowResult sow(Stone stone, Pit pit);

    LastStoneSowResult sowLastStone(Stone stone, Pit pit);

    void sowCapturedStonesInMancala(Set<Stone> stones, Pit mancalaPit);

    Set<Stone> pickupStonesFromPit(int pitIndex, Board board);
}