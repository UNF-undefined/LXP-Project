package com.example.projectlxp.category.service;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void 카테고리를_조회한다() {
        //given
        Category test1 = Category.builder().id(1L).name("test1").build();
        Category test2 = Category.builder().id(2L).name("test2").build();
        Category test3 = Category.builder().id(3L).name("test3").build();
        test1.addChildCategory(test2);
        List<Category> mockCategories = List.of(test1, test3);

        //when
        when(categoryRepository.findAllCategoryOptimize()).thenReturn(mockCategories);

        //then
        assertAll(
            () -> assertThat(categoryService.getCategories().size()).isEqualTo(2),
            () -> assertThat(categoryService.getCategories().get(0).name()).isEqualTo("test1"),
            () -> assertThat(categoryService.getCategories().get(0).sub().get(0).name()).isEqualTo("test2"),
            () -> assertThat(categoryService.getCategories().get(1).name()).isEqualTo("test3")
        );
    }

}
