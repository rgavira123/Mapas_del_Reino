package org.springframework.samples.petclinic.mapas_del_reino.game;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game extends NamedEntity {

    @Size(min = 1, max = 4, message = "El juego debe tener entre 1 y 4 jugadores")
    @ManyToMany
    @JoinTable(name = "game_players",
    joinColumns = @JoinColumn(name="game_id"),
    inverseJoinColumns = @JoinColumn(name="player_id")
    )
    public List<Player> players;

    private LocalDateTime finishDate;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lider_id")
    private Player lider;
}
