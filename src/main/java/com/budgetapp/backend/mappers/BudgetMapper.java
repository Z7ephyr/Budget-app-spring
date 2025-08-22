package com.budgetapp.backend.mappers;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.dtos.budgets.CreateBudgetDTO;
import com.budgetapp.backend.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetMapper {


    @Mapping(target = "categoryId", source = "category.id")
    BudgetDTO toDto(Budget entity);


    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Budget toEntity(CreateBudgetDTO dto);


    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(CreateBudgetDTO dto, @MappingTarget Budget budget);
}