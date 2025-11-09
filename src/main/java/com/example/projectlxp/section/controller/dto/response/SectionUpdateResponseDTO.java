package com.example.projectlxp.section.controller.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class SectionUpdateResponseDTO {

    @NotNull(message = "Section ID 값은 필수 입니다.")
    public Long sectionId;

    @NotBlank(message = "섹션의 제목을 입력해주세요.")
    public String title;

    @NotNull(message = "Order No 값은 필수 입니다.")
    public int orderNo;

    public SectionUpdateResponseDTO(Long sectionId, String title, int orderNo) {
        this.sectionId = sectionId;
        this.title = title;
        this.orderNo = orderNo;
    }
}
