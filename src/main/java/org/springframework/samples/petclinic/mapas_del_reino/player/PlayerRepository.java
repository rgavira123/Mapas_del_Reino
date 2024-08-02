package org.springframework.samples.petclinic.mapas_del_reino.player;
import org.springframework.data.repository.CrudRepository;



public interface PlayerRepository extends CrudRepository<Player, Integer> {

    Player findByUserId(Integer userId);


}
