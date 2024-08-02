package org.springframework.samples.petclinic.mapas_del_reino.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Integer> {
    
    List<Board> findAll();

    @Query("SELECT b FROM Board b WHERE b.player.id = :playerId")
    List<Board> findByPlayerId(Integer playerId);
    @Query("SELECT b FROM Board b WHERE b.game.id = :gameId")
    List<Board> findByGameId(Integer gameId);
    @Query("SELECT b FROM Board b WHERE b.player.id = :playerId AND b.game.id = :gameId")
    Optional<Board> findByGameIdAndPlayerId(Integer gameId, Integer playerId);
}
