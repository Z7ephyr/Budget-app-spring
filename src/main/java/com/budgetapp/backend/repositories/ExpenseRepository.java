package com.budgetapp.backend.repositories;

import com.budgetapp.backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {


    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.user.id = :userId")
    List<Expense> findByUserId(@Param("userId") Long userId);


    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id AND e.user.id = :userId")
    Optional<Expense> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);


    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date DESC")
    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndCategoryIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate GROUP BY e.category.name")
    List<Object[]> sumAmountByCategoryByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.user.id = :userId AND e.category.name = :categoryName AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findByUserIdAndCategoryNameAndDateBetween(@Param("userId") Long userId, @Param("categoryName") String categoryName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT " +
            "e.id AS expense_id, " +
            "e.amount, " +
            "e.date, " +
            "e.description, " +
            "e.user_id, " +
            "e.created_at, " +
            "c.id AS category_id, " +
            "c.name AS category_name, " +
            "c.description AS category_description " +
            "FROM expenses e " +
            "JOIN categories c ON e.category_id = c.id " +
            "WHERE e.user_id = :userId " +
            "AND (:search IS NULL OR lower(cast(e.description as text)) LIKE lower(concat('%', :search, '%'))) " +
            "AND (:categoryName IS NULL OR c.name = :categoryName) " +
            "AND (:startDate IS NULL OR e.date BETWEEN :startDate AND :endDate) " +
            "AND (:minAmount IS NULL OR e.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR e.amount <= :maxAmount) " +
            "ORDER BY e.date DESC",
            nativeQuery = true)
    List<Object[]> findExpensesByFilters(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("categoryName") String categoryName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);
}