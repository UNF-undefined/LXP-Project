package com.example.projectlxp.section.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.controller.dto.response.SectionUpdateResponseDTO;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.section.service.SectionService;
import com.example.projectlxp.section.service.validator.SectionValidator;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final SectionValidator sectionValidator;

    @Autowired
    public SectionServiceImpl(
            SectionRepository sectionRepository,
            CourseRepository courseRepository,
            LectureRepository lectureRepository,
            SectionValidator sectionValidator) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.sectionValidator = sectionValidator;
    }

    @Override
    @Transactional
    public SectionCreateResponseDTO registerSection(
            Long userId, Long courseId, String title, int orderNo) {

        // find Course By id
        Course findCourse =
                courseRepository
                        .findById(courseId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "Course를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // check Section Authority
        sectionValidator.validateSectionAuthority(findCourse.getInstructor().getId(), userId);

        // check section by courseId & orderNo
        sectionRepository
                .findByCourseIdAndOrderNo(courseId, orderNo)
                .ifPresent(
                        section -> {
                            throw new CustomBusinessException("동일한 순서로 섹션을 생성하고 있습니다.");
                        });

        // create Section
        Section newSection = Section.createSection(findCourse, title, orderNo);

        // save Section
        Section savedSection = sectionRepository.save(newSection);

        // convert To SectionCreateResponseDTO
        SectionCreateResponseDTO response;
        response =
                new SectionCreateResponseDTO(
                        savedSection.getId(), savedSection.getTitle(), savedSection.getOrderNo());

        return response;
    }

    @Override
    @Transactional
    public SectionUpdateResponseDTO modifySection(
            Long userId, Long sectionId, String title, int orderNo) {
        // TODO : order No가 이미 존재하면, 이미 존재하는 orderNO를 변경해야 하나 ?!

        // find Section By ID
        Section findSection =
                sectionRepository
                        .findById(sectionId)
                        .orElseThrow(() -> new CustomBusinessException("존재하지 않는 섹션입니다."));

        // check Section Authority
        sectionValidator.validateSectionAuthority(findSection, userId);

        // update Section
        findSection.updateSection(title, orderNo);

        // convert To SectionUpdateResponseDTO & return
        return new SectionUpdateResponseDTO(
                findSection.getId(), findSection.getTitle(), findSection.getOrderNo());
    }

    @Override
    @Transactional
    public void removeSection(Long userId, Long sectionId) {
        // find section
        Section findSection =
                sectionRepository
                        .findById(sectionId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "섹션이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // check Section Authority
        sectionValidator.validateSectionAuthority(findSection, userId);

        // delete section
        sectionRepository.deleteById(sectionId);

        // delete lecture by section id
        lectureRepository.deleteBySectionId(sectionId);

        // orderNo 재정렬 필요
        sectionRepository.decrementOrderAfterDelete(
                findSection.getCourse().getId(), findSection.getOrderNo());
    }
}
