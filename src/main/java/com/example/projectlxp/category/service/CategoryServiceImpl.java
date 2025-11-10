package com.example.projectlxp.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.projectlxp.category.dto.CategoryDTO;
import com.example.projectlxp.category.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAllCategoryOptimize().stream()
                .map(CategoryDTO::from)
                .toList();
    }
}
