package com.budgetapp.backend.mappers;

import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import com.budgetapp.backend.model.Expense;
import com.budgetapp.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "userId", source = "user.id")
    ExpenseDTO toDto(Expense entity);

    @Mapping(target = "merchant", source = "description")
    // The following mappings are explicit but redundant as they map by name by default
    @Mapping(target = "id", source = "id")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "date", source = "date")
    TransactionDTO toTransactionDto(Expense expense);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "description", source = "notes") // Maps notes from DTO to description in entity
    @Mapping(target = "date", expression = "java(dto.getDate() != null ? dto.getDate() : java.time.LocalDate.now())") // Provides default date
        // Note: 'scanReceipt' from CreateExpenseDTO is not mapped to Expense entity, it will be ignored.
    Expense toEntity(CreateExpenseDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    // The following mappings are explicit but redundant as they map by name by default
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "date", source = "date")
    void updateEntityFromDto(ExpenseDTO dto, @MappingTarget Expense expense);
}
