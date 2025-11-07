package com.example.projectlxp.category.repository;

import com.example.projectlxp.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findAllCategoryOptimize();

}
