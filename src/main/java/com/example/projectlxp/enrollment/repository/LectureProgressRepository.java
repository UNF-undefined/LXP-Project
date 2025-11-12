package com.example.projectlxp.enrollment.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.entity.LectureProgress;
import com.example.projectlxp.enrollment.repository.projection.CompletedLectureCountProjection;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {

    /** [필수] 특정 수강신청(Enrollment)에 대한 모든 진도 정보를 조회합니다. (N+1 방지를 위해 Lecture를 함께 fetch) */
    @Query(
            "SELECT lp FROM LectureProgress lp JOIN FETCH lp.lecture WHERE lp.enrollment = :enrollment")
    List<LectureProgress> findAllByEnrollmentWithLecture(
            @Param("enrollment") Enrollment enrollment);

    @Query(
            "SELECT lp.enrollment.id AS enrollmentId, COUNT(lp.id) AS completedCount "
                    + "FROM LectureProgress lp "
                    + "WHERE lp.enrollment.id IN :enrollmentIds AND lp.completed = true "
                    + "GROUP BY lp.enrollment.id")
    List<CompletedLectureCountProjection> findCompletedLectureCountsByEnrollmentIds(
            @Param("enrollmentIds") Set<Long> enrollmentIds);
}
