package org.springframework.samples.petclinic.mapas_del_reino.board;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class BoardDTO2 {

    private Integer playerId;
    private Integer gameId;
    private String criterioA1;
    private String criterioA2;
    private String criterioB1;
    private String criterioB2;
    
    
    public BoardDTO2(Integer playerId, Integer gameId, String criterioA1, String criterioA2, String criterioB1, String criterioB2) {
        this.playerId = playerId; 
        this.gameId = gameId;
        this.criterioA1 = criterioA1;
        this.criterioA2 = criterioA2;
        this.criterioB1 = criterioB1;
        this.criterioB2 = criterioB2;
    }
    

}
