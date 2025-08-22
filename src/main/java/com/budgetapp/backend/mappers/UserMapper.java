package com.budgetapp.backend.mappers;

import com.budgetapp.backend.dtos.users.UserDTO;
import com.budgetapp.backend.dtos.users.UserRegistrationDTO;
import com.budgetapp.backend.dtos.users.UserUpdateDTO;
import com.budgetapp.backend.model.Role; // Import the Role enum
import com.budgetapp.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {


    @Mapping(target = "role", source = "role")
    UserDTO toDto(User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", source = "password")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "totalBalance", ignore = true)
    User toEntity(UserRegistrationDTO registrationDto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    void updateEntityFromDto(UserDTO dto, @MappingTarget User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    void updateFromUpdateDto(UserUpdateDTO updateDto, @MappingTarget User user);

    /**
     * This is the method that will handle the conversion from Role enum to String.
     * MapStruct will automatically use this method when it encounters a mapping from Role to String.
     */
    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }
}