package com.bahar.mancala.dto;

import com.bahar.mancala.model.Pit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PitResponse implements Serializable {
    private int pitIndexInBoard;
    private boolean isMancala;
    private int stoneNumber;
    private String ownerName;

    public PitResponse(Pit pit) {
        this.pitIndexInBoard = pit.getPitIndexInBoard();
        this.isMancala = pit.isMancala();
        this.stoneNumber = pit.getStones().size();
        this.ownerName = pit.getOwner().getName();
    }
}
