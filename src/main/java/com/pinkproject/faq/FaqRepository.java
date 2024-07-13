package com.pinkproject.faq;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Integer> {

    @Query("SELECT f FROM Faq f JOIN FETCH f.admin ORDER BY f.createdAt DESC")
    List<Faq> findAllWithAdmin();

    @Query("SELECT f FROM Faq f JOIN FETCH f.admin WHERE f.title LIKE %:keyword% ORDER BY f.createdAt DESC")
    List<Faq> findByKeywordWithFaq(@Param("keyword") String keyword);


    @Query("SELECT f FROM Faq f JOIN FETCH f.admin ORDER BY f.createdAt DESC")
    Page<Faq> findAllWithAdmin(Pageable pageable);

    @Query("SELECT f FROM Faq f JOIN FETCH f.admin WHERE f.title LIKE %:keyword% ORDER BY f.createdAt DESC")
    Page<Faq> findByKeywordWithFaq(@Param("keyword") String keyword, Pageable pageable);
}
