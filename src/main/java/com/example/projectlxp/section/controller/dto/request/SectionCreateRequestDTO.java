package com.example.projectlxp.section.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SectionCreateRequestDTO {

    @NotNull(message = "course ID는 필수 입력 값 입니다.")
    public Long courseId;

    @NotBlank(message = "섹션의 제목을 입력해주세요.")
    public String title;

    @NotNull(message = "order No는 필수 입력 값 입니다.")
    public int orderNo;

    public SectionCreateRequestDTO(Long courseId, String title, int orderNo) {
        this.courseId = courseId;
        this.title = title;
        this.orderNo = orderNo;
    }
}
