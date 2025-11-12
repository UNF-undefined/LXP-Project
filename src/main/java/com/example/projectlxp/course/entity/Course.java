package com.example.projectlxp.course.entity;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.global.base.BaseEntity;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "courses")
@SQLDelete(sql = "UPDATE courses SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Course extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @Column private String summary;

    @Lob @Column private String description;

    @Min(value = 0)
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private int price = 0;

    @Builder.Default @Column private String thumbnail = "default_thumbnail_url.png";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel level;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Review> reviews = new ArrayList<>();

    public void updateDetails(
            String title,
            String summary,
            String description,
            Integer price,
            String thumbnail,
            CourseLevel level,
            Category newCategory) {

        this.title = (isNull(title)) ? this.title : title;
        this.summary = (isNull(summary)) ? this.summary : summary;
        this.description = (isNull(description)) ? this.description : description;
        if (nonNull(price)) {
            if (price < 0) {
                throw new IllegalArgumentException("가격은 음수일 수 없습니다.");
            }
            this.price = price;
        }
        this.thumbnail = (isNull(thumbnail)) ? this.thumbnail : thumbnail;
        this.level = (isNull(level)) ? this.level : level;
        this.category = (isNull(newCategory)) ? this.category : newCategory;
    }
}
