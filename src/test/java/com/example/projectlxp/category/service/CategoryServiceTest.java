package com.example.projectlxp.category.service;

import com.example.projectlxp.category.controller.CategoryController;
import com.example.projectlxp.category.dto.CategoryDTO;
import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 카테고리를_조회한다() throws Exception {
        //given
        Category test1 = Category.builder().id(1L).name("test1").build();
        List<Category> mockCategories = List.of(
            test1,
            Category.builder().id(2L).name("test2").parent(test1).build(),
            Category.builder().id(3L).name("test3").build()
        );
        List<CategoryDTO> mockResponse = List.of(

        );

        //when
        when(categoryRepository.findAll()).thenReturn(mockCategories);
        when(categoryService.getCategories()).thenReturn(mockResponse); // DTO로 변환 가정

        //then
        mockMvc.perform(get("/api/categories")) // 🚨 실패 지점: 이 URL에 매핑된 Controller가 없으면 404 발생 (Red)
            .andExpect(status().isOk()) // HTTP 200 검증
            .andExpect(jsonPath("$[0].name").value("test1"));
    }


}
