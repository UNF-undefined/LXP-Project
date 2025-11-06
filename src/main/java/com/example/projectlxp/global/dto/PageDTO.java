package com.example.projectlxp.global.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageDTO {

    private final int currentPage;
    private int totalPages;
    private long totalElements;

    @Builder
    protected PageDTO(int currentPage, int totalPages, long totalElements) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public void updateTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void updateTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public static PageDTO of(Page page) {
        return PageDTO.builder()
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .build();
    }

    public static PageDTO of(Pageable pageable) {
        return PageDTO.builder().currentPage(pageable.getPageNumber()).build();
    }
}
