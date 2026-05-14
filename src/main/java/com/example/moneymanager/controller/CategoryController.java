package com.example.moneymanager.controller;

import com.example.moneymanager.dto.CategoryDTO;
import com.example.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO saveCategory= categoryService.saveCategory(categoryDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(saveCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(){
        List<CategoryDTO> categories= categoryService.getCatgoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCatogeriesByType(@PathVariable String type){
        List<CategoryDTO> categorie= categoryService.getCategoiresByTypeForCurrentUser(type);

        return ResponseEntity.ok(categorie);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,
                                                      @RequestBody CategoryDTO categoryDTO){
        CategoryDTO updatecategory= categoryService.updatecategory(categoryId, categoryDTO)  ;
        return  ResponseEntity.ok(updatecategory);
    }
}
