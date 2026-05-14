package com.example.moneymanager.controller;

import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.service.ExpenseSerive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private final ExpenseSerive expenseSerive;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto){
        ExpenseDTO saved= expenseSerive.addexpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenseForMonth(){
       List<ExpenseDTO> expenses= expenseSerive.getCurrentMonthExpenseForCurrentUser();
       return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id){
        expenseSerive.deleteExpense(id);
        return ResponseEntity.status(HttpStatus.OK).body("Expense deleted Succesfully");
    }

//    @GetMapping("/top5expense")
//    public ResponseEntity<List<ExpenseDTO>> getTop5Expense(){
//        List<ExpenseDTO> list=expenseSerive.getLatest5ExpenseForCurrentUser();
//        return ResponseEntity.status(HttpStatus.OK).body(list);
//    }
}
