package org.springframework.samples.petclinic.mapas_del_reino.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.Casilla;
import org.springframework.samples.petclinic.mapas_del_reino.game.Game;
import org.springframework.samples.petclinic.mapas_del_reino.game.GameRepository;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerRepository;

@DataJpaTest
public class BoardRepositoryTests {


    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;



    @Test
    public void testFindByPlayerId() {
        Player player = playerRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Board board = new Board();
        board.setPlayer(player);
        board.setCasillas(new ArrayList<Casilla>());
        board.setGame(game);
        board.setCriterioA1(Criterio.CINCO);
        board.setCriterioA2(Criterio.CUATRO);
        board.setCriterioB1(Criterio.TRES);
        board.setCriterioB2(Criterio.DOS);

        boardRepository.save(board);

        List<Board> boards = boardRepository.findByPlayerId(player.getId());
        assertEquals(1, boards.size());
    }

    @Test
    public void testFindByGameId() {
        Player player = playerRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Board board = new Board();
        board.setPlayer(player);
        board.setCasillas(new ArrayList<Casilla>());
        board.setGame(game);
        board.setCriterioA1(Criterio.CINCO);
        board.setCriterioA2(Criterio.CUATRO);
        board.setCriterioB1(Criterio.TRES);
        board.setCriterioB2(Criterio.DOS);

        boardRepository.save(board);

        List<Board> boards = boardRepository.findByGameId(game.getId());
        assertEquals(1, boards.size());
    }

    @Test
    public void testFindByGameIdAndPlayerId() {
        Player player = playerRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Board board = new Board();
        board.setPlayer(player);
        board.setCasillas(new ArrayList<Casilla>());
        board.setGame(game);
        board.setCriterioA1(Criterio.CINCO);
        board.setCriterioA2(Criterio.CUATRO);
        board.setCriterioB1(Criterio.TRES);
        board.setCriterioB2(Criterio.DOS);

        boardRepository.save(board);

        assertEquals(board, boardRepository.findByGameIdAndPlayerId(game.getId(), player.getId()).orElseThrow(() -> new IllegalArgumentException("Board not found")));
    }

    @Test
    public void negativeTestFindByGameIdAndPlayerId() {
        Player player = playerRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Board board = new Board();
        board.setPlayer(player);
        board.setCasillas(new ArrayList<Casilla>());
        board.setGame(game);
        board.setCriterioA1(Criterio.CINCO);
        board.setCriterioA2(Criterio.CUATRO);
        board.setCriterioB1(Criterio.TRES);
        board.setCriterioB2(Criterio.DOS);

        boardRepository.save(board);

        assertEquals(null, boardRepository.findByGameIdAndPlayerId(game.getId(), 2).orElse(null));
    }

    @Test
    public void negativeTestFindByGame() {
        Player player = playerRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Board board = new Board();
        board.setPlayer(player);
        board.setCasillas(new ArrayList<Casilla>());
        board.setGame(game);
        board.setCriterioA1(Criterio.CINCO);
        board.setCriterioA2(Criterio.CUATRO);
        board.setCriterioB1(Criterio.TRES);
        board.setCriterioB2(Criterio.DOS);

        boardRepository.save(board);

        assertEquals(0, boardRepository.findByGameId(2).size());
    }

    @Test
    public void negativeTestFindByPlayer() {
        Player player = playerRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Player not found"));
        Game game = gameRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Board board = new Board();
        board.setPlayer(player);
        board.setCasillas(new ArrayList<Casilla>());
        board.setGame(game);
        board.setCriterioA1(Criterio.CINCO);
        board.setCriterioA2(Criterio.CUATRO);
        board.setCriterioB1(Criterio.TRES);
        board.setCriterioB2(Criterio.DOS);

        boardRepository.save(board);

        assertEquals(0, boardRepository.findByPlayerId(2).size());
    }
}

