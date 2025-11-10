package com.example.projectlxp.section.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.controller.dto.response.SectionUpdateResponseDTO;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.section.service.SectionService;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRespository;
    private final LectureRepository lectureRepository;

    @Autowired
    public SectionServiceImpl(
            SectionRepository sectionRepository,
            CourseRepository courseRespository,
            LectureRepository lectureRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRespository = courseRespository;
        this.lectureRepository = lectureRepository;
    }

    @Override
    @Transactional
    public SectionCreateResponseDTO registerSection(
            Long userId, Long courseId, String title, int orderNo) {
        // TODO : Global Exception이 만들어지면 수정하자 !

        // find Course By id
        Course findCourse =
                courseRespository
                        .findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course를 찾을 수 없습니다."));

        // check who created course
        if (findCourse.getInstructor().getId() != userId) {
            throw new IllegalArgumentException("섹션을 생성할 권한이 없습니다.");
        }

        // check section by courseId & orderNo
        sectionRepository
                .findByCourseIdAndOrderNo(courseId, orderNo)
                .ifPresent(
                        section -> {
                            throw new IllegalStateException("동일한 순서로 Section을 생성하고 있습니다.");
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
    public SectionUpdateResponseDTO modifySection(Long sectionId, String title, int orderNo) {
        // TODO : order No가 이미 존재하면, 이미 존재하는 orderNO를 변경해야 하나 ?!

        // find Section By ID
        Section findSection =
                sectionRepository
                        .findById(sectionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Section 입니다."));

        // update Section
        findSection.updateSection(title, orderNo);

        // convert To SectionUpdateResponseDTO & return
        return new SectionUpdateResponseDTO(
                findSection.getId(), findSection.getTitle(), findSection.getOrderNo());
    }

    @Override
    @Transactional
    public void removeSection(Long sectionId) {
        // find section
        Section findSection =
                sectionRepository
                        .findById(sectionId)
                        .orElseThrow(() -> new IllegalArgumentException("섹션이 존재하지 않습니다."));

        // delete section
        sectionRepository.deleteById(sectionId);

        // delete lecture by section id
        lectureRepository.deleteBySectionId(sectionId);

        // orderNo 재정렬 필요
        sectionRepository.decrementOrderAfterDelete(
                findSection.getCourse().getId(), findSection.getOrderNo());
    }
}
