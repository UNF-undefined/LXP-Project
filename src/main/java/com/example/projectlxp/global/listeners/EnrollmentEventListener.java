package com.example.projectlxp.global.listeners;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.enrollment.repository.LectureProgressRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.global.events.LectureCompletedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EnrollmentEventListener {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository lectureProgressRepository;

    public EnrollmentEventListener(
            EnrollmentRepository enrollmentRepository,
            LectureProgressRepository lectureProgressRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.lectureProgressRepository = lectureProgressRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLectureCompleted(LectureCompletedEvent event) {
        log.info("LectureCompletedEvent 수신: enrollmentId = {}", event.enrollmentId());

        Enrollment enrollment =
                enrollmentRepository
                        .findById(event.enrollmentId())
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 수강신청입니다. ID: " + event.enrollmentId(),
                                                HttpStatus.NOT_FOUND));

        if (enrollment == null) return;

        long completedCount =
                lectureProgressRepository.countByEnrollmentAndCompleted(enrollment, true);

        enrollment.updateCompletedLectureCount((int) completedCount);
        enrollmentRepository.save(enrollment);
    }
}
