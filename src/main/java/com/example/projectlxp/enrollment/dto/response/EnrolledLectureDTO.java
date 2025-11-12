package com.example.projectlxp.enrollment.dto.response;

import java.util.Map;

import com.example.projectlxp.lecture.entity.Lecture;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrolledLectureDTO {
    private Long lectureId;
    private String title;
    private boolean completed;

    public static EnrolledLectureDTO of(Lecture lecture, Map<Long, Boolean> progressMap) {
        return EnrolledLectureDTO.builder()
                .lectureId(lecture.getId())
                .title(lecture.getTitle())
                .completed(progressMap.getOrDefault(lecture.getId(), false))
                .build();
    }
}
