package com.bahar.mancala.service;

import com.bahar.mancala.helper.BoardTestHelper;
import com.bahar.mancala.dto.LastStoneSowResult;
import com.bahar.mancala.dto.SowResult;
import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Pit;
import com.bahar.mancala.model.Player;
import com.bahar.mancala.model.Stone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PitServiceTest extends BoardTestHelper {
    private PitService pitService;

    @BeforeEach
    void setUp() {
        pitService = new PitServiceImpl();
        initializeBoardService();
    }


    @Test
    void sowPositiveTest() {
        //Given
        Board board = initializeTowPlayerBoard();
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        Stone firstStoneToSow = stones.stream().findFirst().get();
        Pit firstPitToSow = board.getPits().get(1);
        //When
        SowResult sowResult = pitService.sow(firstStoneToSow, firstPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(SowResult.SOWED);
        assertThat(firstPitToSow.getStones().contains(firstStoneToSow)).isTrue();
        assertThat(firstStoneToSow.getPit().equals(firstPitToSow)).isTrue();
    }

    @Test
    void notSowInOtherPlayerMancalaPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        Stone stoneToSow = stones.stream().findFirst().get();
        Pit otherMancalaPitToSow = board.getPits().stream()
                .filter(pit -> pit.isMancala() && !pit.getOwner().getName().equals(stoneToSow.getPit().getOwner().getName()))
                .findFirst().get();
        //When
        SowResult sowResult = pitService.sow(stoneToSow, otherMancalaPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(SowResult.NOT_SOWED);
        assertThat(otherMancalaPitToSow.getStones().contains(stoneToSow)).isFalse();
        assertThat(stoneToSow.getPit().equals(otherMancalaPitToSow)).isFalse();
    }

    @Test
    void sowLastStoneInOwnerNotEmptyPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        Stone lastStoneToSow = stones.stream().findFirst().get(); //consider it's the last one
        Pit lastPitToSow = board.getPits().get(1);//consider it's the last pit to sow stone
        //When
        LastStoneSowResult sowResult = pitService.sowLastStone(lastStoneToSow, lastPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(LastStoneSowResult.SOWED);
        assertThat(lastPitToSow.getStones().contains(lastStoneToSow)).isTrue();
        assertThat(lastStoneToSow.getPit().equals(lastPitToSow)).isTrue();
    }

    @Test
    void notSowLastStoneInOtherPlayerMancalaPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        Stone lastStoneToSow = stones.stream().findFirst().get(); //Consider it's the last one to sow
        Pit otherMancalaPitToSow = board.getPits().stream()
                .filter(pit -> pit.isMancala() && !pit.getOwner().getName().equals(lastStoneToSow.getPit().getOwner().getName()))
                .findFirst().get();
        int otherMancalaStoneSizeBeforSow = otherMancalaPitToSow.getStones().size();
        //When
        LastStoneSowResult sowResult = pitService.sowLastStone(lastStoneToSow, otherMancalaPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(LastStoneSowResult.NOT_SOWED);
        assertThat(otherMancalaPitToSow.getStones().contains(lastStoneToSow)).isFalse();
        assertThat(lastStoneToSow.getPit().equals(otherMancalaPitToSow)).isFalse();
        assertThat(otherMancalaStoneSizeBeforSow == otherMancalaPitToSow.getStones().size()).isTrue();
    }

    @Test
    void whenLastStoneSowInOwnerMancalaPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player ownerPlayer = board.getTurn();
        Pit ownerOrdinaryPit = board.getPits().stream().filter(pit -> !pit.isMancala() && pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
        Set<Stone> stones = pitService.pickupStonesFromPit(ownerOrdinaryPit.getPitIndexInBoard(), board);
        Stone lastStoneToSow = stones.stream().findFirst().get(); //consider it's the last one
        Pit ownerMancalaToSow = ownerPlayer.getMancala();
        //When
        LastStoneSowResult sowResult = pitService.sowLastStone(lastStoneToSow, ownerMancalaToSow);
        //Then
        assertThat(sowResult).isEqualTo(LastStoneSowResult.ANOTHER_ROUND);
        assertThat(ownerMancalaToSow.getStones().contains(lastStoneToSow)).isTrue();
        assertThat(lastStoneToSow.getPit().equals(ownerMancalaToSow)).isTrue();
    }

    @Test
    void whenLastStoneSowInOwnerEmptyPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player ownerPlayer = board.getTurn();
        Pit ownerPitToPickup = board.getPits().stream().filter(pit -> !pit.isMancala() && pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
        Set<Stone> stones = pitService.pickupStonesFromPit(ownerPitToPickup.getPitIndexInBoard(), board);
        Pit ownerEmptyPitToSow = board.getPits().stream()
                .filter(pit -> !pit.isMancala() && pit != ownerPitToPickup && pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
        ownerEmptyPitToSow.setStones(new HashSet<>());
        Stone lastStoneToSow = stones.stream().findFirst().get(); //consider it's the last one
        //When
        LastStoneSowResult sowResult = pitService.sowLastStone(lastStoneToSow, ownerEmptyPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(LastStoneSowResult.TAKE_FROM_ALL_PLAYERS);
        assertThat(ownerEmptyPitToSow.getStones().contains(lastStoneToSow)).isTrue();
        assertThat(lastStoneToSow.getPit().equals(ownerEmptyPitToSow)).isTrue();
    }


    @Test
    void sowCapturedStonesInMancalaPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        Player ownerPlayer = board.getTurn();
        int ownerPlayerMancalaSizeBeforeSow = ownerPlayer.getMancala().getStones().size();
        Player otherPlayer = ownerPlayer.getNextPlayer();
        Pit otherPlayerPitToCapture = board.getPits().get(otherPlayer.getFirstPitIndex()); //for example to capture from first pits
        int otherPlayerPitSizeBeforeSow = otherPlayerPitToCapture.getStones().size();
        //When
        Set<Stone> capturedStones = pitService.pickupStonesFromPit(otherPlayerPitToCapture.getPitIndexInBoard(), board);
        pitService.sowCapturedStonesInMancala(capturedStones, ownerPlayer.getMancala());
        //Then
        assertThat(ownerPlayer.getMancala().getStones().size()).isEqualTo(otherPlayerPitSizeBeforeSow + ownerPlayerMancalaSizeBeforeSow);
        assertThat(otherPlayerPitToCapture.getStones().size()).isEqualTo(0);
        assertThat(ownerPlayer.getMancala().getStones().size() + otherPlayerPitToCapture.getStones().size() )
                .isEqualTo(otherPlayerPitSizeBeforeSow + ownerPlayerMancalaSizeBeforeSow);
    }

    @Test
    void pickupStonesFromPit() {
        //Given
        Board board = initializeTowPlayerBoard();
        //When
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        //Then
        assertThat(board.getPits().get(0).getStones().size()).isEqualTo(0);

    }
}
