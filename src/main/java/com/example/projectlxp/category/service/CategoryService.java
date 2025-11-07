package com.example.projectlxp.category.service;

import com.example.projectlxp.category.dto.CategoryDTO;
import com.example.projectlxp.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAllCategoryOptimize().stream()
            .map(CategoryDTO::from)
            .toList();
    }
}
