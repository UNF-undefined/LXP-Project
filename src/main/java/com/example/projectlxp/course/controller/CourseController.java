package com.example.projectlxp.course.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.course.dto.CourseDTO;
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

    @PostMapping
    public BaseResponse<CourseDTO> registerCourse(
            @RequestBody CourseSaveRequest request, @RequestParam Long userId) {
        return BaseResponse.success(courseService.saveCourse(request, userId));
    }
}
