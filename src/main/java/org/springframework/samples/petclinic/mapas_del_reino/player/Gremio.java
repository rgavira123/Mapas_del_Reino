package org.springframework.samples.petclinic.mapas_del_reino.player;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gremios")  
public class Gremio extends BaseEntity {

    @Column(length = 30)
	String gremio;
    
}
