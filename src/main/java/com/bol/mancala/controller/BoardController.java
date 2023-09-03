package com.bol.mancala.controller;

import com.bol.mancala.dto.BoardInitiateRequest;
import com.bol.mancala.dto.BoardResponse;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Player;
import com.bol.mancala.service.BoardService;
import com.bol.mancala.swagger.ApiGeneralResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/board")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @ApiGeneralResponses
    @Secured("ROLE_API_USER")
    @GetMapping("/{boardId}")
    public BoardResponse getBoard(@PathVariable("boardId") Long boardId) {
        Board board = boardService.getBoardById(boardId);

        return BoardResponse.of(board);
    }

    @ApiGeneralResponses
    @Secured("ROLE_API_USER")
    @PostMapping("/add")
    public BoardResponse initiateBoard(@RequestBody @Valid BoardInitiateRequest boardInitiateRequest) {
        Set<Player> players = boardInitiateRequest.getPlayersName().stream().map(Player::new).collect(Collectors.toSet());
        Board initiatedBoard = boardService.initiateGameBoard(players, boardInitiateRequest.getNumberOfPlayerPits(), boardInitiateRequest.getNumberOfPitStones());

        return BoardResponse.of(initiatedBoard);
    }

    @ApiGeneralResponses
    @Secured("ROLE_API_USER")
    @PutMapping("/play/{boardId}")
    public BoardResponse playFromPit(
            @PathVariable("boardId") Long boardId,
            @RequestParam int pitIndex) {
        Board resultBoard = boardService.playFromPit(boardId, pitIndex);

        return BoardResponse.of(resultBoard);
    }

}
