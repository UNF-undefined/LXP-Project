package com.example.projectlxp.enrollment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.entity.LectureProgress;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {

    @Query(
            "SELECT lp FROM LectureProgress lp JOIN FETCH lp.lecture WHERE lp.enrollment = :enrollment")
    List<LectureProgress> findAllByEnrollmentWithLecture(
            @Param("enrollment") Enrollment enrollment);

    long countByEnrollmentAndCompleted(Enrollment enrollment, boolean completed);

    @Query(
            "SELECT lp FROM LectureProgress lp "
                    + "WHERE lp.enrollment.user.id = :userId AND lp.lecture.id = :lectureId")
    Optional<LectureProgress> findByUserAndLecture(
            @Param("userId") Long userId, @Param("lectureId") Long lectureId);
}
