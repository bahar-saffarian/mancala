package com.bol.mancala.service;

import com.bol.mancala.dto.LastStoneSowResult;
import com.bol.mancala.dto.SowResult;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Stone;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PitServiceImpl implements PitService {
    @Override
    public SowResult sow(Stone stone, Pit pit) {
        SowResult sowResult;
        if (isOutOfTurnPlayerMancalaPit(pit, stone)) {
            sowResult = SowResult.NOT_SOWED;
        } else {
            sowStoneToPit(stone, pit);
            sowResult = SowResult.SOWED;
        }

        return sowResult;
    }

    private void sowStoneToPit(Stone stone, Pit pit) {
        stone.setPit(pit);
        pit.getStones().add(stone);
    }

    private boolean isOutOfTurnPlayerMancalaPit(Pit pit, Stone stone) {
        return pit.isMancala() && !isTwoPitOwnersTheSame(pit, stone.getPit());
    }

    private boolean isTwoPitOwnersTheSame(Pit pit, Pit otherPit) {
        return pit.getOwner().getName().equals(otherPit.getOwner().getName());
    }

    @Override
    public LastStoneSowResult sowLastStone(Stone stone, Pit pit) {
        LastStoneSowResult sowResult;

        if (pit.isMancala() && isTwoPitOwnersTheSame(pit, stone.getPit())) {
            sowStoneToPit(stone, pit);
            sowResult = LastStoneSowResult.ANOTHER_ROUND;
        } else if (pit.getStones().size() == 0 && isTwoPitOwnersTheSame(pit, stone.getPit())) {
            sowStoneToPit(stone, pit);
            sowResult = LastStoneSowResult.TAKE_FROM_ALL_PLAYERS;
        } else if (isOutOfTurnPlayerMancalaPit(pit, stone)) {
            sowResult = LastStoneSowResult.NOT_SOWED;
        } else {
            sowStoneToPit(stone, pit);
            sowResult = LastStoneSowResult.SOWED;
        }

        return sowResult;
    }

    @Override
    public void sowCapturedStonesInMancala(Set<Stone> stones, Pit mancalaPit) {
        stones.forEach(stone -> sowStoneToPit(stone, mancalaPit));
    }

    @Override
    public Set<Stone> pickupStonesFromPit(int pitIndex, Board board) {
        Set<Stone> stonesToSow = board.getPits().get(pitIndex).getStones();
        board.getPits().get(pitIndex).setStones(new HashSet<>());
        return stonesToSow;
    }

}
