package com.example.projectlxp.course.repository.querydsl;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.category.entity.QCategory;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseSortBy;
import com.example.projectlxp.course.entity.QCourse;
import com.example.projectlxp.enrollment.entity.QEnrollment;
import com.example.projectlxp.review.entity.QReview;
import com.example.projectlxp.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

public class CourseQuery extends JPAQuery<Course> {

    private final QCourse course;
    private final QReview review;
    private final QEnrollment enrollment;
    private final QUser user;
    private final QCategory category;
    private final QCategory parentCategory = new QCategory("parentCategory");

    public CourseQuery(EntityManager em, QCourse course) {
        this(em, course, null, null, null, null);
    }

    public CourseQuery(
            EntityManager em,
            QCourse course,
            QReview review,
            QEnrollment enrollment,
            QUser user,
            QCategory category) {
        super(em);
        this.course = course;
        this.review = review != null ? review : QReview.review;
        this.enrollment = enrollment != null ? enrollment : QEnrollment.enrollment;
        this.user = user != null ? user : QUser.user;
        this.category = category != null ? category : QCategory.category;
    }

    public CourseQuery fromCourse() {
        this.from(course);
        return this;
    }

    public CourseQuery join() {
        this.join(course.instructor, user)
                .fetchJoin()
                .join(course.category, category)
                .fetchJoin()
                .leftJoin(category.parent, parentCategory)
                .fetchJoin();
        return this;
    }

    public CourseQuery whereCourse(BooleanExpression... conditions) {
        this.where(conditions);
        return this;
    }

    public CourseQuery orderByDefault(CourseSortBy sortBy) {
        switch (sortBy) {
            case RATING ->
                    this.leftJoin(course.reviews, review)
                            .groupBy(course.id)
                            .orderBy(review.rating.avg().desc());
            case POPULARITY ->
                    this.leftJoin(course.enrollments, enrollment)
                            .groupBy(course.id)
                            .orderBy(enrollment.count().desc());
            case PRICE_ASC -> this.orderBy(course.price.asc());
            case PRICE_DESC -> this.orderBy(course.price.desc());
            default -> this.orderBy(course.createdAt.desc());
        }
        return this;
    }

    public Page<Course> fetchPage(Pageable pageable, long total) {
        this.offset(pageable.getOffset());
        this.limit(pageable.getPageSize());

        return new PageImpl<>(this.fetch(), pageable, total);
    }

    public long count(BooleanExpression... whereConditions) {
        Long totalCount =
                this.select(course.id.countDistinct())
                        .from(course)
                        .where(whereConditions)
                        .fetchOne();

        return totalCount != null ? totalCount : 0L;
    }
}
