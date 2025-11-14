package com.example.projectlxp.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.category.dto.CategoryDTO;
import com.example.projectlxp.category.service.CategoryService;
import com.example.projectlxp.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public BaseResponse<List<CategoryDTO>> getAllCategories() {
        return BaseResponse.success("카테고리 목록 조회 성공!", categoryService.getCategories());
    }
}
