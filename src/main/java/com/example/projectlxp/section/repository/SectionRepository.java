package com.example.projectlxp.section.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.section.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByCourseIdAndOrderNo(Long courseId, int orderNo);
}
