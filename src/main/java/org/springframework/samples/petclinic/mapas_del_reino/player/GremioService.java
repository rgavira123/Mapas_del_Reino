package org.springframework.samples.petclinic.mapas_del_reino.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GremioService {

    private GremioRepository gremioRepository;

    @Autowired
    public GremioService(GremioRepository gremioRepository) {
        this.gremioRepository = gremioRepository;
    }

    @Transactional(readOnly = true)
    public Iterable<Gremio> findAll() {
        return this.gremioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Gremio findById(int id) {
        return this.gremioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gremio", "id", id));
    }

    @Transactional(readOnly = true)
    public Gremio findByGremio(String gremio) {
        return this.gremioRepository.findByGremio(gremio)
                .orElseThrow(() -> new ResourceNotFoundException("Gremio", "Name", gremio));
    }

    @Transactional
    public void saveGremio(Gremio gremio) throws DataAccessException {
        gremioRepository.save(gremio);
    }
}