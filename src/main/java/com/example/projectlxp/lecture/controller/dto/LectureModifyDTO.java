package com.example.projectlxp.lecture.controller.dto;

import lombok.Getter;

@Getter
public class LectureModifyDTO {

    private Long userId;
    private Long lectureId;
    private String title;
    private int orderNo;

    public LectureModifyDTO(Long userId, Long lectureId, String title, int orderNo) {
        this.userId = userId;
        this.lectureId = lectureId;
        this.orderNo = orderNo;
        this.title = title;
    }
}
