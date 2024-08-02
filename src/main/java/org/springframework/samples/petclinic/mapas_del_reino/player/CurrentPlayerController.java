package org.springframework.samples.petclinic.mapas_del_reino.player;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.configuration.services.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/currentplayer")
@Tag(name = "Current player", description = "The current player management API")
public class CurrentPlayerController {
    @GetMapping()
    public ResponseEntity<MessageResponse> getCurrentPlayer(Principal principal) {
        String id = "";
        UsernamePasswordAuthenticationToken principalToken;
        UserDetailsImpl currentUser;
        
        if(principal != null) {
            principalToken = (UsernamePasswordAuthenticationToken) principal;
            currentUser = (UserDetailsImpl) principalToken.getPrincipal();
            id = currentUser.getId().toString();
        } else {
            id = "-1";
        }
        return new ResponseEntity<MessageResponse>(new MessageResponse(id), HttpStatus.OK);
    }
}
