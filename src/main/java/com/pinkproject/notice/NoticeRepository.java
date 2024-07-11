package com.pinkproject.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    @Query("SELECT n FROM Notice n JOIN FETCH n.admin ORDER BY n.createdAt DESC")
    List<Notice> findAllWithAdmin();
}
