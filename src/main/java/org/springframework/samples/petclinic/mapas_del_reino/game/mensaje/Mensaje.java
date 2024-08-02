package org.springframework.samples.petclinic.mapas_del_reino.game.mensaje;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {
    String jugador;
    boolean tableroLleno;
    
}
