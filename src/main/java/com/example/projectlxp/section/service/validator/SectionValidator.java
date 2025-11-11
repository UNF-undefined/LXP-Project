package com.example.projectlxp.section.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;

@Component
public class SectionValidator {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    @Autowired
    public SectionValidator(
            SectionRepository sectionRepository,
            CourseRepository courseRepository,
            LectureRepository lectureRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
    }

    public void validateSectionAuthority(Section section, Long userId) {
        if (section.getCourse().getInstructor().getId() != userId) {
            throw new CustomBusinessException("섹션을 관리할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    public void validateSectionAuthority(Long authId, Long checkId) {
        if (authId != checkId) {
            throw new CustomBusinessException("섹션을 관리할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
