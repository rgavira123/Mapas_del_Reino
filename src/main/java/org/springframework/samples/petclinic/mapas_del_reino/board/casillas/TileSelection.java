package org.springframework.samples.petclinic.mapas_del_reino.board.casillas;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileSelection {
    private String fichaJugador;
    private List<Integer> dados;  
}
