package com.pinkproject.record;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Integer> {
}
