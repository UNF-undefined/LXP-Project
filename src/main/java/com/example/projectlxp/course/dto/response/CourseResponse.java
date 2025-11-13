package com.example.projectlxp.course.dto.response;

import java.util.List;

import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.section.entity.Section;

public record CourseResponse(CourseDTO course, List<SectionLectureDTO> sections) {

    public static CourseResponse of(Course course, List<Section> sections) {
        List<SectionLectureDTO> sectionDtos = sections.stream().map(SectionLectureDTO::of).toList();
        return new CourseResponse(CourseDTO.from(course), sectionDtos);
    }

    public record SectionLectureDTO(
            Long sectionId, String sectionTitle, int orderNo, List<LectureDTO> lectures) {

        public static SectionLectureDTO of(Section section) {
            List<LectureDTO> lectures = section.getLectures().stream().map(LectureDTO::of).toList();
            return new SectionLectureDTO(
                    section.getId(), section.getTitle(), section.getOrderNo(), lectures);
        }
    }

    public record LectureDTO(Long lectureId, String lectureTitle, String duration, int orderNo) {
        public static LectureDTO of(Lecture lecture) {
            return new LectureDTO(
                    lecture.getId(),
                    lecture.getTitle(),
                    lecture.getDuration(),
                    lecture.getOrderNo());
        }
    }
}
