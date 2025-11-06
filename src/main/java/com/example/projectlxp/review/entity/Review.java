package com.example.projectlxp.review.entity;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_review_user_course",
            columnNames = {"user_id", "course_id"}
        )
    }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String content;

    // 스펙에 명시된 CHECK 제약조건 추가
    @Column(nullable = false, columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
    private int rating;

    // 연관관계 매핑 (FK: user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연관관계 매핑 (FK: course_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
