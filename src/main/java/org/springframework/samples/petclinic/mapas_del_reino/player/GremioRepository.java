package org.springframework.samples.petclinic.mapas_del_reino.player;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GremioRepository extends CrudRepository<Gremio, Integer>{

    @Query("SELECT g FROM Gremio g WHERE g.gremio = :gremio")
    Optional<Gremio> findByGremio(String gremio);
    
}
