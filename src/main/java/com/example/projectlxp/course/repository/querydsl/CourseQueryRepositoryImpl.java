package com.example.projectlxp.course.repository.querydsl;

import static com.example.projectlxp.category.entity.QCategory.category;
import static com.example.projectlxp.course.entity.QCourse.course;
import static com.example.projectlxp.enrollment.entity.QEnrollment.enrollment;
import static com.example.projectlxp.review.entity.QReview.review;
import static com.example.projectlxp.user.entity.QUser.user;
import static java.util.Objects.nonNull;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.entity.Course;
import com.querydsl.core.types.dsl.BooleanExpression;

public class CourseQueryRepositoryImpl implements CourseQueryRepository {

    private final EntityManager em;

    public CourseQueryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<Course> searchAll(CourseSearchRequest request, Pageable pageable) {
        long total =
                new CourseQuery(em, course)
                        .count(
                                inInstructors(request.instructorIds()),
                                inCategories(request.categoryIds()));
        return new CourseQuery(em, course, review, enrollment, user, category)
                .fromCourse()
                .join()
                .whereCourse(
                        inInstructors(request.instructorIds()), inCategories(request.categoryIds()))
                .orderByDefault(request.sortBy())
                .fetchPage(pageable, total);
    }

    private BooleanExpression inInstructors(List<Long> instructorIds) {
        return nonNull(instructorIds) && !instructorIds.isEmpty()
                ? course.instructor.id.in(instructorIds)
                : null;
    }

    private BooleanExpression inCategories(List<Long> categoryIds) {
        BooleanExpression result = null;

        if (nonNull(categoryIds) && !categoryIds.isEmpty()) {
            BooleanExpression isLeafCategory = course.category.id.in(categoryIds);
            BooleanExpression isParentCategory = course.category.parent.id.in(categoryIds);

            result = isLeafCategory.or(isParentCategory);
        }

        return result;
    }
}
