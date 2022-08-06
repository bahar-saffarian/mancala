package com.bol.mancala.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Stone extends BaseEntity {
    @ManyToOne
    private Pit pit;

    public Stone(Pit pit) {
        this.pit = pit;
    }
}
