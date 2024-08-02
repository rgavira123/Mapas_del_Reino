package org.springframework.samples.petclinic.mapas_del_reino.board.casillas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class CasillaRequest {
    private List<Integer> coordenada;
    private String tipoCasilla;

    public CasillaRequest(List<Integer> coordenada, String tipoCasilla) {
        this.coordenada = coordenada;
        this.tipoCasilla = tipoCasilla;
    }

  
    
}
