package com.example.projectlxp.category.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.global.base.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "categories")
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Course> courses = new ArrayList<>();

    protected Category() {}

    @Builder
    public Category(String name, boolean isDeleted, Category parent, List<Course> courses) {
        this.name = name;
        this.isDeleted = isDeleted;
        this.parent = parent;
        this.courses = courses;
    }

    public void addChildCategory(Category child) {
        this.children.add(child);
        child.parent = this;
    }
}
