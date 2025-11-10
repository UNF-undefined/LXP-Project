package com.example.projectlxp.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = {"category", "instructor"})
    Optional<Course> findByIdAndCategoryIdAndInstructorId(
            Long courseId, Long categoryId, Long userId);

    @EntityGraph(attributePaths = {"instructor"})
    Optional<Course> findByIdAndInstructorId(Long courseId, Long userId);
}
