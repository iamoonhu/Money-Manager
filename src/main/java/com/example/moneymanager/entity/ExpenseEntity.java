package com.example.moneymanager.entity;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_Expense")
public class ExpenseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String icon;
        private LocalDate date;
        private BigDecimal amount;

        @CreationTimestamp
        @Column(updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;

        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private CategoryEntity category;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "profile_id", nullable = false)
        private ProfileEntity profile;

        @PrePersist
        public void prePersist(){
            if(this.date==null){
                this.date=LocalDate.now();
                // if the user not give the date we will provide the current date
            }
        }
    }



