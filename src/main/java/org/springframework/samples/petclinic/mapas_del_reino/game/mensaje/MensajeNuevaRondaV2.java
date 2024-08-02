package org.springframework.samples.petclinic.mapas_del_reino.game.mensaje;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MensajeNuevaRondaV2 {
    private Integer nuevoIndice;
    private Integer turnoSiguiente;

    public MensajeNuevaRondaV2(Integer nuevoIndice, Integer turnoSiguiente) {
        this.nuevoIndice = nuevoIndice;
        this.turnoSiguiente = turnoSiguiente;
    }
}

