package com.example.projectlxp.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}
