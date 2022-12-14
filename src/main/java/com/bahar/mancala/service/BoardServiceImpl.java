package com.bahar.mancala.service;

import com.bahar.mancala.dto.LastStoneSowResult;
import com.bahar.mancala.dto.SowResult;
import com.bahar.mancala.model.Board;
import com.bahar.mancala.model.Player;
import com.bahar.mancala.model.Stone;
import com.bahar.mancala.repository.BoardRepository;
import com.bahar.mancala.repository.PitRepository;
import com.bahar.mancala.repository.PlayerRepository;
import com.bahar.mancala.repository.StoneRepository;
import com.bahar.mancala.util.ListUtil;
import com.bahar.mancala.util.MancalaRulesUtil;
import com.bahar.mancala.util.PlayBoardValidator;
import com.bahar.mancala.model.Pit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BoardServiceImpl implements BoardService {
    final private BoardRepository boardRepository;
    final private PlayerRepository playerRepository;
    final private PitRepository pitRepository;
    final private StoneRepository stoneRepository;
    final private PitService pitService;

    @Autowired
    public BoardServiceImpl(
            BoardRepository boardRepository,
            PlayerRepository playerRepository,
            PitRepository pitRepository, StoneRepository stoneRepository, PitService pitService,
            @Value("${board.numberOfEachPlayerPits}") int defaultNumberOfEachPlayerPits,
            @Value("${board.numberOfEachPitStones}") int defaultNumberOfEachPitStones

    ) {
        this.boardRepository = boardRepository;
        this.playerRepository = playerRepository;
        this.pitRepository = pitRepository;
        this.stoneRepository = stoneRepository;
        this.pitService = pitService;
        this.DEFAULT_NUMBER_OF_EACH_PLAYER_PITS = defaultNumberOfEachPlayerPits;
        this.DEFAULT_NUMBER_OF_EACH_PIT_STONES = defaultNumberOfEachPitStones;
    }


    private final int DEFAULT_NUMBER_OF_EACH_PLAYER_PITS;


    private final int DEFAULT_NUMBER_OF_EACH_PIT_STONES;


    @Override
    @Transactional
    //return an initiated board that the pits are filled with stones and has empty mancala pits for each player
    //params: The players is a list object of Player class witch just name values.
    //numberOfPlayerPits is an optional value that specified how many pits each player should have(default value is provided by properties file)
    //numberOfPitStones is an optional value that specified how many stones are in each pit in the beginning(default value is provided by properties file)
    public Board initiateGameBoard(Set<Player> players, Integer numberOfPlayerPits, Integer numberOfPitStones) {
        final int finalNumberOfPitStones = numberOfPitStones == null ? DEFAULT_NUMBER_OF_EACH_PIT_STONES : numberOfPitStones;
        final int finalNumberOfPlayerPits = numberOfPlayerPits == null ? DEFAULT_NUMBER_OF_EACH_PLAYER_PITS : numberOfPlayerPits;
        final Random rand = new Random();

        //creating board pits filled with stones that are assigned to the pit
        List<Pit> boardPits =
                players.stream().map(player -> {
                    List<Pit> playerPits =
                            new ArrayList<>(IntStream.range(0, finalNumberOfPlayerPits)
                                    .mapToObj(i -> {
                                        Pit pit = new Pit(player, false);
                                        IntStream.range(0, finalNumberOfPitStones)
                                                .forEach(stoneIndex -> pit.getStones().add(new Stone(pit)));
                                        return pit;
                                    }).toList());
                    playerPits.add(new Pit(player, true));

                    return playerPits;
                }).flatMap(List::stream).toList();

        setIndexValueOfPits(boardPits);
        setBoardAttributesOfPlayers(boardPits);
        linkPlayersInOrder(players.stream().toList());

        Board board = new Board(
                boardPits,
                players,
                players.stream().toList().get(rand.nextInt(players.size()))
        );

        List<Stone> boardStones = new ArrayList<>();
        boardPits.forEach(pit -> boardStones.addAll(pit.getStones()));
        stoneRepository.saveAll(boardStones);
        pitRepository.saveAll(boardPits);
        playerRepository.saveAll(players);
        boardRepository.save(board);
        return board;
    }

    //Each player needs to have access to its mancala, the player's first pit index in the board, and the player's pit count in the board
    //This function fills attributes of each player by the created pits information
    private void setBoardAttributesOfPlayers(List<Pit> boardPits) {
        Map<Player, Optional<Pit>> playerToFirstPitMap =
                boardPits.stream().collect(Collectors.groupingBy(Pit::getOwner, Collectors.minBy(Comparator.comparing(Pit::getPitIndexInBoard))));
        Map<Player, Long> playerToPitCountMap =
                boardPits.stream().filter(pit -> !pit.isMancala()).collect(Collectors.groupingBy(Pit::getOwner, Collectors.counting()));
        Map<Player, List<Pit>> playerToMancalaMap = boardPits.stream().filter(Pit::isMancala).collect(Collectors.groupingBy(Pit::getOwner));


        playerToFirstPitMap.forEach((player, pit) ->
                player.setFirstPitIndex(pit.orElseThrow(() -> new IllegalStateException("Min index value not found ")).getPitIndexInBoard()));
        playerToPitCountMap.forEach((player, pitCount) ->
                player.setPitCount(pitCount.intValue()));
        playerToMancalaMap.forEach((player, mancala) ->
                player.setMancala(mancala.stream().findFirst().orElseThrow(() -> new IllegalStateException("Player Mancala value not found "))));
    }

    //Each pit is aware of it's index in the board to help to express its position in some conditions
    private void setIndexValueOfPits(List<Pit> boardPits) {
        IntStream.range(0, boardPits.size()).forEach(index -> boardPits.get(index).setPitIndexInBoard(index));
    }

    private void linkPlayersInOrder(List<Player> playerList) {
        IntStream.range(0, playerList.size())
                .forEach(
                        i -> playerList.get(i).setNextPlayer(ListUtil.getNextOfLoopList(playerList, (i+1)))
                );
    }

    @Override
    @Transactional
    //This function play the board from a specified pit, pitIndex argument is the pit to play and boardId helps to load the related board
    //At the end of this function it is checked that whether the game is finished or not.
    //if the game is finished the points are calculated and the winner is determined.
    //In case of having even points, the winner attribute is defined as list structure.
    public Board playFromPit(final Long boardId, final int pitIndex) {
        Board board = getBoardById(boardId);

        List<PlayBoardValidator.ValidationResult> validationErrors = PlayBoardValidator.validatePlayFromPit(pitIndex).apply(board);
        if (!validationErrors.isEmpty()) throw new IllegalStateException(validationErrors.toString()); //TODO Custom exception handling

        pickupAndSowStonesInSuccessivePits(pitIndex, board);

        determineWinnerIfTheGameIsOver(board);

        return board;
    }

    public void determineWinnerIfTheGameIsOver(Board board) {
        if (MancalaRulesUtil.isTheGaveOver(board)) {
            moveAllRemainStonesToTheOwnersMancala(board);
            board.setFinished(true);
            board.setWinner(MancalaRulesUtil.getWinners(board));
        }
    }

    public void moveAllRemainStonesToTheOwnersMancala(Board board) {
        board.getPits().stream().filter(pit -> !pit.isMancala() && pit.getStones().size()>0)
                .forEach(pit -> {
                    Set<Stone> pickedStones = pitService.pickupStonesFromPit(pit.getPitIndexInBoard(), board);
                    pitService.sowCapturedStonesInMancala(pickedStones, pit.getOwner().getMancala());
                });
    }

    private void pickupAndSowStonesInSuccessivePits(int pitIndex, Board board) {
        Set<Stone> stonesToSow = pitService.pickupStonesFromPit(pitIndex, board);
        Iterator<Stone> stonesIterator = stonesToSow.iterator();
        AtomicInteger pitIndexToSow = new AtomicInteger(pitIndex);

        //Sow stones in successive pits Except the last one, because the last stone plays the main roll in game
        IntStream.range(0, (stonesToSow.size()-1)).forEach(i -> {
            SowResult sowResult;
            Stone stoneToSow = stonesIterator.next();
            do {
                pitIndexToSow.set(pitIndexToSow.get() + 1);
                sowResult = pitService.sow(stoneToSow, ListUtil.getNextOfLoopList(board.getPits(), pitIndexToSow.get()));
            } while (sowResult.equals(SowResult.NOT_SOWED));
        });

        //SOW the last stone
        Stone lastStoneToSow = stonesIterator.next();
        LastStoneSowResult lastStoneSowResult;
        Pit lastPitToSow;
        do {
            pitIndexToSow.set(pitIndexToSow.get() + 1);
            lastPitToSow = ListUtil.getNextOfLoopList(board.getPits(), pitIndexToSow.get());
            lastStoneSowResult = pitService.sowLastStone(lastStoneToSow, lastPitToSow);
        } while (lastStoneSowResult.equals(LastStoneSowResult.NOT_SOWED));

        switch (lastStoneSowResult) {
            case SOWED -> board.setTurn(board.getTurn().getNextPlayer());
            case ANOTHER_ROUND -> board.setTurn(board.getTurn());
            case TAKE_FROM_ALL_PLAYERS -> {
                Pit ownerPlayerMancalaPit = board.getTurn().getMancala();
                int pitToCaptureOffset = lastPitToSow.getPitIndexInBoard() - board.getTurn().getFirstPitIndex();
                board.getPlayers().forEach(player -> {
                    Set<Stone> pickedStones = pitService.pickupStonesFromPit(player.getFirstPitIndex() + pitToCaptureOffset, board);
                    pitService.sowCapturedStonesInMancala(pickedStones, ownerPlayerMancalaPit);
                });
                board.setTurn(board.getTurn().getNextPlayer());
            }
        }
    }

    @Override
    @Transactional
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException("Board Not found"));//TODO Custom Exception
    }

}