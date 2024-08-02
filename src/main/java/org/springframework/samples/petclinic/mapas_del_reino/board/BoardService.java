package org.springframework.samples.petclinic.mapas_del_reino.board;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.Casilla;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.TipoCasilla;
import org.springframework.samples.petclinic.mapas_del_reino.game.GameRepository;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

    private BoardRepository br;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;

    @Autowired
    public BoardService(BoardRepository br, PlayerRepository pr, GameRepository gr) {
        this.br = br;
        this.playerRepository = pr;
        this.gameRepository = gr;
    }

    @Transactional(readOnly = true)
    public List<Board> findAll() {
        return br.findAll();
    }

    @Transactional(readOnly = true)
    public Board findBoardById(Integer id) throws DataAccessException {
        return br.findById(id).orElseThrow(() -> new ResourceNotFoundException("Board", "ID", id));
    }

    @Transactional(readOnly = true)
    public List<Board> findBoardsByPlayerId(Integer id) {
        return br.findByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public List<Board> findBoardsByGameId(Integer id) {
        return br.findByGameId(id);
    }

    @Transactional(readOnly = true)
    public Board findBoardByGameIdAndUserId(Integer gameId, Integer UserId) {
        Integer playerId = playerRepository.findByUserId(UserId).getId();
        return br.findByGameIdAndPlayerId(gameId, playerId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Board save(Board board) {
        return br.save(board);
    }

    @Transactional(readOnly = true)
    public Board updateBoard(Board board, Integer id) {
        Board bToUpdate = findBoardById(id);
        BeanUtils.copyProperties(board, bToUpdate, "id");
        return save(bToUpdate);

    }

    @Transactional
    public Board createBoard(BoardDTO2 boardDTO2) {

        Board board = new Board();
        board.setPlayer(playerRepository.findByUserId(boardDTO2.getPlayerId()));
        board.setGame(gameRepository.findById(boardDTO2.getGameId()).orElse(null));
        board.setCriterioA1(Criterio.valueOf(boardDTO2.getCriterioA1()));
        board.setCriterioA2(Criterio.valueOf(boardDTO2.getCriterioA2()));
        board.setCriterioB1(Criterio.valueOf(boardDTO2.getCriterioB1()));
        board.setCriterioB2(Criterio.valueOf(boardDTO2.getCriterioB2()));

        board.setPoints(0);
        board.setCasillas(creaCasillasIniciales());

        br.save(board);
        return board;

    }

    private List<Casilla> creaCasillasIniciales() {
        List<Casilla> casillas = new ArrayList<>();

        int[][] coordinates = {
            {-4, 0, 4}, {-4, 1, 3}, {-4, 2, 2}, {-4, 3, 1}, {-4, 4, 0},
            {-3, -1, 4}, {-3, 0, 3}, {-3, 1, 2}, {-3, 2, 1}, {-3, 3, 0}, {-3, 4, -1},
            {-2, -2, 4}, {-2, -1, 3}, {-2, 0, 2}, {-2, 1, 1}, {-2, 2, 0}, {-2, 3, -1}, {-2, 4, -2},
            {-1, -3, 4}, {-1, -2, 3}, {-1, -1, 2}, {-1, 0, 1}, {-1, 1, 0}, {-1, 2, -1}, {-1, 3, -2}, {-1, 4, -3},
            {0, -4, 4}, {0, -3, 3}, {0, -2, 2}, {0, -1, 1}, {0, 0, 0}, {0, 1, -1}, {0, 2, -2}, {0, 3, -3}, {0, 4, -4},
            {1, -4, 3}, {1, -3, 2}, {1, -2, 1}, {1, -1, 0}, {1, 0, -1}, {1, 1, -2}, {1, 2, -3}, {1, 3, -4},
            {2, -4, 2}, {2, -3, 1}, {2, -2, 0}, {2, -1, -1}, {2, 0, -2}, {2, 1, -3}, {2, 2, -4},
            {3, -4, 1}, {3, -3, 0}, {3, -2, -1}, {3, -1, -2}, {3, 0, -3}, {3, 1, -4},
            {4, -4, 0}, {4, -3, -1}, {4, -2, -2}, {4, -1, -3}, {4, 0, -4}
        };

        for (int[] coord : coordinates) {
            if(coord[0] == 0 && coord[1] == -3 && coord[2] == 3 ||
                coord[0] == 3 && coord[1] == -3 && coord[2] == 0 ||
                coord[0] == -4 && coord[1] == 0 && coord[2] == 4 ||
                coord[0] == 4 && coord[1] == 0 && coord[2] == 4 ||
                coord[0] == -3 && coord[1] == 3 && coord[2] == 0 ||
                coord[0] == 0 && coord[1] == 3 &&coord[2] == -3 ||
                coord[0] == 4 && coord[1] == 0 && coord[2] == -4 
            ) {
                
                casillas.add(new Casilla(coord[0], coord[1], coord[2], TipoCasilla.PODER_MASMENOS));

        }
        else if(coord[0]== 0 && coord[1]==0 && coord[2] == 0 ){
            casillas.add(new Casilla(coord[0], coord[1], coord[2], TipoCasilla.PODER_INTERROGACION));
        }
        else{
            casillas.add(new Casilla(coord[0], coord[1], coord[2], TipoCasilla.VACIA));
        }
        }

        return casillas;
    }
  
    @Transactional
    public Board colocaCasilla(Integer id, List<Integer> coords, String tipoCasilla) {
        Board board = br.findById(id).orElse(null);
        if (board != null) {
            List<Casilla> casillas = board.getCasillas();
            boolean casillaNoEsVacia = casillas.stream().anyMatch(c -> c.getQ() == coords.get(0) && c.getR() == coords.get(1) && c.getS() == coords.get(2) && c.getTipoCasilla() != TipoCasilla.VACIA && c.getTipoCasilla() != TipoCasilla.PODER_MASMENOS && c.getTipoCasilla() != TipoCasilla.PODER_INTERROGACION);  
            Casilla casillaSeleccionada = casillas.stream().filter(c -> c.getQ() == coords.get(0) && c.getR() == coords.get(1) && c.getS() == coords.get(2)).findFirst().orElse(null);
            List<int[]> adyacencias = calculaAdyacencias(casillaSeleccionada);
            boolean tableroVacio = board.getCasillas().stream().allMatch(c -> tableroVacio(c));
            boolean noPuedoColocar = casillas.stream().filter(x-> adyacencias.stream().anyMatch(y-> y[0] == x.getQ() && y[1] == x.getR() && y[2] == x.getS())).allMatch(c -> c.getTipoCasilla() == TipoCasilla.VACIA || 
            c.getTipoCasilla() == TipoCasilla.PODER_MASMENOS || c.getTipoCasilla() == TipoCasilla.PODER_INTERROGACION);
    
            if (!tableroVacio) {
                if(noPuedoColocar){
                    throw new RuntimeException("No se puede colocar una si las adyacentes estan todas vacías.");
                }
                else if(casillaNoEsVacia){
                    throw new RuntimeException("No se puede colocar una casilla en una casilla que no sea vacía.");
                }
                else{
                    List<Casilla> casillasUpdateadas = new ArrayList<>(board.getCasillas().stream()
                    .map(casilla -> cambiaCasilla(casilla, coords, tipoCasilla))
                    .collect(Collectors.toList()));
                    board.setCasillas(casillasUpdateadas);

                }
                    
            }

            else{
            
            List<Casilla> casillasUpdateadas = new ArrayList<>(board.getCasillas().stream()
                    .map(casilla -> cambiaCasilla(casilla, coords, tipoCasilla))
                    .collect(Collectors.toList()));
                    board.setCasillas(casillasUpdateadas);
                }

            
            br.save(board);
        }
        return board;
    }

    @Transactional
    public Board eliminaCasilla(Integer id, List<Integer> coords, String tipoCasilla){
        Board board = br.findById(id).orElse(null);
        if (board != null){
            List<Casilla> casillas = board.getCasillas();
            Casilla casillaSeleccionada = casillas.stream().filter(c -> c.getQ() == coords.get(0) && c.getR() == coords.get(1) && c.getS() == coords.get(2)).findFirst().orElse(null);
            if(casillaSeleccionada.getQ() == 0 && casillaSeleccionada.getR() == 0 && casillaSeleccionada.getS() == 0){
                List<Casilla> casillasUpdateadas = new ArrayList<>(board.getCasillas().stream()
                .map(casilla -> cambiaCasilla(casilla, coords, "PODER_INTERROGACION"))
                .collect(Collectors.toList()));
                board.setCasillas(casillasUpdateadas);
            }
            else if(casillaSeleccionada.getQ() == -4 && casillaSeleccionada.getR() == 0 && casillaSeleccionada.getS() == 4 ||
            casillaSeleccionada.getQ() == 4 && casillaSeleccionada.getR() == 0 && casillaSeleccionada.getS() == 4 ||
            casillaSeleccionada.getQ() == -3 && casillaSeleccionada.getR() == 3 && casillaSeleccionada.getS() == 0 ||
            casillaSeleccionada.getQ() == 0 && casillaSeleccionada.getR() == 3 && casillaSeleccionada.getS() == -3 ||
            casillaSeleccionada.getQ() == 4 && casillaSeleccionada.getR() == 0 && casillaSeleccionada.getS() == -4 ||
            casillaSeleccionada.getQ() == -4 && casillaSeleccionada.getR() == 0 && casillaSeleccionada.getS() == 4){
                List<Casilla> casillasUpdateadas = new ArrayList<>(board.getCasillas().stream()
                .map(casilla -> cambiaCasilla(casilla, coords, "PODER_MASMENOS"))
                .collect(Collectors.toList()));
                board.setCasillas(casillasUpdateadas);
            }
            else{
                List<Casilla> casillasUpdateadas = new ArrayList<>(board.getCasillas().stream()
                .map(casilla -> cambiaCasilla(casilla, coords, "VACIA"))
                .collect(Collectors.toList()));
                board.setCasillas(casillasUpdateadas);
            }

        }

        br.save(board);
        return board;

    }



    private boolean tableroVacio(Casilla c) {

        return c.getTipoCasilla() == TipoCasilla.VACIA || c.getTipoCasilla() == TipoCasilla.PODER_INTERROGACION
                || c.getTipoCasilla() == TipoCasilla.PODER_MASMENOS;

    }

    private Casilla cambiaCasilla(Casilla casilla, List<Integer> coordenada, String tipoCasilla) {
        if (casilla.getQ() == coordenada.get(0) && casilla.getR() == coordenada.get(1)
                && casilla.getS() == coordenada.get(2)) {
            casilla.setTipoCasilla(TipoCasilla.valueOf(tipoCasilla));
        }

        return casilla;

    }

    private List<int[]> calculaAdyacencias(Casilla casilla) {

        List<int[]> adyacentes = new ArrayList<>();
        int[][] directions = {
            {1, -1, 0}, {1, 0, -1}, {0, 1, -1},
            {-1, 1, 0}, {-1, 0, 1}, {0, -1, 1}
        };

        for (int[] direction : directions) {
            int[] adj = {casilla.getQ() + direction[0], casilla.getR() + direction[1], casilla.getS()
                 + direction[2]};
            adyacentes.add(adj);
        }

        return adyacentes;
    }

    @Transactional
    public Board calculaPuntuacionFinal(Integer id){
        Board board = br.findById(id).orElse(null);
        int puntos = 0;

        switch (board.getCriterioA1()) {
            case UNO:
                puntos += CalculadoraCriterios.calculaCriterioA1(board.getCasillas());
                break;
            case DOS:
                puntos += CalculadoraCriterios.calculaCriterioA2(board.getCasillas());
                break;
            case TRES:
                puntos += CalculadoraCriterios.calculaCriterioA3(board.getCasillas());
                break;
            case CUATRO:
                puntos += CalculadoraCriterios.calculaCriterioA4(board.getCasillas());
                break;
            case CINCO:
                puntos += CalculadoraCriterios.calculaCriterioA5(board.getCasillas());
                break;
            case SEIS:
                puntos += CalculadoraCriterios.calculaCriterioA6(board.getCasillas());
                break;
            default:
                break;
        }
        switch (board.getCriterioA2()) {
            case UNO:
                puntos += CalculadoraCriterios.calculaCriterioA1(board.getCasillas());
                break;
            case DOS:
                puntos += CalculadoraCriterios.calculaCriterioA2(board.getCasillas());
                break;
            case TRES:
                puntos += CalculadoraCriterios.calculaCriterioA3(board.getCasillas());
                break;
            case CUATRO:
                puntos += CalculadoraCriterios.calculaCriterioA4(board.getCasillas());
                break;
            case CINCO:
                puntos += CalculadoraCriterios.calculaCriterioA5(board.getCasillas());
                break;
            case SEIS:
                puntos += CalculadoraCriterios.calculaCriterioA6(board.getCasillas());
                break;
            default:
                break;
        }
        switch (board.getCriterioB1()) {
            case UNO:
                puntos += CalculadoraCriterios.calculaCriterioB1(board.getCasillas());
                break;
            case DOS:
                puntos += CalculadoraCriterios.calculaCriterioB2(board.getCasillas());
                break;
            case TRES:
                puntos += CalculadoraCriterios.calculaCriterioB3(board.getCasillas());
                break;
            case CUATRO:
                puntos += CalculadoraCriterios.calculaCriterioB4(board.getCasillas());
                break;
            case CINCO:
                puntos += CalculadoraCriterios.calculaCriterioB5(board.getCasillas());
                break;
            case SEIS:
                puntos += CalculadoraCriterios.calculaCriterioB6(board.getCasillas());
                break;
            default:
                break;
        }
        switch (board.getCriterioB2()) {
            case UNO:
                puntos += CalculadoraCriterios.calculaCriterioA1(board.getCasillas());
                break;
            case DOS:
                puntos += CalculadoraCriterios.calculaCriterioB2(board.getCasillas());
                break;
            case TRES:
                puntos += CalculadoraCriterios.calculaCriterioB3(board.getCasillas());
                break;
            case CUATRO:
                puntos += CalculadoraCriterios.calculaCriterioB4(board.getCasillas());
                break;
            case CINCO:
                puntos += CalculadoraCriterios.calculaCriterioB5(board.getCasillas());
                break;
            case SEIS:
                puntos += CalculadoraCriterios.calculaCriterioB6(board.getCasillas());
                break;
            default:
                break;
        }
        board.setPoints(board.getPoints()+puntos);
        br.save(board);
        return board;

    }

    @Transactional
    public Board calculaPuntuacionInterrogacionCriterioA(Integer id, Integer criterioA){
        Board board = br.findById(id).orElse(null);
        int puntos = 0;
        switch(criterioA){
            case 1:
                puntos += CalculadoraCriterios.calculaCriterioA1(board.getCasillas());
                break;
            case 2:
                puntos += CalculadoraCriterios.calculaCriterioA2(board.getCasillas());
                break;
            case 3:
                puntos += CalculadoraCriterios.calculaCriterioA3(board.getCasillas());
                break;
            case 4:
                puntos += CalculadoraCriterios.calculaCriterioA4(board.getCasillas());
                break;
            case 5:
                puntos += CalculadoraCriterios.calculaCriterioA5(board.getCasillas());
                break;
            case 6:
                puntos += CalculadoraCriterios.calculaCriterioA6(board.getCasillas());
                break;
            default:
                break;
        }

        board.setPoints(board.getPoints()+puntos);
        br.save(board);
        return board;
    }

    @Transactional
    public Board calculaPuntuacionInterrogacionCriterioB(Integer id, Integer criterioB){
        Board board = br.findById(id).orElse(null);
        int puntos = 0;
        switch(criterioB){
            case 1:
                puntos += CalculadoraCriterios.calculaCriterioB1(board.getCasillas());
                break;
            case 2:
                puntos += CalculadoraCriterios.calculaCriterioB2(board.getCasillas());
                break;
            case 3:
                puntos += CalculadoraCriterios.calculaCriterioB3(board.getCasillas());
                break;
            case 4:
                puntos += CalculadoraCriterios.calculaCriterioB4(board.getCasillas());
                break;
            case 5:
                puntos += CalculadoraCriterios.calculaCriterioB5(board.getCasillas());
                break;
            case 6:
                puntos += CalculadoraCriterios.calculaCriterioB6(board.getCasillas());
                break;
            default:
                break;
        }

        board.setPoints(board.getPoints()+puntos);
        br.save(board);
        return board;

    }

    @Transactional
    public void eliminarTablerosDeJugador(int userId){
        Player player = playerRepository.findByUserId(userId);
        List<Board> boards = br.findByPlayerId(player.getId());
        if(boards.size() != 0){
            for(Board board : boards){
                br.delete(board);
            }
        }
    }

}

