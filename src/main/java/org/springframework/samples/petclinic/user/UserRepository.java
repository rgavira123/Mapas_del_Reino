package org.springframework.samples.petclinic.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;

public interface UserRepository extends  CrudRepository<User, String>{

	@Query("SELECT p FROM Player p WHERE p.user.id = :userId")
	Optional<Player> findPlayerByUser(int userId);

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findById(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);

	@Query("DELETE FROM Player p WHERE p.user.id = :userId")
	@Modifying
	void deletePlayerRelation(int userId);
}
