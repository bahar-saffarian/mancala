package com.bol.mancala.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Board extends BaseEntity {
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Pit> pits;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Player> players;
    @OneToOne
    private Player turn;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Player> winner;
    private boolean finished = false;

    public Board(List<Pit> pits, Set<Player> players, Player turn) {
        this.pits = pits;
        this.players = players;
        this.turn = turn;
    }
}
