package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserLoginDTO;
import com.budgetapp.backend.dtos.users.UserRegistrationDTO;
import com.budgetapp.backend.dtos.users.UserUpdateDTO;
import com.budgetapp.backend.mappers.UserMapper;
import com.budgetapp.backend.model.Role;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User user = userMapper.toEntity(registrationDTO);

        user.setRole(Role.user);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> loginUser(UserLoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return Optional.of(userMapper.toDto(user));
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getAuthenticatedUserDTO(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto);
    }

    /**
     * Updates an existing user's profile information.
     * This method is new and handles the profile update logic.
     * @param userId The ID of the user to update.
     * @param updateDTO The DTO containing the fields to update.
     * @return The updated UserDTO.
     * @throws EntityNotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserUpdateDTO updateDTO) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));


        userMapper.updateFromUpdateDto(updateDTO, existingUser);

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }
}
