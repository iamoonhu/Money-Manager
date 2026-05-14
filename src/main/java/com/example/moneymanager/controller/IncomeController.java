package com.example.moneymanager.controller;


import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.dto.IncomeDTO;
import com.example.moneymanager.service.IncomeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    @Autowired
    private final IncomeService incomeSerive;

    @PostMapping
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO dto){
        IncomeDTO saved = incomeSerive.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpenseForMonth(){
        List<IncomeDTO> incomes= incomeSerive.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomes );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIncome(@PathVariable Long id){
        incomeSerive.deleteIncome(id);
        return ResponseEntity.status(HttpStatus.OK).body("Income deleted Succesfully");
    }

//    @GetMapping("/top5income")
//    public ResponseEntity<List<IncomeDTO>> getTop5Expense(){
//        List<IncomeDTO> list=incomeSerive.getLatest5IncomeForCurrentUser();
//        return ResponseEntity.status(HttpStatus.OK).body(list);
//    }
}
