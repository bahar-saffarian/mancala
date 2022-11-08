package com.bahar.mancala.service;

import com.bahar.mancala.dto.LastStoneSowResult;
import com.bahar.mancala.dto.SowResult;
import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Pit;
import com.bahar.mancala.model.Stone;

import java.util.Set;

public interface PitService {
    SowResult sow(Stone stone, Pit pit);

    LastStoneSowResult sowLastStone(Stone stone, Pit pit);

    void sowCapturedStonesInMancala(Set<Stone> stones, Pit mancalaPit);

    Set<Stone> pickupStonesFromPit(int pitIndex, Board board);
}
