package com.example.projectlxp.section.controller.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class SectionCreateResponseDTO {

    @NotNull(message = "Section의 ID 값은 필수 입니다.")
    private Long sectionId;

    @NotBlank(message = "섹션의 제목은 빈값이 안됩니다.")
    private String title;

    @NotNull(message = "섹션의 순서는 필수 입니다.")
    private int orderNo;

    public SectionCreateResponseDTO(Long sectionId, String title, int orderNo) {
        this.sectionId = sectionId;
        this.title = title;
        this.orderNo = orderNo;
    }
}
