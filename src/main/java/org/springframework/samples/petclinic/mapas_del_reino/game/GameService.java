package org.springframework.samples.petclinic.mapas_del_reino.game;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.mapas_del_reino.player.PlayerRepository;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, 
            UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Game findGameById(int id) {
        return gameRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Game findGameByName(String name) {
        return gameRepository.findByName(name);
    }

    @Transactional
    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    @Transactional
    public void deleteGame(int id) {
        gameRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Integer countPlayersInGame(int gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);

        if (game != null) {
            return game.getPlayers().size();

        }
        return null;
    }

    @Transactional
    public void addPlayerToGame(Integer gameId, Integer playerId) {

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && game.players.size() < 4 && !game.players.contains(playerRepository.findById(playerId).orElse(null))){
            game.getPlayers().add(playerRepository.findById(playerId).orElse(null));
            gameRepository.save(game);
        }

    }

    @Transactional
    public void removePlayerFromGame(Integer gameId, Integer playerId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            game.getPlayers().remove(playerRepository.findById(playerId).orElse(null));
            gameRepository.save(game);
        }
    }
    @Transactional(readOnly = true) 
    public List<Game> findGamesByPlayerId(Integer playerId) {
        return gameRepository.findGamesByPlayerId(playerId);
    }

    @Transactional(readOnly = true)
    public List<Game> findCreatedGames() {
        return gameRepository.findCreatedGames();
    }

    @Transactional(readOnly = true)
    public List<Game> findStartedGames() {
        return gameRepository.findStartedGames();
    }

    @Transactional(readOnly = true)
    public List<Game> findFinishedGames() {
        return gameRepository.findFinishedGames();
    }

    @Transactional
    public void eliminarJugadorDePartida(int userId) {
        Player player = playerRepository.findByUserId(userId);
        List<Game> games = gameRepository.findAllGamesByPlayerId(player.getId());
        for(Game game: games){
            if(game.getStatus() != GameStatus.STARTED) {
                if(game.players.size() == 1) {
                    gameRepository.delete(game);
                } else {
                    game.players.remove(player);
                    gameRepository.save(game);
                }
            } else {
                gameRepository.delete(game);
            }
            
        }
    }
}
