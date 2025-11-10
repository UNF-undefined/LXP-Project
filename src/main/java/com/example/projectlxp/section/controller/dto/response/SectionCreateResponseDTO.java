package com.example.projectlxp.section.controller.dto.response;

import lombok.Getter;

@Getter
public class SectionCreateResponseDTO {

    private Long sectionId;

    private String title;

    private int orderNo;

    public SectionCreateResponseDTO(Long sectionId, String title, int orderNo) {
        this.sectionId = sectionId;
        this.title = title;
        this.orderNo = orderNo;
    }
}
