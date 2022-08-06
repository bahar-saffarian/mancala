package com.bol.mancala.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Pit extends BaseEntity {
    int pitIndexInBoard;
    @ManyToOne
    private Player owner;
    private boolean isMankala;
    @OneToMany
    private Set<Stone> stones = new HashSet<>();

    public Pit(Player owner, boolean isMankala) {
        this.owner = owner;
        this.isMankala = isMankala;
    }
}
