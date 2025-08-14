package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserLoginDTO;
import com.budgetapp.backend.dtos.users.UserRegistrationDTO;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(UserRegistrationDTO registrationDTO);
    Optional<UserDTO> loginUser(UserLoginDTO loginDTO);
    UserDTO getUserById(Long id);

    Optional<UserDTO> getAuthenticatedUserDTO(Long userId);
}
