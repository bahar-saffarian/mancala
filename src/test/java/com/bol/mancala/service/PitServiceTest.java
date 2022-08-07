package com.bol.mancala.service;

import com.bol.mancala.data.LastStoneSowResult;
import com.bol.mancala.data.SowResult;
import com.bol.mancala.helper.BoardTestHelper;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Player;
import com.bol.mancala.model.Stone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PitServiceTest {
    private PitService pitService;

    @BeforeEach
    void setUp() {
        pitService = new PitServiceImpl();
    }


    @Test
    void sowPositiveTest() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
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
    void notSowInOtherPlayerMankalaPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        Stone stoneToSow = stones.stream().findFirst().get();
        Pit otherMankalaPitToSow = board.getPits().stream()
                .filter(pit -> pit.isMankala() && !pit.getOwner().getName().equals(stoneToSow.getPit().getOwner().getName()))
                .findFirst().get();
        //When
        SowResult sowResult = pitService.sow(stoneToSow, otherMankalaPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(SowResult.NOT_SOWED);
        assertThat(otherMankalaPitToSow.getStones().contains(stoneToSow)).isFalse();
        assertThat(stoneToSow.getPit().equals(otherMankalaPitToSow)).isFalse();
    }

    @Test
    void sowLastStoneInOwnerNotEmptyPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
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
    void notSowLastStoneInOtherPlayerMankalaPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        Stone lastStoneToSow = stones.stream().findFirst().get(); //Consider it's the last one to sow
        Pit otherMankalaPitToSow = board.getPits().stream()
                .filter(pit -> pit.isMankala() && !pit.getOwner().getName().equals(lastStoneToSow.getPit().getOwner().getName()))
                .findFirst().get();
        int otherMankalaStoneSizeBeforSow = otherMankalaPitToSow.getStones().size();
        //When
        LastStoneSowResult sowResult = pitService.sowLastStone(lastStoneToSow, otherMankalaPitToSow);
        //Then
        assertThat(sowResult).isEqualTo(LastStoneSowResult.NOT_SOWED);
        assertThat(otherMankalaPitToSow.getStones().contains(lastStoneToSow)).isFalse();
        assertThat(lastStoneToSow.getPit().equals(otherMankalaPitToSow)).isFalse();
        assertThat(otherMankalaStoneSizeBeforSow == otherMankalaPitToSow.getStones().size()).isTrue();
    }

    @Test
    void whenLastStoneSowInOwnerMankalaPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
        Player ownerPlayer = board.getTurn();
        Pit ownerOrdinaryPit = board.getPits().stream().filter(pit -> !pit.isMankala() && pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
        Set<Stone> stones = pitService.pickupStonesFromPit(ownerOrdinaryPit.getPitIndexInBoard(), board);
        Stone lastStoneToSow = stones.stream().findFirst().get(); //consider it's the last one
        Pit ownerMankalaToSow = ownerPlayer.getMankala();
        //When
        LastStoneSowResult sowResult = pitService.sowLastStone(lastStoneToSow, ownerMankalaToSow);
        //Then
        assertThat(sowResult).isEqualTo(LastStoneSowResult.ANOTHER_ROUND);
        assertThat(ownerMankalaToSow.getStones().contains(lastStoneToSow)).isTrue();
        assertThat(lastStoneToSow.getPit().equals(ownerMankalaToSow)).isTrue();
    }

    @Test
    void whenLastStoneSowInOwnerEmptyPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
        Player ownerPlayer = board.getTurn();
        Pit ownerPitToPickup = board.getPits().stream().filter(pit -> !pit.isMankala() && pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
        Set<Stone> stones = pitService.pickupStonesFromPit(ownerPitToPickup.getPitIndexInBoard(), board);
        Pit ownerEmptyPitToSow = board.getPits().stream()
                .filter(pit -> !pit.isMankala() && pit != ownerPitToPickup && pit.getOwner().getName().equals(ownerPlayer.getName())).findFirst().get();
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
    void sowCapturedStonesInMankalaPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
        Player ownerPlayer = board.getTurn();
        int ownerPlayerMankalaSizeBeforeSow = ownerPlayer.getMankala().getStones().size();
        Player otherPlayer = ownerPlayer.getNextPlayer();
        Pit otherPlayerPitToCapture = board.getPits().get(otherPlayer.getFirstPitIndex()); //for example to capture from first pits
        int otherPlayerPitSizeBeforeSow = otherPlayerPitToCapture.getStones().size();
        //When
        Set<Stone> capturedStones = pitService.pickupStonesFromPit(otherPlayerPitToCapture.getPitIndexInBoard(), board);
        pitService.sowCapturedStonesInMankala(capturedStones, ownerPlayer.getMankala());
        //Then
        assertThat(ownerPlayer.getMankala().getStones().size()).isEqualTo(otherPlayerPitSizeBeforeSow + ownerPlayerMankalaSizeBeforeSow);
        assertThat(otherPlayerPitToCapture.getStones().size()).isEqualTo(0);
        assertThat(ownerPlayer.getMankala().getStones().size() + otherPlayerPitToCapture.getStones().size() )
                .isEqualTo(otherPlayerPitSizeBeforeSow + ownerPlayerMankalaSizeBeforeSow);
    }

    @Test
    void pickupStonesFromPit() {
        //Given
        Board board = BoardTestHelper.initializeTowPlayerBoard();
        //When
        Set<Stone> stones = pitService.pickupStonesFromPit(0, board);
        //Then
        assertThat(board.getPits().get(0).getStones().size()).isEqualTo(0);

    }
}
