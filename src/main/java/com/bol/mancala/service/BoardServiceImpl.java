package com.bol.mancala.service;

import com.bol.mancala.dto.LastStoneSowResult;
import com.bol.mancala.dto.SowResult;
import com.bol.mancala.model.Board;
import com.bol.mancala.model.Pit;
import com.bol.mancala.model.Player;
import com.bol.mancala.model.Stone;
import com.bol.mancala.repository.BoardRepository;
import com.bol.mancala.repository.PitRepository;
import com.bol.mancala.repository.PlayerRepository;
import com.bol.mancala.repository.StoneRepository;
import com.bol.mancala.util.ListUtil;
import com.bol.mancala.util.MancalaRulesUtil;
import com.bol.mancala.util.PlayBoardValidator;
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
    public Board initiateGameBoard(Set<Player> players, Integer numberOfPlayerPits, Integer numberOfPitStones) {
        final int finalNumberOfPitStones = numberOfPitStones == null ? DEFAULT_NUMBER_OF_EACH_PIT_STONES : numberOfPitStones;
        final int finalNumberOfPlayerPits = numberOfPlayerPits == null ? DEFAULT_NUMBER_OF_EACH_PLAYER_PITS : numberOfPlayerPits;
        final Random rand = new Random();

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
    public Board playFromPit(final Long boardId, final int pitIndex) {
        Board board = getBoardById(boardId);

        List<PlayBoardValidator.ValidationResult> validationErrors = PlayBoardValidator.validatePlayFromPit(pitIndex).apply(board);
        if (!validationErrors.isEmpty()) throw new IllegalStateException(validationErrors.toString()); //TODO Custom exception handling

        pickupAndSowStonesInSuccessivePits(pitIndex, board);

        if (MancalaRulesUtil.isTheGaveOver(board)) {
            board.setFinished(true);
            board.setWinner(MancalaRulesUtil.getWinners(board));
        }

        return board;
    }

    @Override
    @Transactional
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException("Board Not found"));//TODO Custom Exception
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


}