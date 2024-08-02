package org.springframework.samples.petclinic.user;

import java.security.Principal;

import org.springframework.samples.petclinic.configuration.services.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Current User", description = "The current user management API")
public class CurrentUserController {
    @GetMapping("api/v1/currentuser/id")
    public String getCurrentUser(Principal principal) {
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
        return id;
    }
    
}
