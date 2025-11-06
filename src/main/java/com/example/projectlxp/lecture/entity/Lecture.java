package com.example.projectlxp.lecture.entity;

import com.example.projectlxp.enrollment.entity.LectureProgress;
import com.example.projectlxp.section.entity.Section;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "lectures")
@SQLDelete(sql = "UPDATE lectures SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType type;

    @Column(name = "order_no", nullable = false)
    private int orderNo;

    @Column
    private String file;

    @Column(length = 50)
    private String duration;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "lecture")
    private List<LectureProgress> lectureProgresses = new ArrayList<>();
}
