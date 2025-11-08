package com.example.projectlxp.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectlxp.course.entity.Course;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {}
