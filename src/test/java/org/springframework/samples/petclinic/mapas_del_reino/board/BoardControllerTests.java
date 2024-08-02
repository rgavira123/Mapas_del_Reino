package org.springframework.samples.petclinic.mapas_del_reino.board;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.samples.petclinic.mapas_del_reino.game.Game;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTests {

    @Mock
    private BoardService boardService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BoardRestController boardRestController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindAll() throws Exception {
        reset(boardService);
        Board board = new Board();
        board.setId(1);
        Board board2 = new Board();
        board2.setId(2);
        List<Board> boards = List.of(board, board2);
        when(boardService.findAll()).thenReturn(boards);

        mockMvc.perform(get("/api/v1/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    @WithMockUser(username = "player1", authorities = {"PLAYER"})
    public void testFindBoardsByGameId() throws Exception {
        // Arrange
        reset(boardService);
        Board board = new Board();
        board.setId(1);
        Board board2 = new Board();
        board2.setId(2);
        Integer gameId = 1;
        Game game = new Game();
        game.setId(gameId);
        board.setGame(game);
        board2.setGame(game);
        List<Board> boards = List.of(board, board2);
        when(boardService.findBoardsByGameId(gameId)).thenReturn(boards);

        // Act & Assert
        mockMvc.perform(get("/api/v1/boards/game/" + gameId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}