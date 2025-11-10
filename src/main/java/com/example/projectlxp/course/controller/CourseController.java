package com.example.projectlxp.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.course.dto.CourseResponse;
import com.example.projectlxp.course.dto.CourseSaveRequest;
import com.example.projectlxp.course.service.CourseService;
import com.example.projectlxp.global.dto.BaseResponse;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    public BaseResponse<CourseResponse> search(@PathVariable Long courseId) {
        return BaseResponse.success(courseService.searchCourse(courseId));
    }

    @PostMapping
    public BaseResponse<CourseResponse> registerCourse(
            @RequestBody CourseSaveRequest request, @RequestParam Long userId) {
        return BaseResponse.success(courseService.saveCourse(request, userId));
    }
}
