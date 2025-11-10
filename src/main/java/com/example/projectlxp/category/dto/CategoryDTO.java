package com.example.projectlxp.category.dto;

import java.util.List;

import com.example.projectlxp.category.entity.Category;

public record CategoryDTO(Long id, String name, List<CategoryDTO> sub) {

    public static CategoryDTO from(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getChildren().stream().map(CategoryDTO::from).toList());
    }
}
