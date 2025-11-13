package com.example.projectlxp.enrollment.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.entity.LectureProgress;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.enrollment.repository.LectureProgressRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.global.events.LectureCompletedEvent;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.lecture.repository.LectureRepository;

@Service
public class LectureProgressServiceImpl implements LectureProgressService {

    private final LectureProgressRepository lectureProgressRepository;
    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public LectureProgressServiceImpl(
            LectureProgressRepository lectureProgressRepository,
            LectureRepository lectureRepository,
            EnrollmentRepository enrollmentRepository,
            ApplicationEventPublisher eventPublisher) {
        this.lectureProgressRepository = lectureProgressRepository;
        this.lectureRepository = lectureRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void markLectureAsStarted(Long userId, Long lectureId) {
        LectureProgress progress = findOrCreateLectureProgress(userId, lectureId);
        progress.updateLastAccessedAt();
    }

    private LectureProgress findOrCreateLectureProgress(Long userId, Long lectureId) {
        return lectureProgressRepository
                .findByUserAndLecture(userId, lectureId)
                .orElseGet(() -> createLectureProgress(userId, lectureId));
    }

    private LectureProgress createLectureProgress(Long userId, Long lectureId) {
        Lecture lecture =
                lectureRepository
                        .findById(lectureId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 강의입니다. lectureId=" + lectureId,
                                                HttpStatus.NOT_FOUND));

        Long courseId = lecture.getSection().getCourse().getId();

        Enrollment enrollment =
                enrollmentRepository
                        .findByUserIdAndCourseId(userId, courseId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "해당 강의에 대한 수강신청 정보를 찾을 수 없습니다. userId="
                                                        + userId
                                                        + ", courseId="
                                                        + courseId,
                                                HttpStatus.NOT_FOUND));

        LectureProgress newProgress = LectureProgress.create(enrollment, lecture);
        return lectureProgressRepository.save(newProgress);
    }

    @Override
    @Transactional
    public void markLectureAsComplete(Long userId, Long lectureId) {
        LectureProgress progress =
                lectureProgressRepository
                        .findByUserAndLecture(userId, lectureId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 수강목록입니다.", HttpStatus.NOT_FOUND));

        if (progress.isCompleted()) return;

        progress.complete();
        eventPublisher.publishEvent(new LectureCompletedEvent(progress.getEnrollment().getId()));
    }
}
