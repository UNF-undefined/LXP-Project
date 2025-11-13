package com.example.projectlxp.enrollment.service;

public interface LectureProgressService {
    void markLectureAsStarted(Long userId, Long lectureId);

    void markLectureAsComplete(Long userId, Long lectureId);
}
