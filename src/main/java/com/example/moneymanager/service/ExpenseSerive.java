package com.example.moneymanager.service;

import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositry.CategoryRepo;
import com.example.moneymanager.repositry.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseSerive {

    private final CategoryRepo categoryRepo;
    private final ExpenseRepo expenseRepo;
    private final ProfileService profileService;

    // adding the new expense to teh data base
    public ExpenseDTO addexpense(ExpenseDTO dto){
        if (dto.getDate() == null) {
            dto.setDate(LocalDate.now());
        }
        ProfileEntity profile= profileService.getCuurentAccount();
         CategoryEntity category= categoryRepo.findById(dto.getCategoryId())
                 .orElseThrow(()-> new RuntimeException("Category Not Found"));

         ExpenseEntity newExpense= toEntity(dto,profile,category);
         newExpense= expenseRepo.save(newExpense);
         return  toDTO(newExpense);
    }

    // retrieving the expense for the specific month for the current user
    public List<ExpenseDTO> getCurrentMonthExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate enddate= now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list=expenseRepo
                .findByProfileIdAndDateBetween(profile.getId(),startDate,enddate);

        return list.stream().map(this::toDTO).toList();
    }


   // delete expense by id for the current user
    public void deleteExpense(Long expenseId){
        ProfileEntity profile= profileService.getCuurentAccount();
        ExpenseEntity enityty=expenseRepo.findById(expenseId)
                .orElseThrow(()-> new RuntimeException("Not such expense found..."));
        if(!enityty.getProfile().getId().equals(profile.getId())){
            throw  new RuntimeException("Unautherized to delete this expense");
        }
        expenseRepo.delete(enityty);
    }

    //Get latest 5 expeses for the current user
    public List<ExpenseDTO> getLatest5ExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        List<ExpenseEntity >values= expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());

        return values.stream().map(this::toDTO).toList();
    }

    //get total expense
    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        BigDecimal total= expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return total!=null? total: BigDecimal.ZERO;
    }

    // Filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate StartDate, LocalDate End, String Keyword, Sort sort){
        ProfileEntity profile= profileService.getCuurentAccount();
        List<ExpenseEntity> list=expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase
                (profile.getId(), StartDate, End,Keyword, sort);

        return list.stream().map(this::toDTO).toList();
    }

    //notifications

    public List<ExpenseDTO> getExpensesforuserondate(Long ProfileId, LocalDate date){
        List<ExpenseEntity> list= expenseRepo.findByProfileIdAndDate(ProfileId,date);
        return  list.stream().map(this::toDTO).toList();

    }



    // helper method
    public ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile , CategoryEntity category){
        return  ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public  ExpenseDTO toDTO( ExpenseEntity entity){
         return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() !=null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory()!=null? entity.getCategory().getName() : null)
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createAt(entity.getCreatedAt())
                .updateAt(entity.getUpdatedAt())
                .build();
    }
}
