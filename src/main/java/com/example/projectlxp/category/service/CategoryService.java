package com.example.projectlxp.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.projectlxp.category.dto.CategoryDTO;

@Service
public interface CategoryService {

    List<CategoryDTO> getCategories();
}
