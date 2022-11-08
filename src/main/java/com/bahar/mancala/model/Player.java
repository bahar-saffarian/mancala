package com.bahar.mancala.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Player nextPlayer;
    @OneToOne
    private Pit mancala;
    int firstPitIndex;
    int pitCount;

    public Player(String name) {
        this.name = name;
    }
}
