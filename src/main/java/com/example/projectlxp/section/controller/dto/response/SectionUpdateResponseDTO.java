package com.example.projectlxp.section.controller.dto.response;

import lombok.Getter;

@Getter
public class SectionUpdateResponseDTO {

    public Long sectionId;

    public String title;

    public int orderNo;

    public SectionUpdateResponseDTO(Long sectionId, String title, int orderNo) {
        this.sectionId = sectionId;
        this.title = title;
        this.orderNo = orderNo;
    }
}
