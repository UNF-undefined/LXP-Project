package com.example.projectlxp.category.controller;

import com.example.projectlxp.category.dto.CategoryDTO;
import com.example.projectlxp.category.service.CategoryService;
import com.example.projectlxp.global.dto.BaseResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public BaseResponse<List<CategoryDTO>> getAllCategories() {
        return BaseResponse.success("카테고리 목록 조회 성공!", categoryService.getCategories());
    }
}
