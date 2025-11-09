package com.example.projectlxp.enrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.user.entity.User;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserAndCourse(User user, Course course);
}
