package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;


/**
 * Service class for handling authentication-related business logic,
 * such as generating new JSON Web Tokens (JWTs).
 */
@Service
public class AuthService {

    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Generates a new JWT for an authenticated user.
     *
     * @param userDTO The UserDTO containing the necessary user details.
     * @return A newly generated JWT string.
     */
    public String generateToken(UserDTO userDTO) {

        UserDetails userDetails = new UserDetails() {
            @Override
            public String getUsername() {
                return userDTO.getEmail();
            }


            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };


        return jwtUtil.generateToken(userDetails);
    }
}
