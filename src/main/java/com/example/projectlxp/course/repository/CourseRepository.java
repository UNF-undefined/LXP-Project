package com.example.projectlxp.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.querydsl.CourseQueryRepository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, CourseQueryRepository {

    @Query(
            """
        SELECT c FROM Course c
        JOIN FETCH c.instructor i
        JOIN FETCH c.category cat
        LEFT JOIN FETCH cat.parent p
        WHERE c.id = :courseId AND cat.id = :categoryId AND i.id = :userId
    """)
    Optional<Course> findByIdAndCategoryIdAndInstructorId(
            Long courseId, Long categoryId, Long userId);

    @Query(
            """
        SELECT c FROM Course c
        JOIN FETCH c.instructor i
        JOIN FETCH c.category cat
        LEFT JOIN FETCH cat.parent p
        WHERE c.id = :courseId
    """)
    Optional<Course> findByIdWithInstructorAndCategory(Long courseId);

    Optional<Course> findByIdAndInstructorId(Long courseId, Long instructorId);
}
