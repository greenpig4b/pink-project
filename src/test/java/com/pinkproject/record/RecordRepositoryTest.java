package com.pinkproject.record;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.pinkproject.record.enums.CategoryIn.SALARY;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecordRepositoryTest {
    @Autowired
    private RecordRepository recordRepository;

    @Test
    void findByUserIdAndCreatedAtBetween_test() {


        Integer userId = 1;
        int year = 2024;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Record> record = recordRepository.findByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);

        assertThat(record.getFirst().getId()).isEqualTo(142);
        assertThat(record.getFirst().getAssets().getKorean()).isEqualTo("은행");
        assertThat(record.getFirst().getCategoryIn()).isEqualTo(SALARY);
    }
}