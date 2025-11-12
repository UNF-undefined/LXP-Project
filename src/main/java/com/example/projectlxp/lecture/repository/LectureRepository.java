package com.example.projectlxp.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.projectlxp.lecture.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Lecture l SET l.isDeleted = true WHERE l.section.id = :sectionId")
    void deleteBySectionId(Long sectionId);

    /** orderNo를 올릴 때 사용합니다. */
    @Modifying
    @Query(
            "UPDATE Lecture l SET l.orderNo = l.orderNo + 1 "
                    + "WHERE l.section.id = :sectionId "
                    + "AND l.orderNo >= :newOrderNo "
                    + "AND l.orderNo < :oldOrderNo")
    void incrementOrderBetween(
            @Param("sectionId") Long sectionId,
            @Param("oldOrderNo") int oldOrderNo,
            @Param("newOrderNo") int newOrderNo);

    /** orderNo를 내릴 때 사용합니다. */
    @Modifying
    @Query(
            "UPDATE Lecture l SET l.orderNo = l.orderNo - 1 "
                    + "WHERE l.section.id = :sectionId "
                    + "AND l.orderNo > :oldOrderNo "
                    + "AND l.orderNo <= :newOrderNo")
    void decrementOrderBetween(
            @Param("sectionId") Long sectionId,
            @Param("oldOrderNo") int oldOrderNo,
            @Param("newOrderNo") int newOrderNo);

    @Modifying
    @Query(
            "UPDATE Lecture l SET l.orderNo = l.orderNo - 1"
                    + " WHERE l.section.id = :sectionId AND l.orderNo > :orderNo")
    void decrementOrderAfterDelete(
            @Param("sectionId") Long sectionId, @Param("orderNo") int deletedOrderNo);
}
