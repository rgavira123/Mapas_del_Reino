package org.springframework.samples.petclinic.mapas_del_reino.game;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends CrudRepository<Game, Integer>{

    List<Game> findAll();

    @Query("SELECT g FROM Game g WHERE g.name = :name")
    Game findByName(String name);

    // De momento siempre que obtengamos las partidas de los jugadores serán partidas finalizadas
    // En un futuro, si se implementa la funcionalidad de partidas en curso, habrá que crear un nuevo método
    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id = :playerId AND g.status = FINISHED")
    List<Game> findGamesByPlayerId(@Param("playerId") int playerId);

    @Query("SELECT g FROM Game g WHERE g.status = CREATED")
    List<Game> findCreatedGames();

    @Query("SELECT g FROM Game g WHERE g.status = STARTED")
    List<Game> findStartedGames();

    @Query("SELECT g FROM Game g WHERE g.status = FINISHED")
    List<Game> findFinishedGames();

    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id = :playerId")
    List<Game> findAllGamesByPlayerId(@Param("playerId") int playerId);
}
