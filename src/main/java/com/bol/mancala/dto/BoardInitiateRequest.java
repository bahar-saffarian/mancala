package com.bol.mancala.dto;

import com.bol.mancala.util.annotation.PlayersMinSizeConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
