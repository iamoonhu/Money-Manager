package com.example.moneymanager.service;


import com.example.moneymanager.dto.ExpenseDTO;
import com.example.moneymanager.dto.IncomeDTO;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.entity.IncomeEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositry.CategoryRepo;
import com.example.moneymanager.repositry.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepo categoryRepo;
    private final IncomeRepo incomeRepo;
    private final ProfileService profileService;
    // adding the new Income to teh data base
    public IncomeDTO addIncome(IncomeDTO dto){
        if (dto.getDate() == null) {
            dto.setDate(LocalDate.now());
        }
        ProfileEntity profile= profileService.getCuurentAccount();
        CategoryEntity category= categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category Not Found"));

        IncomeEntity newIncome= toEntity(dto,profile,category);
        newIncome= incomeRepo.save(newIncome);
        return  toDTO(newIncome);
    }

    // retrieving the income for the specific month for the current user
    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate enddate= now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list=incomeRepo
                .findByProfileIdAndDateBetween(profile.getId(),startDate,enddate);

        return list.stream().map(this::toDTO).toList();
    }

    // delete income by id for the current user
    public void deleteIncome(Long incomeId){
        ProfileEntity profile= profileService.getCuurentAccount();
        IncomeEntity enityty=incomeRepo.findById(incomeId)
                .orElseThrow(()-> new RuntimeException("Not such Income found..."));
        if(!enityty.getProfile().getId().equals(profile.getId())){
            throw  new RuntimeException("Unautherized to delete this income");
        }
        incomeRepo.delete(enityty);
    }

    //Get latest 5 expeses for the current user
    public List<IncomeDTO> getLatest5IncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        List<IncomeEntity >values= incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return values.stream().map(this::toDTO).toList();
    }

    //get total Income for the Current User
    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        BigDecimal total= incomeRepo.findTotalExpenseByProfileId(profile.getId());
        return total!=null? total: BigDecimal.ZERO;
    }

    // Filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate StartDate, LocalDate End, String Keyword, Sort sort){
        ProfileEntity profile= profileService.getCuurentAccount();
        List<IncomeEntity> list=incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase
                (profile.getId(), StartDate, End,Keyword, sort);

        return list.stream().map(this::toDTO).toList();
    }

    // helper method
    public IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile , CategoryEntity category){
        return  IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
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
