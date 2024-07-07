package com.pinkproject.record;

import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import com.pinkproject.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    _DailyMainDTORecord findByUserId(User user);
}
