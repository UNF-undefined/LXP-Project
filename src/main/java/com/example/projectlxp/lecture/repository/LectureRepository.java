package com.example.projectlxp.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.lecture.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {}
