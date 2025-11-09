package com.example.projectlxp.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.global.base.BaseEntity;
import com.example.projectlxp.review.entity.Review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok 적용
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // 추후 추가 예정
    //    @Column(nullable = false, unique = true, length = 20)
    //    private String nickname;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted = false;

    // ---- 매핑
    @OneToMany(mappedBy = "instructor")
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    public User(String name, Role role, String email, String hashedPassword, String profileImage) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profileImage = profileImage;
    }

    public static User createUser(
            String name, String email, String hashedPassword, Role role, String profileImage) {
        return new User(name, role, email, hashedPassword, profileImage);
    }
}
