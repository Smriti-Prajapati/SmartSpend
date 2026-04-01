package com.smartspend.repository;

import com.smartspend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate start, LocalDate end);

    List<Expense> findByUserIdAndCategoryIdOrderByDateDesc(Long userId, Long categoryId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%',:kw,'%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%',:kw,'%')))")
    List<Expense> searchByKeyword(@Param("userId") Long userId, @Param("kw") String keyword);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Double sumAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    Double sumAmountByUserIdAndDateBetween(@Param("userId") Long userId,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);

    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId " +
           "AND e.date BETWEEN :start AND :end GROUP BY e.category.name")
    List<Object[]> getCategoryBreakdown(@Param("userId") Long userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    @Query("SELECT MONTH(e.date), YEAR(e.date), SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end " +
           "GROUP BY YEAR(e.date), MONTH(e.date) ORDER BY YEAR(e.date), MONTH(e.date)")
    List<Object[]> getMonthlyTotals(@Param("userId") Long userId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    @Query("SELECT e.category.name, e.category.color, e.category.icon, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end " +
           "GROUP BY e.category.name, e.category.color, e.category.icon ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTopCategoriesWithDetails(@Param("userId") Long userId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);

    List<Expense> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}
