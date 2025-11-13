package com.example.projectlxp.enrollment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.enrollment.service.LectureProgressService;
import com.example.projectlxp.global.dto.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LectureProgressController {

    private final LectureProgressService lectureProgressService;

    @PostMapping("/lectures/{lectureId}/start")
    public BaseResponse<Void> startLecture(
            @RequestParam Long userId, @PathVariable Long lectureId) {

        lectureProgressService.markLectureAsStarted(userId, lectureId);
        return BaseResponse.success("강의 시작 처리가 성공적으로 이루어졌습니다.", null);
    }

    @PostMapping("/lectures/{lectureId}/complete")
    public BaseResponse<Void> completeLecture(
            @RequestParam Long userId, @PathVariable Long lectureId) {

        lectureProgressService.markLectureAsComplete(userId, lectureId);
        return BaseResponse.success("강의 완료 처리가 성공적으로 이루어졌습니다.", null);
    }
}
