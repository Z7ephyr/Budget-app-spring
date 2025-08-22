package com.budgetapp.backend.mappers;

import com.budgetapp.backend.dtos.expenses.CategoryDTO;
import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import com.budgetapp.backend.model.Category;
import com.budgetapp.backend.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ExpenseDTO toDto(Expense entity);


    @Mapping(target = "merchant", source = "description")
    @Mapping(target = "category", source = "category", qualifiedByName = "mapCategoryToDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "date", source = "date")
    TransactionDTO toTransactionDto(Expense expense);


    @Named("mapCategoryToDto")
    default CategoryDTO mapCategoryToDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    // 💡 FIX: This is the critical change. Map `description` from the DTO to the entity.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)

    @Mapping(target = "description", source = "description")
    @Mapping(target = "date", expression = "java(dto.getDate() != null ? dto.getDate() : java.time.LocalDate.now())")
    @Mapping(target = "category.id", source = "categoryId")
    Expense toEntity(CreateExpenseDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "category.id", source = "categoryId")
    void updateEntityFromDto(ExpenseDTO dto, @MappingTarget Expense expense);
}