package com.example.projectlxp.lecture.controller.dto.response;

import com.example.projectlxp.lecture.entity.LectureType;

public record LectureCreateResponseDTO(
        Long lectureId, String title, LectureType lectureType, int orderNo) {}
