package com.example.moneymanager.controller;


import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.dto.FilterDTO;
import com.example.moneymanager.dto.IncomeDTO;
import com.example.moneymanager.service.ExpenseSerive;
import com.example.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseSerive expenseSerive;

    @PostMapping()
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDTO filterDTO){
        LocalDate startDate= filterDTO.getStartDate()!=null? filterDTO.getStartDate():  LocalDate.of(2000, 1, 1);
        LocalDate endDate= filterDTO.getEndDate()!=null ? filterDTO.getEndDate(): LocalDate.now();
        String keyword=filterDTO.getKeyword()!= null ? filterDTO.getKeyword(): "";
        String sortField= filterDTO.getSortField()!= null? filterDTO.getSortField(): "date";
        Sort.Direction direction= "desc".equalsIgnoreCase(filterDTO.getSortOrder())? Sort.Direction.DESC: Sort.Direction.ASC;
        Sort sort = org.springframework.data.domain.Sort.by(direction,sortField);

        if("income".equalsIgnoreCase(filterDTO.getType())){
            List<IncomeDTO> incomes =incomeService.filterIncomes(startDate,endDate,keyword,sort);
            return  ResponseEntity.ok(incomes);

        }else if ("expense".equalsIgnoreCase(filterDTO.getType())){
            List<ExpenseDTO> expenses = expenseSerive.filterExpenses(startDate,endDate,keyword,sort);
            return  ResponseEntity.ok(expenses);
        }else{
            return  ResponseEntity.badRequest().body("Invalid type.");
        }
    }
}
