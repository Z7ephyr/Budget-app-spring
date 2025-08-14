package com.budgetapp.backend.mappers;

import com.budgetapp.backend.dtos.alerts.AlertDTO;

import com.budgetapp.backend.model.Alert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlertMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "read", source = "read")
    AlertDTO toDto(Alert alert);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "read", source = "read")
    Alert toEntity(AlertDTO dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "read", source = "read")
    void updateEntityFromDto(AlertDTO dto, @MappingTarget Alert alert);


}