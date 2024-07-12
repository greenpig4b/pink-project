package com.pinkproject.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    @Query("SELECT n FROM Notice n JOIN FETCH n.admin ORDER BY n.createdAt DESC")
    List<Notice> findAllWithAdmin();

    @Query("SELECT n FROM Notice n JOIN FETCH n.admin WHERE n.title LIKE %:keyword% ORDER BY n.createdAt DESC")
    List<Notice> findByKeywordWithNotice(@Param("keyword") String keyword);
}
