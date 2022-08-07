package com.bol.mancala.service;

import com.bol.mancala.data.LastStoneSowResult;
import com.bol.mancala.data.SowResult;
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
        if (isOutOfTurnPlayerMankalaPit(pit, stone)) {
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

    private boolean isOutOfTurnPlayerMankalaPit( Pit pit, Stone stone) {
        return pit.isMankala() && !isTwoPitOwnersTheSame(pit, stone.getPit());
    }

    private boolean isTwoPitOwnersTheSame(Pit pit, Pit otherPit) {
        return pit.getOwner().getName().equals(otherPit.getOwner().getName());
    }

    @Override
    public LastStoneSowResult sowLastStone(Stone stone, Pit pit) {
        LastStoneSowResult sowResult;

        if (pit.isMankala() && isTwoPitOwnersTheSame(pit, stone.getPit())) {
            sowStoneToPit(stone, pit);
            sowResult = LastStoneSowResult.ANOTHER_ROUND;
        } else if (pit.getStones().size() == 0 && isTwoPitOwnersTheSame(pit, stone.getPit())) {
            sowStoneToPit(stone, pit);
            sowResult = LastStoneSowResult.TAKE_FROM_ALL_PLAYERS;
        } else if (isOutOfTurnPlayerMankalaPit(pit, stone)) {
            sowResult = LastStoneSowResult.NOT_SOWED;
        } else {
            sowStoneToPit(stone, pit);
            sowResult = LastStoneSowResult.SOWED;
        }

        return sowResult;
    }

    @Override
    public void sowCapturedStonesInMankala(Set<Stone> stones, Pit mankalaPit) {
        stones.forEach(stone -> sowStoneToPit(stone, mankalaPit));
    }

    @Override
    public Set<Stone> pickupStonesFromPit(int pitIndex, Board board) {
        Set<Stone> stonesToSow = board.getPits().get(pitIndex).getStones();
        board.getPits().get(pitIndex).setStones(new HashSet<>());
        return stonesToSow;
    }

}
