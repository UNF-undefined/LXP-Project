package com.example.projectlxp.section.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.section.service.SectionService;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRespository;

    @Autowired
    public SectionServiceImpl(
            SectionRepository sectionRepository, CourseRepository courseRespository) {
        this.sectionRepository = sectionRepository;
        this.courseRespository = courseRespository;
    }

    @Override
    @Transactional
    public SectionCreateResponseDTO registerSection(Long courseId, String title, int orderNo) {
        // TODO : IllegalArgumentException으로 하려고 하니 Controller에서 Catch 분리를 어떻게 해야할지 모르겠음 !

        // find Course By id
        Course findCourse =
                courseRespository
                        .findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course를 찾을 수 없습니다."));

        // check section by courseId & orderNo유
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
}
