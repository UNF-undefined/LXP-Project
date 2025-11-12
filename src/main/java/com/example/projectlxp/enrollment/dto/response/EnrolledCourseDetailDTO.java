package com.example.projectlxp.enrollment.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.enrollment.entity.Enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledCourseDetailDTO {
    private Long enrollmentId;
    private LocalDateTime enrolledAt;
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private String courseThumbnailUrl;
    private double completionRate;
    private List<EnrolledLectureDTO> lectures;

    public static EnrolledCourseDetailDTO of(
            Enrollment enrollment,
            Course course,
            double completionRate,
            List<EnrolledLectureDTO> lectures) {
        return EnrolledCourseDetailDTO.builder()
                .enrollmentId(enrollment.getId())
                .enrolledAt(enrollment.getCreatedAt())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .instructorName(course.getInstructor().getName())
                .completionRate(completionRate)
                .lectures(lectures)
                .build();
    }
}
