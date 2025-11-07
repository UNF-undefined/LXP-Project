package com.example.projectlxp.review.repository;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 강좌(Course)에 해당하는 리뷰 목록을 페이징 처리하여 조회.
     * Pageable 객체에 담긴 정렬(sort) 및 페이징(page, size) 정보를 바탕으로
     * JPA가 자동으로 쿼리를 생성.
     *
     * @param course 조회할 강좌 엔티티
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징 처리된 리뷰 목록 (Page<Review>)
     */
    Page<Review> findByCourse(Course course, Pageable pageable);
}