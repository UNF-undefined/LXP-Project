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
    private double completionRate;
    private boolean isHidden;

    public static EnrolledCourseDTO from(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        return EnrolledCourseDTO.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .thumbnail(course.getThumbnail())
                .isHidden(enrollment.isHidden())
                .build();
    }

    public static EnrolledCourseDTO of(Enrollment enrollment, Long totalLectureCount) {
        double completionRate = 0.0;
        if (totalLectureCount > 0) {
            completionRate =
                    (double) enrollment.getCompletedLectureCount() / totalLectureCount * 100.0;
        }

        return EnrolledCourseDTO.builder()
                .enrollmentId(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .thumbnail(enrollment.getCourse().getThumbnail())
                .completionRate(completionRate)
                .isHidden(enrollment.isHidden())
                .build();
    }
}
