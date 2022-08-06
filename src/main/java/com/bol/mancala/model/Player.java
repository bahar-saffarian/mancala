package com.bol.mancala.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Player extends BaseEntity {
    private String name;
    @OneToOne
    private Player nextPlayer;
    @OneToOne
    private Pit mankala;
    int firstPitIndex;
    int pitCount;

    public Player(String name) {
        this.name = name;
    }
}
