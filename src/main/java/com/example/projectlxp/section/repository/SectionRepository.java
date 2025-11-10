package com.example.projectlxp.section.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.section.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByCourseIdAndOrderNo(Long courseId, int orderNo);

    /**
     * Section 삭제 후, 뒤에 오는 섹션들의 orderNo를 1씩 감소시킵니다.
     *
     * <p>예시) orderNo = 3인 섹션이 삭제되면, 기존 orderNo 4, 5, 6번 섹션들이 각각 3, 4, 5로 변경됩니다.
     *
     * @param courseId 해당 섹션이 속한 Course의 ID
     * @param deletedOrderNo 삭제된 섹션의 orderNo
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE Section s SET s.orderNo = s.orderNo - 1 "
                    + "WHERE s.course.id = :courseId AND s.orderNo > :orderNo")
    void decrementOrderAfterDelete(
            @Param("courseId") Long courseId, @Param("orderNo") int deletedOrderNo);
}
