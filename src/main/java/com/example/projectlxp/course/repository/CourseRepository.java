package com.example.projectlxp.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectlxp.course.entity.Course;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {}
