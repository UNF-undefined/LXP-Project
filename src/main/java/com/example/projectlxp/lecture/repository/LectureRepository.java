package com.example.projectlxp.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.lecture.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Lecture l SET l.isDeleted = true WHERE l.section.id = :sectionId")
    void deleteBySectionId(Long sectionId);
}
