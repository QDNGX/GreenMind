package ru.kolbasov_d_k.backend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SessionController {

    @RequestMapping(value = "/session", method = {RequestMethod.HEAD, RequestMethod.GET})
    public ResponseEntity<Void> checkSession(Authentication auth) {
        boolean ok = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.ok().build();
    }
}
