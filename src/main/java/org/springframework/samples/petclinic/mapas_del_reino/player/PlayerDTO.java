package org.springframework.samples.petclinic.mapas_del_reino.player;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class PlayerDTO {

    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;

    @NotNull
    @Email
    private String email;


    public PlayerDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


    
}
