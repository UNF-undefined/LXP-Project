package com.example.projectlxp.enrollment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.user.entity.User;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserAndCourse(User user, Course course);

    @Query(
            value = "SELECT e FROM Enrollment e JOIN FETCH e.course c WHERE e.user.id = :userId",
            countQuery = "SELECT COUNT(e) FROM Enrollment e WHERE e.user.id = :userId")
    Page<Enrollment> findByUserIdWithCourse(@Param("userId") Long userId, Pageable pageable);
}
