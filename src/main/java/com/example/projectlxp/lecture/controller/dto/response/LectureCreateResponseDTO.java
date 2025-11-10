package com.example.projectlxp.lecture.controller.dto.response;

import com.example.projectlxp.lecture.entity.LectureType;

import lombok.Getter;

@Getter
public class LectureCreateResponseDTO {

    public Long lectureId;

    public String title;

    public LectureType lectureType;

    public int orderNo;

    public LectureCreateResponseDTO(
            Long lectureId, String title, LectureType lectureType, int orderNo) {
        this.lectureId = lectureId;
        this.title = title;
        this.lectureType = lectureType;
        this.orderNo = orderNo;
    }
}
