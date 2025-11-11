package com.example.projectlxp.lecture.controller.dto.response;

import lombok.Getter;

@Getter
public class LectureUpdateResponseDTO {

    private Long lectureId;

    private String title;

    private int orderNo;

    public LectureUpdateResponseDTO(Long lectureId, String title, int orderNo) {
        this.lectureId = lectureId;
        this.title = title;
        this.orderNo = orderNo;
    }
}
