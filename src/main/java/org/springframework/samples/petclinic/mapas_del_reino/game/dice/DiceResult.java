package org.springframework.samples.petclinic.mapas_del_reino.game.dice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiceResult {
    private Integer dado1;
    private Integer dado2;
    private Integer dado3;
    private Integer dado4;

}
