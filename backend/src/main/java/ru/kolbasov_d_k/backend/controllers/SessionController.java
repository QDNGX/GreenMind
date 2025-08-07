package ru.kolbasov_d_k.backend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling session-related operations.
 * Provides endpoints for checking the authentication status of a user.
 */
@RestController
@RequestMapping("/api")
public class SessionController {

    /**
     * Checks if the user has an active authenticated session.
     * 
     * @param auth The authentication object representing the current user's authentication status
     * @return ResponseEntity with status 204 No Content if the user is authenticated and not anonymous,
     *         or status 200 OK if the user is not authenticated or is anonymous
     */
    @RequestMapping(value = "/session", method = {RequestMethod.HEAD, RequestMethod.GET})
    public ResponseEntity<Void> checkSession(Authentication auth) {
        boolean ok = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.ok().build();
    }
}
