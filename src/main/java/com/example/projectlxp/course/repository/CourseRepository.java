package com.example.projectlxp.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query(
            "SELECT c FROM Course c "
                    + "JOIN FETCH c.instructor i "
                    + "JOIN FETCH c.category cat "
                    + "LEFT JOIN FETCH cat.parent p "
                    + "WHERE c.id = :courseId AND cat.id = :categoryId AND i.id = :userId")
    Optional<Course> findByIdAndCategoryIdAndInstructorId(
            Long courseId, Long categoryId, Long userId);

    @EntityGraph(attributePaths = {"instructor"})
    Optional<Course> findByIdAndInstructorId(Long courseId, Long userId);
}
