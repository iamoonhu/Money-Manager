package com.example.moneymanager.repositry;

import com.example.moneymanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<CategoryEntity,Long> {

       // select * from tbl_categories where profile_id
      List<CategoryEntity> findByProfileId(Long profileId);

      Optional<CategoryEntity> findByIdAndProfileId(Long id, Long ProfileId);

      List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

      Boolean existsByNameAndProfileId(String name, Long profileId);



}
