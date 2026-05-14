package com.example.moneymanager.service;

import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.dto.IncomeDTO;
import com.example.moneymanager.dto.RecentTransactiondto;
import com.example.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardSerive {

    private final IncomeService incomeService;
    private final ExpenseSerive expenseSerive;
    private final ProfileService profileService;

    public Map<String, Object> getDashBoarddata(){
        ProfileEntity profile = profileService.getCuurentAccount();
        Map<String , Object> returnValue= new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes= incomeService.getLatest5IncomeForCurrentUser();
        List<ExpenseDTO> latestExpense = expenseSerive.getLatest5ExpenseForCurrentUser();
        List<RecentTransactiondto> recenttransactions=concat(latestIncomes.stream().map(income ->
                RecentTransactiondto.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreateAt())
                        .updatedAt(income.getUpdateAt())
                        .type("Income")
                        .build()),
                latestExpense.stream().map( expense->
                        RecentTransactiondto.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreateAt())
                                .updatedAt(expense.getUpdateAt())
                                .type("Expense")
                                .build()))
                .sorted((a, b) -> {
                    // Null-safe date comparison
                    if (a.getDate() == null && b.getDate() == null) return 0;
                    if (a.getDate() == null) return 1;   // nulls go to the end
                    if (b.getDate() == null) return -1;

                    int cmp = b.getDate().compareTo(a.getDate());

                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());

               returnValue.put("Total Balance",
                       incomeService.getTotalIncomeForCurrentUser()
                               .subtract(expenseSerive.getTotalExpenseForCurrentUser()));

               returnValue.put("Total Income", incomeService.getTotalIncomeForCurrentUser());
               returnValue.put("Total Expense",expenseSerive.getTotalExpenseForCurrentUser());
               returnValue.put("Recent 5 expenses",latestExpense);
               returnValue.put("Recent 5 Incomes ", latestIncomes);
               returnValue.put("Recent Transactions",recenttransactions);

               return returnValue;
    }


}
