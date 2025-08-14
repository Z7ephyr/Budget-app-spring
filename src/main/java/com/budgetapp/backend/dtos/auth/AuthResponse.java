package com.budgetapp.backend.dtos.auth;

import com.budgetapp.backend.dtos.users.UserDTO;

/**
 * A DTO to be returned upon successful user login.
 * It contains the generated JWT token and the user's information.
 */
public class AuthResponse {

    private String token;
    private UserDTO user;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    // Getters and setters (or use Lombok @Data)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
