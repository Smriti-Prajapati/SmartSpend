package com.smartspend.repository;

import com.smartspend.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdOrderByDateDesc(Long userId);

    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    Double sumAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId AND i.date BETWEEN :start AND :end")
    Double sumAmountByUserIdAndDateBetween(@Param("userId") Long userId,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);

    @Query("SELECT MONTH(i.date), YEAR(i.date), SUM(i.amount) FROM Income i " +
           "WHERE i.user.id = :userId AND i.date BETWEEN :start AND :end " +
           "GROUP BY YEAR(i.date), MONTH(i.date) ORDER BY YEAR(i.date), MONTH(i.date)")
    List<Object[]> getMonthlyTotals(@Param("userId") Long userId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    List<Income> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}
