package com.budgetapp.backend.config;

import com.budgetapp.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class wraps our application's User entity and provides the necessary
 * information to Spring Security for authentication and authorization.
 * It also stores the user's ID, which will be crucial for our controllers.
 */
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Factory method to build a UserDetailsImpl from our application's User entity.
     * This method is called from our custom UserDetailsService.
     *
     * @param user The User entity from the database.
     * @return A new UserDetailsImpl instance.
     */
    public static UserDetailsImpl build(User user) {
        // We're assuming a single role for now. We can expand this later if needed.
        List<GrantedAuthority> authorities = Collections.singletonList(
                // Use .name() to get the String representation of the enum.
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())
        );

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    // --- UserDetails Interface Methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // Since our tokens are short-lived, we don't need to check these.
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

    // --- Custom Getters for our application ---

    public Long getId() {
        return id;
    }
}
