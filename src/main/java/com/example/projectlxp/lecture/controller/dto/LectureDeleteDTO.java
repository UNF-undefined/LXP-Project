package com.example.projectlxp.lecture.controller.dto;

import lombok.Getter;

@Getter
public class LectureDeleteDTO {

    private Long userId;
    private Long lectureId;

    public LectureDeleteDTO(Long userId, Long lectureId) {
        this.userId = userId;
        this.lectureId = lectureId;
    }
}
