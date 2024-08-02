package org.springframework.samples.petclinic.mapas_del_reino.player;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTests {
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService(playerRepository);
    }

    @Test
    void findAll_ShouldReturnAllPlayers() {
        // Arrange
        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        Player player2 = new Player();
        players.add(player1);
        players.add(player2);
        when(playerRepository.findAll()).thenReturn(players);

        // Act
        
        Iterable<Player> result = playerService.findAll();
        List<Player> playerList = StreamSupport.stream(result.spliterator(), false)
        .collect(Collectors.toList());

        // Assert
        assertEquals(2, playerList.size());
    }

    @Test
    void findPlayerById_ExistingId_ShouldReturnPlayer() {
        // Arrange
        int playerId = 1;
        Player player = new Player();
        player.setId(playerId);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // Act
        Player result = playerService.findPlayerById(playerId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void findPlayerById_NonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        int playerId = 1;
        when(playerRepository.findById(playerId)).thenThrow(ResourceNotFoundException.class);
    
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> playerService.findPlayerById(playerId));
    }

    @Test
    void savePlayer_ShouldSavePlayer() {
        // Arrange
        Player player = new Player();
        when(playerRepository.save(player)).thenReturn(player);

        // Act
        Player savedPlayer = playerService.savePlayer(player);

        // Assert
        assertNotNull(savedPlayer);
    }

    @Test
    public void testFinalUpdatePlayer() {
        // Arrange
        int playerId = 1;
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setFirstName("John");
        playerDTO.setLastName("Doe");
        playerDTO.setEmail("john.doe@example.com");

        Player player = new Player();
        player.setId(playerId);
        player.setFirstName("Old First Name");
        player.setLastName("Old Last Name");
        player.setEmail("old.email@example.com");

        when(playerRepository.findById(playerId)).thenReturn(java.util.Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        // Act
        Player updatedPlayer = playerService.finalUpdatePlayer(playerDTO, playerId);

        // Assert
        assertEquals(playerDTO.getFirstName(), updatedPlayer.getFirstName());
        assertEquals(playerDTO.getLastName(), updatedPlayer.getLastName());
        assertEquals(playerDTO.getEmail(), updatedPlayer.getEmail());
        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(1)).save(player);
    }
}
