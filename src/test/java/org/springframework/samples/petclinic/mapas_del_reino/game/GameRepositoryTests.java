package org.springframework.samples.petclinic.mapas_del_reino.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
public class GameRepositoryTests {

    @Autowired
    private GameRepository gameRepository;

    public final static int PLAYER_ID = 1;

    @Test
    public void testFindByName() {
        Game foundGame = gameRepository.findByName("Partida 1");
        assertEquals("Partida 1", foundGame.getName());
    }

    @Test
    public void testFindByNameNonFound() {
        Game foundGame = gameRepository.findByName("Partida XX");
        assertEquals(null, foundGame);
    }

    @Test
    public void testFindFinishedGamesByPlayerId() {
        List<Game> games = gameRepository.findGamesByPlayerId(PLAYER_ID);
        int tamaño = games.size();
        assertEquals(10, tamaño);
    }

    @Test
    public void testFindCreatedGames() {
        List<Game> games = gameRepository.findCreatedGames();
        int tamaño = games.size();
        assertEquals(1, tamaño);
    }

    @Test
    public void testFindStartedGames() {
        List<Game> games = gameRepository.findStartedGames();
        int tamaño = games.size();
        assertEquals(0, tamaño);
    }

    @Test
    public void testFindFinishedGames() {
        List<Game> games = gameRepository.findFinishedGames();
        int tamaño = games.size();
        assertEquals(10, tamaño);
    }

    @Test
    public void testFindAllGamesByPlayerId() {
        List<Game> games = gameRepository.findAllGamesByPlayerId(PLAYER_ID);
        int tamaño = games.size();
        assertEquals(11, tamaño);
    }

    




}
