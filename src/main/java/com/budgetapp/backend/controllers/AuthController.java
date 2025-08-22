package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.auth.AuthResponse;
import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserLoginDTO;
import com.budgetapp.backend.dtos.users.UserRegistrationDTO;
import com.budgetapp.backend.dtos.users.UserUpdateDTO;
import com.budgetapp.backend.config.UserDetailsImpl;
import com.budgetapp.backend.services.UserService;
import com.budgetapp.backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO registeredUser = userService.registerUser(registrationDTO);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registrationDTO.getEmail(), registrationDTO.getPassword())
        );
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtUtil.generateToken(userDetails);

        AuthResponse authResponse = new AuthResponse(token, registeredUser);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String token = jwtUtil.generateToken(userDetails);

            Optional<UserDTO> userDTO = userService.getAuthenticatedUserDTO(userDetails.getId());
            if (userDTO.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Authenticated user not found.");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok(new AuthResponse(token, userDTO.get()));

        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid email or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * Endpoint to get the profile of the authenticated user.
     * The @AuthenticationPrincipal directly injects the UserDetailsImpl object from the security context.
     * @param principal The authenticated user's details.
     * @return A ResponseEntity containing the user's profile data or a not-found status.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getAuthenticatedUserProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        Long userId = principal.getId();
        Optional<UserDTO> userDTO = userService.getAuthenticatedUserDTO(userId);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to update the profile of the authenticated user.
     * @param principal The authenticated user's details.
     * @param userUpdateDTO The DTO containing the updated profile data.
     * @return A ResponseEntity with the updated user profile.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@AuthenticationPrincipal UserDetailsImpl principal,
                                                 @RequestBody UserUpdateDTO userUpdateDTO) {
        Long userId = principal.getId();
        UserDTO updatedUser = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }
}
