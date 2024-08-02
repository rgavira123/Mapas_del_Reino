package org.springframework.samples.petclinic.mapas_del_reino.player;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// Add this line

@DataJpaTest
public class PlayerRepositoryTests {

    @Autowired
    private PlayerRepository playerRepository;

    public final static int USER_ID = 20;

    @Test
    public void testFindByUserId() {
        Player player = playerRepository.findByUserId(USER_ID);
        assertEquals(Integer.valueOf(20), Integer.valueOf(player.getUser().getId()));
    
    }

    @Test
    public void testFindByUserIdNotFound() {
        Player player = playerRepository.findByUserId(214);
        assertEquals(null, player);
    }
       
    

}
