package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.auth.AuthResponse;
import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserLoginDTO;
import com.budgetapp.backend.dtos.users.UserRegistrationDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // Error handling (IllegalArgumentException, DataIntegrityViolationException, Exception)
        // will now be handled by GlobalExceptionHandler.
        // We no longer need try-catch blocks here for errors that GlobalExceptionHandler handles.
        UserDTO registeredUser = userService.registerUser(registrationDTO);

        // After successful registration, authenticate the user to get their principal
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registrationDTO.getEmail(), registrationDTO.getPassword())
        );
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generate a JWT token for the authenticated user
        String token = jwtUtil.generateToken(userDetails);

        // Return a custom AuthResponse containing the new JWT and user data
        AuthResponse authResponse = new AuthResponse(token, registeredUser);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            // Step 1: Authenticate the user's credentials using the AuthenticationManager.
            // This will throw an AuthenticationException if the email/password is incorrect.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            // Step 2: Get the authenticated user's details.
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Step 3: Generate a JWT token for the authenticated user.
            String token = jwtUtil.generateToken(userDetails);

            // Step 4: Get the user's DTO to include in the response.
            Optional<UserDTO> userDTO = userService.getAuthenticatedUserDTO(userDetails.getId());
            if (userDTO.isEmpty()) {
                // This case should not happen if authentication was successful, but it's good practice.
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Authenticated user not found.");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Step 5: Return a custom AuthResponse containing the JWT and user data.
            return ResponseEntity.ok(new AuthResponse(token, userDTO.get()));

        } catch (AuthenticationException e) {
            // Handle authentication failure specifically for login.
            // GlobalExceptionHandler will not handle AuthenticationException by default to return 401.
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid email or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Other exception handling will be taken care of by GlobalExceptionHandler.
            // However, if you want a specific message for login, you can keep this.
            // Otherwise, GlobalExceptionHandler.handleGenericException will handle it.
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
