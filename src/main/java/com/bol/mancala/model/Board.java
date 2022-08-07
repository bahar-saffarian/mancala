package com.bol.mancala.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Board extends BaseEntity {
    @OneToMany
    private List<Pit> pits;
    @OneToMany
    private Set<Player> players;
    @OneToOne
    private Player turn;
    @OneToMany
    private List<Player> winner;
    private boolean finished = false;

    public Board(List<Pit> pits, Set<Player> players, Player turn) {
        this.pits = pits;
        this.players = players;
        this.turn = turn;
    }
}
