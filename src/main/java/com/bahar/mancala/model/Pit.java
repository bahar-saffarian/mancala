package com.bahar.mancala.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Pit extends BaseEntity {
    private int pitIndexInBoard;
    @ManyToOne
    private Player owner;
    private boolean isMancala;
    @OneToMany(mappedBy = "pit")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Stone> stones = new HashSet<>();

    public Pit(Player owner, boolean isMancala) {
        this.owner = owner;
        this.isMancala = isMancala;
    }
}
