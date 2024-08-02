package org.springframework.samples.petclinic.mapas_del_reino.player;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc

public class PlayerControllerTests {

    @MockBean
    private PlayerService playerService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Player player1;
    private Player player2;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("Player One");

        player1.setEmail("player1@example.com");
        player1.setUser(user);

        player2 = new Player();
        player2.setId(2);
        player1.setFirstName("Player Two");
        player2.setEmail("player2@example.com");
        player2.setUser(user);
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindAll() throws Exception {
        List<Player> players = List.of(player1, player2);

        when(playerService.findAll()).thenReturn(players);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/players"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(player1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(player2.getId()));

        verify(playerService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindById() throws Exception {
        when(playerService.findPlayerById(player1.getId())).thenReturn(player1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/players/{id}", player1.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(player1.getId()));

        verify(playerService, times(1)).findPlayerById(player1.getId());
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testUpdate() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setFirstName("Updated Name");
        playerDTO.setLastName("Updated LastName");
        playerDTO.setEmail("updated@example.com");

        Player updatedPlayer = new Player();
        updatedPlayer.setId(player1.getId());
        updatedPlayer.setFirstName(playerDTO.getFirstName());
        updatedPlayer.setLastName(playerDTO.getLastName());
        updatedPlayer.setEmail(playerDTO.getEmail());
        updatedPlayer.setUser(user);

        when(playerService.finalUpdatePlayer(ArgumentMatchers.any(PlayerDTO.class), ArgumentMatchers.eq(player1.getId())))
                .thenReturn(updatedPlayer);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/players/{id}", player1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(player1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(playerDTO.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(playerDTO.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(playerDTO.getEmail()));

        verify(playerService, times(1)).finalUpdatePlayer(ArgumentMatchers.any(PlayerDTO.class), ArgumentMatchers.eq(player1.getId()));
    }
}
