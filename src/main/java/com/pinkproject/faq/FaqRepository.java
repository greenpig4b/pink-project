package com.pinkproject.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Integer> {

    @Query("SELECT f FROM Faq f JOIN FETCH f.admin ORDER BY f.createdAt DESC")
    List<Faq> findAllWithAdmin();
}
