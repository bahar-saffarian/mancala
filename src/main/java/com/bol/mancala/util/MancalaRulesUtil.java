package com.bol.mancala.util;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;

import java.util.List;
import java.util.stream.IntStream;

public class MancalaRulesUtil {
    public static boolean isTheGaveOver(Board board) {
        return board.getPlayers().stream().anyMatch(player ->
                IntStream.range(player.getFirstPitIndex(), player.getFirstPitIndex() + player.getPitCount())
                        .allMatch(playerPitIndex -> board.getPits().get(playerPitIndex).getStones().isEmpty())
        );
    }

    public static List<Player> getWinners(Board board) {
        int maxStonesInMancala = board.getPlayers().stream().map(player -> player.getMancala().getStones().size()).max(Integer::compareTo).get();
        return board.getPlayers().stream().filter(player -> player.getMancala().getStones().size() == maxStonesInMancala).toList();
    }
}
