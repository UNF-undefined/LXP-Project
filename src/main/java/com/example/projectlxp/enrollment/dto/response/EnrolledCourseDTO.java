package com.example.projectlxp.enrollment.dto.response;

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
public class EnrolledCourseDTO {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String thumbnail;
    private int progress;

    public static EnrolledCourseDTO from(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        return new EnrolledCourseDTO(
                enrollment.getId(),
                course.getId(),
                course.getTitle(),
                course.getThumbnail(),
                enrollment.getProgress());
    }
}
