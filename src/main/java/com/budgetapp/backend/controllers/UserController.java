package com.budgetapp.backend.controllers;

import com.budgetapp.backend.config.UserDetailsImpl;
import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserUpdateDTO;
import com.budgetapp.backend.dtos.users.UserUpdateResponseDTO;
import com.budgetapp.backend.services.UserService;
import com.budgetapp.backend.services.AuthService; // Import the authentication service
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService; // Inject the authentication service

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     * Accessible only by authenticated users.
     * @param userDetails The details of the authenticated user, provided by Spring Security.
     * @return ResponseEntity containing the user's profile data.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getAuthenticatedUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Use the correct method name from your UserService
        Optional<UserDTO> userDTO = userService.getAuthenticatedUserDTO(userDetails.getId());


        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * Generates a new JWT after a successful update.
     * @param userDetails The details of the authenticated user.
     * @param updateDTO The DTO containing the new profile data.
     * @return ResponseEntity with the updated user data and a new token.
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserUpdateResponseDTO> updateAuthenticatedUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserDTO updatedUser = userService.updateUser(userDetails.getId(), updateDTO);

        String newToken = authService.generateToken(updatedUser);

        UserUpdateResponseDTO responseDTO = new UserUpdateResponseDTO(updatedUser, newToken);

        return ResponseEntity.ok(responseDTO);
    }
}
