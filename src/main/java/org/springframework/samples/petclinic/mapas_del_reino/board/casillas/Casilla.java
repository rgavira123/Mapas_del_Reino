package org.springframework.samples.petclinic.mapas_del_reino.board.casillas;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class Casilla {
    
    private Integer q, r, s;

    private TipoCasilla tipoCasilla;


    public Casilla(int q, int r, int s, TipoCasilla tipoCasilla) {
        this.q = q;
        this.r = r;
        this.s = s;
        this.tipoCasilla = tipoCasilla;
    }


    @Override
    public String toString() {
        return "Casilla [q=" + q + ", r=" + r + ", s=" + s + ", tipoCasilla=" + tipoCasilla + "]";
    }

    



    
}
