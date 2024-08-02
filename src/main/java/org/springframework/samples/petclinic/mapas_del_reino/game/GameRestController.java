package org.springframework.samples.petclinic.mapas_del_reino.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.samples.petclinic.mapas_del_reino.game.dice.DiceResult;
import org.springframework.samples.petclinic.mapas_del_reino.game.mensaje.Mensaje;
import org.springframework.samples.petclinic.mapas_del_reino.game.mensaje.MensajeNuevaRondaV2;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/games")
@Tag(name = "Games", description = "The Game management API")
@SecurityRequirement(name = "bearerAuth")
public class GameRestController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public GameRestController(GameService gameService, PlayerService playerService, UserService userService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping
    public ResponseEntity<List<Game>> findAll() {
        return new ResponseEntity<>((List<Game>) gameService.findAll(), HttpStatus.OK);
    }

    @GetMapping("{gameId}")
    public ResponseEntity<Game> findGameById(@PathVariable("gameId") int gameId){
        return new ResponseEntity<>(gameService.findGameById(gameId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> createGame(@RequestBody String name){
        Game game = new Game();
        game.setName(name);
        Integer userId = userService.findCurrentUser().getId();
        Player player = playerService.findPlayerByUserId(userId);
        List<Player> players = new ArrayList<>();
        players.add(player);
        game.setPlayers(players);
        game.setStatus(GameStatus.CREATED);
        game.setLider(player);
        gameService.saveGame(game);
        template.convertAndSend("/topic/gameList", "Game created");
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Game>> findGamesByPlayerId(@PathVariable("playerId") Integer playerId){
        return new ResponseEntity<>(gameService.findGamesByPlayerId(playerId), HttpStatus.OK);
    }

    @PutMapping("/finish/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> finishGame(@PathVariable("id") Integer id){
        Game game = gameService.findGameById(id);
        game.setStatus(GameStatus.FINISHED);
        game.setFinishDate(LocalDateTime.now());
        template.convertAndSend("/topic/gameFinished/"+id, "La partida ha finalizado");
        gameService.saveGame(game);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping("/created")
    public ResponseEntity<List<Game>> findCreatedGames(){
        return new ResponseEntity<>(gameService.findCreatedGames(), HttpStatus.OK);
    }

    @GetMapping("/started")
    public ResponseEntity<List<Game>> findStartedGames(){
        return new ResponseEntity<>(gameService.findStartedGames(), HttpStatus.OK);
    }

    @GetMapping("/finished")
    public ResponseEntity<List<Game>> findFinishedGames(){
        return new ResponseEntity<>(gameService.findFinishedGames(), HttpStatus.OK);
    }

    @PutMapping("/join/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> joinGame(@PathVariable("gameId") Integer gameId){
        Game game = gameService.findGameById(gameId);
        Integer userId = userService.findCurrentUser().getId();
        Player player = playerService.findPlayerByUserId(userId);
        gameService.addPlayerToGame(game.getId(), player.getId());
        gameService.saveGame(game);
        template.convertAndSend("/topic/gameList", "A player has joined a game");
        template.convertAndSend("/topic/lobby/"+gameId, player.getUser().getUsername()+" joined the game!");
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PutMapping("/leave/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> leaveGame(@PathVariable("gameId") Integer gameId){
        Game game = gameService.findGameById(gameId);
        Integer userId = userService.findCurrentUser().getId();
        Player player = playerService.findPlayerByUserId(userId);
        gameService.removePlayerFromGame(game.getId(), player.getId());
        game.setLider(game.getPlayers().get(0));
        gameService.saveGame(game);
        template.convertAndSend("/topic/gameList", "A player has left a game");
        template.convertAndSend("/topic/lobby/"+gameId, player.getUser().getUsername()+" left the game!");
        if(game.getPlayers().size() == 0){
            gameService.deleteGame(gameId);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }
    
    @DeleteMapping("{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteGame(@PathVariable("gameId") Integer gameId){
        template.convertAndSend("/topic/gameDeleted/"+gameId, "true");
        gameService.deleteGame(gameId);
        template.convertAndSend("/topic/gameList", "Game deleted");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/start/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> startGame(@PathVariable("gameId") Integer gameId){
        Game game = gameService.findGameById(gameId);
        game.setStatus(GameStatus.STARTED);
        gameService.saveGame(game);
        template.convertAndSend("/topic/gameList", "This game has started");
        template.convertAndSend("/topic/start/"+gameId, "Game started!");
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @MessageMapping("/game/{gameId}/rollDice")
    @SendTo("/topic/game/{gameId}")
    public DiceResult rollDice(@DestinationVariable String gameId, DiceResult result) {
        // Puedes procesar el resultado de los dados aquí si es necesario
        return result;
    }

    @MessageMapping("/game/{gameId}/finalizarTurno")
    @SendTo("/topic/game/{gameId}/turnoFinalizado")
    public Mensaje sendMessage(@DestinationVariable String gameId, Mensaje message){
        return message;
    }

    @MessageMapping("/game/{gameId}/siguienteRondaV2")
    @SendTo("/topic/game/{gameId}/siguienteRonda")
    public MensajeNuevaRondaV2 enviaIdActivo(@DestinationVariable String gameId, @Payload MensajeNuevaRondaV2 mensaje){
        return mensaje;
    }

    
}
