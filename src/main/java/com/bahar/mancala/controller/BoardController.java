package com.bahar.mancala.controller;

import com.bahar.mancala.dto.BoardInitiateRequest;
import com.bahar.mancala.dto.BoardResponse;
import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Player;
import com.bahar.mancala.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("boardId") Long boardId) {
        Board board = boardService.getBoardById(boardId);

        return getBoardResponseEntity(board);
    }
    @PostMapping("/add")
    public ResponseEntity<BoardResponse> initiateBoard(@RequestBody BoardInitiateRequest boardInitiateRequest) {
        Set<Player> players = boardInitiateRequest.getPlayersName().stream().map(Player::new).collect(Collectors.toSet());
        Board initiatedBoard = boardService.initiateGameBoard(players, boardInitiateRequest.getNumberOfPlayerPits(), boardInitiateRequest.getNumberOfPitStones());

        return getBoardResponseEntity(initiatedBoard);
    }

    @PutMapping("/play/{boardId}")
    public ResponseEntity<BoardResponse> playFromPit(
            @PathVariable("boardId") Long boardId,
            @RequestParam int pitIndex) {
        Board resultBoard = boardService.playFromPit(boardId, pitIndex);

        return getBoardResponseEntity(resultBoard);
    }

    private ResponseEntity<BoardResponse> getBoardResponseEntity(Board board) {
        return new ResponseEntity<>(new BoardResponse(board), HttpStatus.OK);
    }
}
