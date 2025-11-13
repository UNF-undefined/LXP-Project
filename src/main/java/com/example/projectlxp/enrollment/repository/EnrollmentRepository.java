package com.example.projectlxp.enrollment.repository;

import java.util.Optional;

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
            value =
                    "SELECT e FROM Enrollment e JOIN FETCH e.course c WHERE e.user.id = :userId AND e.isHidden = :isHidden",
            countQuery =
                    "SELECT COUNT(e) FROM Enrollment e WHERE e.user.id = :userId AND e.isHidden = :isHidden")
    Page<Enrollment> findVisibleByUserIdWithCourse(
            @Param("userId") Long userId, @Param("isHidden") Boolean isHidden, Pageable pageable);

    @Query(
            "SELECT e FROM Enrollment e "
                    + "JOIN FETCH e.user u "
                    + "JOIN FETCH e.course c "
                    + "WHERE e.id = :enrollmentId AND u.id = :userId")
    Optional<Enrollment> findDetailByIdAndUserId(
            @Param("enrollmentId") Long enrollmentId, @Param("userId") Long userId);

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
