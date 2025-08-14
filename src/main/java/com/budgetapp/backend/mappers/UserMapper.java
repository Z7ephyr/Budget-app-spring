package com.budgetapp.backend.mappers;

import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserRegistrationDTO;
import com.budgetapp.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // This method is for mapping a User entity to a DTO.
    @Mapping(target = "role", source = "role")
    UserDTO toDto(User user);


    // This method is for mapping a UserRegistrationDTO to a User entity.
    // We explicitly tell MapStruct to ignore the 'role' field because the
    // registration DTO does not have it. The role will be set to 'user'
    // in the service layer after this mapping is done.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRegistrationDTO registrationDto);


    // This method is for updating a user entity from a DTO.
    // It's good practice to ignore sensitive or non-updatable fields.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateEntityFromDto(UserDTO dto, @MappingTarget User user);
}
