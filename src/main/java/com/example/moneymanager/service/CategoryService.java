package com.example.moneymanager.service;

import com.example.moneymanager.dto.CategoryDTO;
import com.example.moneymanager.entity.CategoryEntity;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositry.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class
CategoryService {

    private final ProfileService profileService;
    //private final CategoryService categoryService;
    private final CategoryRepo categoryRepo;

    // save new category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        ProfileEntity profile= profileService.getCuurentAccount();
        if (categoryRepo.existsByNameAndProfileId(categoryDTO.getName(),profile.getId())){
            throw  new RuntimeException("Category with this name is already created boss..");
        }

        CategoryEntity newCatory= toEntity(categoryDTO,profile);
        newCatory=categoryRepo.save(newCatory);

        return toDTO(newCatory);
    }

    // get category for current user
    public List<CategoryDTO> getCatgoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCuurentAccount();
        List<CategoryEntity> categories = categoryRepo.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    //get category for current user filtering with its type
    public List<CategoryDTO> getCategoiresByTypeForCurrentUser(String type){
        ProfileEntity profile= profileService.getCuurentAccount();
        java.util.List<CategoryEntity> entities= categoryRepo
                .findByTypeAndProfileId(type, profile.getId());
        return  entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updatecategory(Long categoryId, CategoryDTO dto){
        ProfileEntity profile = profileService.getCuurentAccount();
        CategoryEntity existingCategory=categoryRepo.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(()-> new RuntimeException("Category Not found or not accesible"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory.setType(dto.getType());
        existingCategory = categoryRepo.save(existingCategory);

        return toDTO(existingCategory);
    }


    //helper methods
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile){
        return  CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity){
        return  CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile()!=null? categoryEntity.getProfile().getId(): null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }


}
