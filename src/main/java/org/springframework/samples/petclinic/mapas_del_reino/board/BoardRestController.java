package org.springframework.samples.petclinic.mapas_del_reino.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.CasillaRequest;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.TileSelection;

@RestController
@RequestMapping("/api/v1/boards")
@Tag(name = "Boards", description = "The Board management API")
@CrossOrigin(origins = "http://localhost:3000")
public class BoardRestController {
    
    private final BoardService bs;

    private SimpMessagingTemplate template;

    @Autowired
    public BoardRestController(BoardService bs, SimpMessagingTemplate template) {
        this.bs = bs;
        this.template = template;
    }

    @GetMapping()
    public ResponseEntity<List<Board>> findAll() {
        return new ResponseEntity<>((List<Board>) bs.findAll(), HttpStatus.OK);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Board>> findBoardsByGameId(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>((List<Board>) bs.findBoardsByGameId(gameId), HttpStatus.OK);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Board>> findBoardsByPlayerId(@PathVariable("playerId") Integer playerId) {
        return new ResponseEntity<>((List<Board>) bs.findBoardsByPlayerId(playerId), HttpStatus.OK);
    }

    @GetMapping("/game/{gameId}/player/{playerId}")
    public ResponseEntity<Board> findBoardByGameIdAndUserId(@PathVariable("gameId") Integer gameId, @PathVariable("playerId") Integer userId) {
        return new ResponseEntity<>(bs.findBoardByGameIdAndUserId(gameId, userId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> findBoardById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(bs.findBoardById(id), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Board> createBoard(@RequestBody @Valid BoardDTO2 boardDTO2){
        Board createdBoard = bs.createBoard(boardDTO2);
        template.convertAndSend("/topic/board/tableroCreado/"+boardDTO2.getGameId(), "Tablero creado");
        return new ResponseEntity<>(createdBoard, HttpStatus.CREATED);
    }

    @MessageMapping("boards/create/{gameId}")
    @SendTo("/topic/boards/create/{gameId}")
    public BoardDTO2 devuelveTablero(@DestinationVariable String gameId, BoardDTO2 tablero) {
        // Puedes procesar el resultado de los dados aquí si es necesario
        return tablero;
    }

    @PutMapping("/{id}/setCasilla")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Board> colocaCasilla(@PathVariable("id") Integer id, @RequestBody @Valid CasillaRequest request){
        Board updatedBoard = bs.colocaCasilla(id, request.getCoordenada(), request.getTipoCasilla());
        template.convertAndSend("/topic/boards/setCasilla/"+id, request);
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @PutMapping("/{id}/deleteCasilla")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Board> eliminaCasilla(@PathVariable("id") Integer id, @RequestBody @Valid CasillaRequest request){
        Board updatedBoard = bs.eliminaCasilla(id, request.getCoordenada(), request.getTipoCasilla());
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @MessageMapping("/boards/setTileType/{gameId}")
    @SendTo("/topic/boards/choosedTileType/{gameId}")
    public TileSelection showDices(@DestinationVariable String gameId, TileSelection ficha){
        return ficha;
    }

    @PutMapping("/{id}/setPuntuacionFinal")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Board> calculaPuntuacionFinal(@PathVariable("id") Integer id){
        Board updatedBoard = bs.calculaPuntuacionFinal(id);

        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);

    }

    //ESTOS DOS METODOS DE ABAJO SE LLAMAN CUADNO ALGUIEN COLOCA EN INTERROGACIÓN.

    //este criterio A hacer referencia al dado elegido, dado1 y dado 2 son criterio A
    @PutMapping("/{id}/setPuntuacionInterrogacion/criteriosA/{criterioA}")
    public ResponseEntity<Board> calculaPuntuacionInterrogacionCriterioA(@PathVariable("id") Integer id, @PathVariable("criterioA") Integer criterioA){
        Board updatedBoard = bs.calculaPuntuacionInterrogacionCriterioA(id, criterioA);

        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);

    }

    //este criterio B hacer referencia al dado elegido, dado3 y dado 4 son criterio B, 
    @PutMapping("/{id}/setPuntuacionInterrogacion/criteriosB/{criterioB}")
    public ResponseEntity<Board> calculaPuntuacionInterrogacionCriterioB(@PathVariable("id") Integer id, @PathVariable("criterioB") Integer criterioB){
        Board updatedBoard = bs.calculaPuntuacionInterrogacionCriterioB(id, criterioB);

        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);

    }



    
}
