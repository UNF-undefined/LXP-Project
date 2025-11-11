package com.example.projectlxp.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.user.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 강좌(Course)에 해당하는 리뷰 목록을 페이징 처리하여 조회. Pageable 객체에 담긴 정렬(sort) 및 페이징(page, size) 정보를 바탕으로
     * JPA가 자동으로 쿼리를 생성.
     *
     * @param course 조회할 강좌 엔티티
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징 처리된 리뷰 목록 (Page<Review>)
     */
    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.user u WHERE r.course = :course",
            countQuery = "SELECT COUNT(r) FROM Review r WHERE r.course = :course")
    Page<Review> findByCourse(Course course, Pageable pageable);

    /** 특정 유저가 특정 강좌에 대해 리뷰를 작성했는지 확인 */
    boolean existsByUserAndCourse(User user, Course course);
}
