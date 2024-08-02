package org.springframework.samples.petclinic.mapas_del_reino.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerRepository;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {

    @Mock
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        gameService = new GameService(gameRepository, playerRepository, userRepository);
    }


    @Test
    public void testCountPlayersInGame() {
        Game game = new Game();
        Integer gameId = 1;
        game.setId(gameId);
        List<Player> players = new ArrayList<>();
        Player player = new Player();
        player.setId(1);
        players.add(player);
        Player player2 = new Player();
        player2.setId(2);
        players.add(player2);
        game.setPlayers(players);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game)); // Replace 'gameRepository' with the actual mock object that you want to use in your test.
        int result = gameService.countPlayersInGame(gameId);
        assertEquals(2, result);
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    public void testAddPlayerToGame() {
        Game game = new Game();
        Integer gameId = 1;
        game.setId(gameId);
        List<Player> players = new ArrayList<>();
        Player player = new Player();
        player.setId(1);
        game.setPlayers(players);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game)); // Replace 'gameRepository' with the actual mock object that you want to use in your test.
        gameService.addPlayerToGame(gameId, 1);
        int result = gameService.countPlayersInGame(gameId);
        assertEquals(1, result);
        verify(gameRepository, times(2)).findById(gameId);
    }

    @Test
    public void testRemovePlayerFromGame() {
        Game game = new Game();
        Integer gameId = 1;
        game.setId(gameId);
        List<Player> players = new ArrayList<>();
        Player player = new Player();
        player.setId(1);
        game.setPlayers(players);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        gameService.removePlayerFromGame(gameId, 1);
        int result = gameService.countPlayersInGame(gameId);
        assertEquals(0, result);
        verify(gameRepository, times(2)).findById(gameId);
    }

    @Test
    public void testEliminarJugadorDePartida() {
        Game game = new Game();
        game.setId(1);
        List<Player> players = new ArrayList<>();
        Player player = new Player();
        player.setId(1);
        players.add(player);
        game.setPlayers(players);
        game.setStatus(GameStatus.CREATED);
        User user = new User();
        Integer userId = 1;
        user.setId(userId);
        player.setUser(user);
        List<Game> games = new ArrayList<>();
        games.add(game);
        when(playerRepository.findByUserId(userId)).thenReturn(player);
        when(gameRepository.findAllGamesByPlayerId(1)).thenReturn(games);
        gameService.eliminarJugadorDePartida(userId);
        verify(playerRepository, times(1)).findByUserId(userId);
        verify(gameRepository, times(1)).findAllGamesByPlayerId(1);
    }

    @Test
    void eliminarJugadorDePartida_shouldDeleteGameIfStatusNotStartedAndPlayerCountIsOne() {
        // Arrange
        User user = new User();
        Integer userId = 1;
        user.setId(userId);
        Player player = new Player();
        player.setId(1);
        player.setUser(user);

        Game game = new Game();
        game.setStatus(GameStatus.CREATED);
        List<Player> players = new ArrayList<>();
        players.add(player);
        game.setPlayers(players);

        List<Game> games = new ArrayList<>();
        games.add(game);

        when(playerRepository.findByUserId(userId)).thenReturn(player);
        when(gameRepository.findAllGamesByPlayerId(player.getId())).thenReturn(games);

        // Act
        gameService.eliminarJugadorDePartida(userId);

        // Assert
        verify(gameRepository, times(1)).delete(game);
    }

    @Test
    void eliminarJugadorDePartida_shouldRemovePlayerFromGameIfStatusNotStartedAndPlayerCountIsMoreThanOne() {
        // Arrange
        User user = new User();
        Integer userId = 1;
        user.setId(userId);
        Player player = new Player();
        player.setId(1);
        player.setUser(user);
        User user2 = new User();
        user2.setId(2);
        Player player2 = new Player();
        player2.setId(2); 
        player2.setUser(user2);

        Game game = new Game();
        game.setStatus(GameStatus.CREATED);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(player2);
        game.setPlayers(players);
        List<Game> games = new ArrayList<>();
        games.add(game);

        when(playerRepository.findByUserId(userId)).thenReturn(player);
        when(gameRepository.findAllGamesByPlayerId(player.getId())).thenReturn(games);

        // Act
        gameService.eliminarJugadorDePartida(userId);

        // Assert
        assertEquals(1, game.getPlayers().size());
        verify(gameRepository, times(1)).save(game);
    }
}
