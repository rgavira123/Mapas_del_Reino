package org.springframework.samples.petclinic.mapas_del_reino.player;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class GremioRepositoryTests {

    @Autowired
    private GremioRepository gremioRepository;

    @Test
    public void testFindByGremio() {
        Gremio gremio = gremioRepository.findByGremio("CABALLEROS").orElseThrow(() -> new IllegalArgumentException("Gremio not found"));
        assertEquals("CABALLEROS", gremio.getGremio());
    }

    @Test
    public void testFindByGremioNotFound() {
        Gremio gremio = gremioRepository.findByGremio("gremio inexistente").orElse(null);
        assertEquals(null, gremio);
    }
    
}
