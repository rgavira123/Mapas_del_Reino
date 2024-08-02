package org.springframework.samples.petclinic.mapas_del_reino.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTests {
    
    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerService  playerService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    void testFindAll() throws Exception {
        Game game = new Game();
        Game game2 = new Game();
        Game game3 = new Game();
        when(gameService.findAll()).thenReturn(List.of(game, game2, game3));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));

        verify(gameService, times(1)).findAll();
    }

    @Test
    void testFindGameById() throws Exception {
        int gameId = 1;
        when(gameService.findGameById(gameId)).thenReturn(new Game());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/{gameId}", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService, times(1)).findGameById(gameId);
    }

    @Test
    void testFindGamesByPlayerId() throws Exception {
        int playerId = 1;
        Player player = new Player();
        Game game = new Game();
        Game game2 = new Game();
        Game game3 = new Game();
        game.setPlayers(List.of(player));
        game2.setPlayers(List.of(player));
        game3.setPlayers(List.of(player));
        List<Game> games = List.of(game, game2, game3);
        when(gameService.findGamesByPlayerId(playerId)).thenReturn(games);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/player/{playerId}", playerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));

        verify(gameService, times(1)).findGamesByPlayerId(playerId);
    }

    @Test
    void testFinishGame() throws Exception {
        int gameId = 1;
        when(gameService.findGameById(gameId)).thenReturn(new Game());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/games/finish/{id}", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService, times(1)).saveGame(any(Game.class));
    }

    @Test
    void testFindCreatedGames() throws Exception {
        when(gameService.findCreatedGames()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/created"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService, times(1)).findCreatedGames();
    }

    @Test
    void testFindStartedGames() throws Exception {
        when(gameService.findStartedGames()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/started"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService, times(1)).findStartedGames();
    }

    @Test
    void testFindFinishedGames() throws Exception {
        when(gameService.findFinishedGames()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/finished"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService, times(1)).findFinishedGames();
    }

    @Test
    void testDeleteGame() throws Exception {
        int gameId = 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/games/{gameId}", gameId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(gameService, times(1)).deleteGame(gameId);
    }

    @Test
    void testStartGame() throws Exception {
        int gameId = 1;
        when(gameService.findGameById(gameId)).thenReturn(new Game());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/games/start/{gameId}", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService, times(1)).saveGame(any(Game.class));
    }

}
