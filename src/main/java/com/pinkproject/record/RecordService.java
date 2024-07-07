package com.pinkproject.record;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.Formatter;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO.DailyRecord;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO.DailyTransactionDetail;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import com.pinkproject.record.enums.TransactionType;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    public _DailyMainDTORecord getDailyMain(Integer sessionUserUd, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserUd)
                .orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        // _DailyMainDTORecord에 담을 정보를 추린다.
        // 조회 시작일과 종료일을 설정
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 주어진 기간 동안의 기록을 검색
        List<Record> records = recordRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        // 해당 월의 수입의 합, 지출의 합, 수입/지출의 합을 계산
        Integer monthlyIncome = records.stream()
                .filter(record -> record.getTransactionType() == TransactionType.INCOME)
                .mapToInt(Record::getAmount)
                .sum();

        Integer monthlyExpense = records.stream()
                .filter(record -> record.getTransactionType() == TransactionType.EXPENSE)
                .mapToInt(Record::getAmount)
                .sum();

        Integer monthlyTotalAmount = monthlyIncome - monthlyExpense;

        // 날짜별로 기록을 그룹화
        Map<String, List<Record>> recordsByDate = records.stream()
                .collect(Collectors.groupingBy(record -> record.getCreatedAt().toLocalDate().toString()));

        // 날짜별 기록을 생성하고 정렬
        List<DailyRecord> dailyRecords = recordsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())  // 날짜 기준으로 정렬
                .map(entry -> {
                    LocalDate date = LocalDate.parse(entry.getKey());
                    List<Record> dailyRecordList = entry.getValue();

                    Integer dailyIncome = dailyRecordList.stream()
                            .filter(record -> record.getTransactionType() == TransactionType.INCOME)
                            .mapToInt(Record::getAmount)
                            .sum();

                    Integer dailyExpense = dailyRecordList.stream()
                            .filter(record -> record.getTransactionType() == TransactionType.EXPENSE)
                            .mapToInt(Record::getAmount)
                            .sum();

                    Integer dailyTotalAmount = dailyIncome - dailyExpense;

                    // 일별 거래 세부 정보를 생성
                    List<DailyTransactionDetail> dailyTransactionDetails = dailyRecordList.stream()
                            .map(record -> new DailyTransactionDetail(
                                    record.getTransactionType(),
                                    record.getCategoryIn() != null ? record.getCategoryIn().getKorean() : null,
                                    record.getCategoryOut() != null ? record.getCategoryOut().getKorean() : null,
                                    record.getDescription(),
                                    Formatter.formatCreatedAtPeriodWithTime(record.getCreatedAt()), // 오전/오후 시간 반환
                                    record.getAssets() != null ? record.getAssets().getKorean() : null,
                                    Formatter.number(record.getAmount())
                            )).toList();

                    // 일별 기록을 생성
                    return new DailyRecord(
                            Formatter.formatDayOnly(date),
                            Formatter.number(dailyIncome),
                            Formatter.number(dailyExpense),
                            Formatter.number(dailyTotalAmount),
                            dailyTransactionDetails
                    );
                }).toList();

        // 월별 및 일별 기록을 포함한 DTO 객체를 반환
        return new _DailyMainDTORecord(
                Formatter.formatYearWithSuffix(startDate),
                Formatter.formatMonthWithSuffix(startDate),
                Formatter.number(monthlyIncome),
                Formatter.number(monthlyExpense),
                Formatter.number(monthlyTotalAmount),
                dailyRecords
        );
    }
}
