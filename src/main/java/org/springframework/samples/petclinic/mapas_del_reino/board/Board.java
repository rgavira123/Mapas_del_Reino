package org.springframework.samples.petclinic.mapas_del_reino.board;

import java.util.List;

import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.Casilla;
import org.springframework.samples.petclinic.mapas_del_reino.game.Game;
import org.springframework.samples.petclinic.mapas_del_reino.player.Player;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "board")
public class Board extends BaseEntity {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    @Min(0)
    private Integer points;

    @ElementCollection
    private List<Casilla> casillas;

    @Enumerated(EnumType.STRING)
    private Criterio criterioA1;

    @Enumerated(EnumType.STRING)
    private Criterio criterioA2;

    @Enumerated(EnumType.STRING)
    private Criterio criterioB1;

    @Enumerated(EnumType.STRING)
    private Criterio criterioB2;

}
