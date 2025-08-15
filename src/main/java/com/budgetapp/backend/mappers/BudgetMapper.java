package com.budgetapp.backend.mappers;
import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    // Removed the mapping for userId
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "aiRecommendation", ignore = true)
    @Mapping(target = "lastMonthComparison", ignore = true)
    BudgetDTO toDto(Budget entity);

    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Budget toEntity(BudgetDTO dto);

    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(BudgetDTO dto, @MappingTarget Budget budget);
}
