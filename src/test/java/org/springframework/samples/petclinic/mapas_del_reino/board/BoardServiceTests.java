package org.springframework.samples.petclinic.mapas_del_reino.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.Casilla;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.TipoCasilla;
import org.springframework.samples.petclinic.mapas_del_reino.game.Game;
import org.springframework.samples.petclinic.mapas_del_reino.game.GameRepository;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerRepository;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTests {

    @InjectMocks
    private BoardService boardService;
    
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    public void setup() {
        // No es necesario inicializar manualmente BoardService aquí
        // porque @InjectMocks lo hace automáticamente
    }

    @Test
    public void testCreateBoard() {
        // Arrange
        BoardDTO2 boardDTO2 = new BoardDTO2();
        boardDTO2.setPlayerId(1);
        boardDTO2.setGameId(1);
        boardDTO2.setCriterioA1("UNO");
        boardDTO2.setCriterioA2("DOS");
        boardDTO2.setCriterioB1("TRES");
        boardDTO2.setCriterioB2("CUATRO");

        Player player = new Player();
        player.setId(1);
        Game game = new Game();
        game.setId(1);

        when(playerRepository.findByUserId(boardDTO2.getPlayerId())).thenReturn(player);
        when(gameRepository.findById(boardDTO2.getGameId())).thenReturn(Optional.of(game));

        // Act
        Board createdBoard = boardService.createBoard(boardDTO2);

        // Assert
        verify(boardRepository, times(1)).save(any(Board.class));
        assertEquals(player, createdBoard.getPlayer());
        assertEquals(game, createdBoard.getGame());
        assertEquals("UNO", createdBoard.getCriterioA1().toString());
        assertEquals("DOS", createdBoard.getCriterioA2().toString());
        assertEquals("TRES", createdBoard.getCriterioB1().toString());
        assertEquals("CUATRO", createdBoard.getCriterioB2().toString());
    }

    @Test
    public void testColocaCasilla() {
        // Arrange
        Board board = new Board();
        board.setId(1);
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.VACIA));
        board.setCasillas(casillas);

        List<Integer> coords = new ArrayList<>();
        coords.add(0);
        coords.add(0);
        coords.add(0);
        String tipoCasilla = "CASTILLO";

        when(boardRepository.findById(1)).thenReturn(Optional.of(board));

        // Act
        Board result = boardService.colocaCasilla(1, coords, tipoCasilla);

        // Assert
        assertEquals(TipoCasilla.CASTILLO, result.getCasillas().get(0).getTipoCasilla());
    }
    
    @Test
    public void testEliminaCasilla() {
        // Arrange
        Integer boardId = 1;
        List<Integer> coords = new ArrayList<>();
        coords.add(0);
        coords.add(0);
        coords.add(0);
        String tipoCasilla = "VACIA";

        Board board = new Board();
        board.setId(boardId);
        List<Casilla> casillas = new ArrayList<>();
        Casilla casilla = new Casilla();
        casilla.setQ(0);
        casilla.setR(0);
        casilla.setS(0);
        casilla.setTipoCasilla(TipoCasilla.PODER_MASMENOS);
        casillas.add(casilla);
        board.setCasillas(casillas);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // Act
        Board result = boardService.eliminaCasilla(boardId, coords, tipoCasilla);

        // Assert
        assertEquals(TipoCasilla.PODER_INTERROGACION, result.getCasillas().get(0).getTipoCasilla());
    }

    @Test
    void eliminarTablerosDeJugador_ShouldDeleteBoards() {
        // Arrange
        int userId = 1;
        Player player = new Player();
        player.setId(1);
        List<Board> boards = new ArrayList<>();
        boards.add(new Board());
        boards.add(new Board());

        when(playerRepository.findByUserId(userId)).thenReturn(player);
        when(boardRepository.findByPlayerId(player.getId())).thenReturn(boards);

        // Act
        boardService.eliminarTablerosDeJugador(userId);

        // Assert
        verify(boardRepository, times(2)).delete(any(Board.class));
    }

     @Test
    public void testCalculaCriterioA1() {
        // Create a list of casillas
        List<Casilla> casillas = new ArrayList<>();

        // Add casillas surrounding a castle
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.PRADERA));
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.PRADERA));
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.PRADERA));
        casillas.add(new Casilla(0, -1, 1, TipoCasilla.PRADERA));
        casillas.add(new Casilla(-1, 0, 1, TipoCasilla.PRADERA));
        casillas.add(new Casilla(-1, 1, 0, TipoCasilla.PRADERA));

        // Add an empty casilla in the center
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.VACIA));

        // Add a castle casilla
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.CASTILLO));

        // Calculate the points using the calculaCriterioA1 method
        int puntos = CalculadoraCriterios.calculaCriterioA1(casillas);

        // Assert that the points are correct
        assertEquals(2, puntos);
    }

     @Test
    public void testCalculaCriterioA2() {
        // Create a list of casillas
        List<Casilla> casillas = new ArrayList<>();

        // Add casillas with the required adjacency conditions
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.PRADERA)); // Pradera with adjacent Montaña and Río
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.MONTAÑA)); // Montaña adjacent to Pradera
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.RIO)); // Río adjacent to Pradera
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.BOSQUE)); // Bosque adjacent to Pradera

        // Call the method to calculate the points
        int puntos = CalculadoraCriterios.calculaCriterioA2(casillas);

        // Assert that the calculated points are correct
        assertEquals(4, puntos);
    }

    @Test
    public void testCalculaCriterioA3() {
        // Create a list of casillas representing the game board
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-4, 0, 4, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-4, 1, 3, TipoCasilla.BOSQUE));
        

        // Call the method to calculate the score
        int score = CalculadoraCriterios.calculaCriterioA3(casillas);

        // Assert that the score is correct
        assertEquals(4, score);
    }

    @Test
    public void testCalculaCriterioA4() {
        // Create a list of casillas
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.PRADERA));
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.RIO));
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.RIO));
        casillas.add(new Casilla(1, 1, -2, TipoCasilla.PRADERA));

        // Call the method to calculate the points
        int puntos = CalculadoraCriterios.calculaCriterioA4(casillas);

        // Assert that the points are calculated correctly
        assertEquals(4, puntos);
    }

    @Test
    public void testCalculaCriterioA5() {
        // Create a list of Casilla objects for testing
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(-4, 0, 4, TipoCasilla.PUEBLO));
        casillas.add(new Casilla(-4,2,2,TipoCasilla.PUEBLO));
        casillas.add(new Casilla(-4,4,0, TipoCasilla.PUEBLO));
        casillas.add(new Casilla(-2,-2,-4, TipoCasilla.PUEBLO));

        // Call the method being tested
        int result = CalculadoraCriterios.calculaCriterioA5(casillas);

        // Assert the expected result
        assertEquals(20, result);
    }

    @Test
    public void testCalculaCriterioA6() {
        // Create a list of casillas with mountains on the borders
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(4, -4, 0, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(4, -3, -1, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(4, -2, -2, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(4, -1, -3, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(4, 0, -4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(-4, 4, 0, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(-4, 3, 1, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(-4, 2, 2, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(-4, 1, 3, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(-4, 0, 4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(0, 4, -4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(1, 3, -4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(2, 2, -4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(3, 1, -4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(4, 0, -4, TipoCasilla.MONTAÑA));

        // Call the method to calculate the points
        int puntos = CalculadoraCriterios.calculaCriterioA6(casillas);

        // Assert that the points are correct
        assertEquals(15, puntos);
    }

    @Test
    public void testCalculaCriterioB1() {
        // Create a list of casillas representing the game board
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(-4, 0, 4, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(-4, 1, 3, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(0, -1, 1, TipoCasilla.MONTAÑA));

        // Call the method to calculate the score
        int score = CalculadoraCriterios.calculaCriterioB1(casillas);

        // Assert that the score is as expected
        assertEquals(3, score);
    }

    @Test
    public void testCalculaCriterioB2() {
        // Create a list of casillas for testing
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.PRADERA));
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.PUEBLO));
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.CASTILLO));
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.CASTILLO));

        // Call the method to calculate the criteria
        int result = CalculadoraCriterios.calculaCriterioB2(casillas);

        // Assert the expected result
        assertEquals(4, result);
    }

    @Test
    public void testCalculaCriterioB3() {
        // Create a list of Casillas for testing
        List<Casilla> casillas = new ArrayList<>();
        // Add Casillas to the list
        casillas.add(new Casilla(4, -1, -3, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(3, 0, -3, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(2, 1, -3, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(1, 2, -3, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(0, 3, -3, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-1, 3, -2, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-2, 3, -1, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-3, 3, 0, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-4, 3, 1, TipoCasilla.BOSQUE));

        // Test case 1: No path from Q = 4 to Q = -4 with bosques
        int expectedPoints1 = 10;
        int actualPoints1 = CalculadoraCriterios.calculaCriterioB3(casillas);
        assertEquals(expectedPoints1, actualPoints1);
    }

    @Test
    public void testCalculaCriterioB4() {
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.CASTILLO));
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.RIO));
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-1, 1, 0, TipoCasilla.PRADERA));
        casillas.add(new Casilla(-1, 0, 1, TipoCasilla.PUEBLO));
        casillas.add(new Casilla(0, -1, 1, TipoCasilla.CASTILLO));

        int expectedPuntos = 12;
        int actualPuntos = CalculadoraCriterios.calculaCriterioB4(casillas);

        assertEquals(expectedPuntos, actualPuntos);
    }

    @Test
    public void testCalculaCriterioB5() {
        // Create a list of casillas
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.RIO));
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.PRADERA));
        casillas.add(new Casilla(-1, 1, 0, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(-1, 0, 1, TipoCasilla.BOSQUE));
        casillas.add(new Casilla(0, -1, 1, TipoCasilla.BOSQUE));

        // Call the method to calculate the criteria
        int result = CalculadoraCriterios.calculaCriterioB5(casillas);

        // Assert the expected result
        assertEquals(2, result);
    }

    @Test
    public void testCalculaCriterioB6() {
        // Create a list of casillas with the desired conditions
        List<Casilla> casillas = new ArrayList<>();
        casillas.add(new Casilla(0, 0, 0, TipoCasilla.PUEBLO));
        casillas.add(new Casilla(1, -1, 0, TipoCasilla.MONTAÑA));
        casillas.add(new Casilla(1, 0, -1, TipoCasilla.RIO));
        casillas.add(new Casilla(0, 1, -1, TipoCasilla.BOSQUE));

        // Call the method to calculate the points
        int puntos = CalculadoraCriterios.calculaCriterioB6(casillas);

        // Assert that the points are calculated correctly
        assertEquals(8, puntos);
    }

}
