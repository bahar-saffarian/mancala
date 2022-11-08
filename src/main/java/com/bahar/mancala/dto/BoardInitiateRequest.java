package com.bahar.mancala.dto;

import com.bahar.mancala.util.annotation.PlayersMinSizeConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardInitiateRequest implements Serializable {
    @NotEmpty
    @PlayersMinSizeConstraint
    private Set<String> playersName;
    private Integer numberOfPlayerPits;
    private Integer numberOfPitStones;
}
