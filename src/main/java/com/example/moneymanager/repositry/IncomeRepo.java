package com.example.moneymanager.repositry;

import com.example.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepo extends JpaRepository<IncomeEntity, Long> {

    // select * from tbl_expenses where profile_id = ?1 order by date desc
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_Expenses where profile_Id= ?1 order by date desc limit 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);


    @Query("SELECT SUM(e.amount) FROM IncomeEntity  e where e.profile.id= :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);


    // select * from tbl_Expenses where profile_id= ?1 and date between ?2 and ?3 and name like %?4%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate EndDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_Expense where profile_id= ?1 and date between ?2 and ?3
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate sD , LocalDate eD);
}
