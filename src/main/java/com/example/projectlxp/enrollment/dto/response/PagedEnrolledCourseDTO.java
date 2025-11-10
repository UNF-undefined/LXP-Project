package com.example.projectlxp.enrollment.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedEnrolledCourseDTO {
    private List<EnrolledCourseDTO> enrolledCourseDTOList;
    private Integer totalPages;
    private Long totalElements;
    private Boolean isFirst;
    private Boolean isLast;

    public static PagedEnrolledCourseDTO from(Page<EnrolledCourseDTO> page) {
        return PagedEnrolledCourseDTO.builder()
                .enrolledCourseDTOList(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
