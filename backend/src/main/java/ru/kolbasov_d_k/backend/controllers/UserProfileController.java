package ru.kolbasov_d_k.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.UserProductService;
import ru.kolbasov_d_k.backend.services.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for handling user profile-related operations.
 * Provides endpoints for retrieving and updating user profile information.
 */
@RestController
@RequestMapping("/api")
public class UserProfileController {

    private final UserService userService;
    private final UserProductService userProductService;

    /**
     * Constructs a new UserProfileController with the required services.
     *
     * @param userService Service for user-related operations
     * @param userProductService Service for user-product relationship operations
     */
    @Autowired
    public UserProfileController(UserService userService, UserProductService userProductService) {
        this.userService = userService;
        this.userProductService = userProductService;
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     * 
     * @param principal The currently authenticated user
     * @return ResponseEntity containing a map with user profile information (username, email, birthDate, orders)
     *         or a 401 Unauthorized status if the user is not authenticated
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String,Object>> getProfile(Principal principal) {
        try{
            User user = userService.getCurrentUser(principal);
            Map<String,Object> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("birthDate", user.getBirthDate());
            map.put("role", user.getRole());
            map.put("orders", userProductService.findOrders(user));

            return ResponseEntity.ok(map);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * Supports partial updates for username, email, and birthDate fields.
     * Returns JSON response for compatibility with frontend APIHelper.
     * 
     * @param principal The currently authenticated user
     * @param map A map containing the fields to update. Supported keys: "username", "email", "birthDate"
     * @return ResponseEntity with status 200 OK and success message if the update was successful,
     *         401 Unauthorized if user is not authenticated, or 400 Bad Request for other errors
     */
    @PatchMapping("/profile")
    public ResponseEntity<Map<String,String>> updateProfile(Principal principal, @RequestBody Map<String,Object> map) {
        try{
            User user = userService.getCurrentUser(principal);
            if (map.containsKey("username")) {
                userService.updateUserName(user, (String) map.get("username"));
            }
            if (map.containsKey("email")) {
                userService.updateUserEmail(user, (String) map.get("email"));
            }
            if (map.containsKey("birthDate")) {
                String dateStr = (String) map.get("birthDate");
                LocalDate birthDate = LocalDate.parse(dateStr);
                userService.updateUserBirthDate(user, birthDate);
            }
            
            // Возвращение JSON для работы с APIHelper
            Map<String,String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e){
            // Пользователь не найден или недостаточно прав
            return ResponseEntity.status(401).build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
