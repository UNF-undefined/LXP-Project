package com.example.projectlxp.lecture.controller.dto.request;

import lombok.Getter;

@Getter
public class LectureUpdateRequestDTO {

    private String title;

    private int orderNo;

    public LectureUpdateRequestDTO(String title, int orderNo) {
        this.title = title;
        this.orderNo = orderNo;
    }
}
