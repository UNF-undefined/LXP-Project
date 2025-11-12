package com.example.projectlxp.course.repository.querydsl;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseSortBy;
import com.example.projectlxp.course.entity.QCourse;
import com.example.projectlxp.enrollment.entity.QEnrollment;
import com.example.projectlxp.review.entity.QReview;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

public class CourseQuery extends JPAQuery<Course> {

    private final QCourse course;
    private final QReview review;
    private final QEnrollment enrollment;

    public CourseQuery(EntityManager em, QCourse course, QReview review, QEnrollment enrollment) {
        super(em);
        this.course = course;
        this.review = review;
        this.enrollment = enrollment;
    }

    public CourseQuery selectFromCourse() {
        this.from(course);
        return this;
    }

    public CourseQuery whereCourse(BooleanExpression... conditions) {
        where(conditions);
        return this;
    }

    public CourseQuery orderByDefault(CourseSortBy sortBy) {
        switch (sortBy) {
            case RATING ->
                    leftJoin(course.reviews, review)
                            .groupBy(course.id)
                            .orderBy(review.rating.avg().desc());
            case POPULARITY ->
                    leftJoin(course.enrollments, enrollment)
                            .groupBy(course.id)
                            .orderBy(enrollment.count().desc());
            case PRICE_ASC -> orderBy(course.price.asc());
            case PRICE_DESC -> orderBy(course.price.desc());
            default -> orderBy(course.createdAt.desc());
        }
        return this;
    }

    public Page<Course> fetchPage(Pageable pageable) {
        Long totalCount = clone().select(course.id.countDistinct()).fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        this.offset(pageable.getOffset());
        this.limit(pageable.getPageSize());

        return new PageImpl<>(this.fetch(), pageable, total);
    }
}
