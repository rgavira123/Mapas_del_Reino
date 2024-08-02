package org.springframework.samples.petclinic.mapas_del_reino.player;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players", description = "The Player management API")
@SecurityRequirement(name = "bearerAuth")
public class PlayerRestController {

    private final PlayerService playerService;
    private final UserService   userService;

    @Autowired
    public PlayerRestController(PlayerService playerService, UserService userService) {
        this.playerService = playerService;
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<Player>> findAll() {
        return new ResponseEntity<>((List<Player>) playerService.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> create(@RequestBody @Valid Player player) {
        Player newPlayer = new Player();
        BeanUtils.copyProperties(player, newPlayer, "id");
        User user = userService.findCurrentUser();
        newPlayer.setUser(user);
        Player savedPlayer = this.playerService.savePlayer(newPlayer);

        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
        
    }

    @GetMapping(value = "{playerId}")
    public ResponseEntity<Player> findById(@PathVariable("playerId") int id) {
        return new ResponseEntity<>(playerService.findPlayerById(id), HttpStatus.OK);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Player> update(@PathVariable("id") int id, @RequestBody @Valid PlayerDTO playerDTO){
        Player playerUpdated = playerService.finalUpdatePlayer(playerDTO, id);
        return new ResponseEntity<>(playerUpdated, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Player> findByUserId(@PathVariable("userId") int userId) {
        return new ResponseEntity<>(userService.findPlayerByUser(userId).get(), HttpStatus.OK);
    }
}
