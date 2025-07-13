package ru.kolbasov_d_k.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.UserProductService;
import ru.kolbasov_d_k.backend.services.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserProfileController {

    private final UserService userService;
    private final UserProductService userProductService;

    @Autowired
    public UserProfileController(UserService userService, UserProductService userProductService) {
        this.userService = userService;
        this.userProductService = userProductService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String,Object>> getProfile(Principal principal) {
        try{
            User user = userService.getCurrentUser(principal);
            Map<String,Object> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("birthDate", user.getBirthDate());
            map.put("orders", userProductService.findOrders(user));

            return ResponseEntity.ok(map);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(401).build();
        }
    }
}
