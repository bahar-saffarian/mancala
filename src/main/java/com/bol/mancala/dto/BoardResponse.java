package com.bol.mancala.dto;

import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse implements Serializable {
    private Long id;
    private List<PitResponse> pits;
    private Set<PlayersResponse> players;
    private String turn;
    private Set<PlayersResponse> winners;
    private boolean isFinished;

    public static BoardResponse of(Board board) {
        AtomicInteger order = new AtomicInteger(0);
        Set<PlayersResponse> players = board.getPlayers().stream().sorted(Comparator.comparing(Player::getName))
                .map(
                        player -> {
                            order.set(order.get() + 1);
                            return new PlayersResponse(player.getName(), order.get());
                        }
                ).collect(Collectors.toSet());

        return new BoardResponse(
                board.getId(),
                board.getPits().stream().map(PitResponse::new).toList(),
                players,
                board.getTurn().getName(),
                null,
                board.isFinished()
        );

    }
}
