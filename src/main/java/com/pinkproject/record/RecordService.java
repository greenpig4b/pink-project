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
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 검색한 것 담음
        List<Record> records = recordRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        // 2. 해당 월의 수입의 합, 지출의 합, 수입/지출의 합이 필요하다.
        Integer monthlyIncome = records.stream()
                .filter(record -> record.getTransactionType() == TransactionType.INCOME)
                .mapToInt(Record::getAmount)
                .sum();

        Integer monthlyExpense = records.stream()
                .filter(record -> record.getTransactionType() == TransactionType.EXPENSE)
                .mapToInt(Record::getAmount)
                .sum();

        Integer monthlyTotalAmount = monthlyIncome - monthlyExpense;

        Map<String, List<Record>> recordsByDate = records.stream()
                .collect(Collectors.groupingBy(record -> record.getCreatedAt().toLocalDate().toString()));

        List<DailyRecord> dailyRecords = recordsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())  // 날짜 기준으로 정렬
                .map(entry -> {
                    String date = entry.getKey();
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

                    List<DailyTransactionDetail> dailyTransactionDetails = dailyRecordList.stream()
                            .map(record -> new DailyTransactionDetail(
                                    record.getTransactionType(),
                                    record.getCategoryIn(),
                                    record.getCategoryOut(),
                                    record.getDescription(),
                                    Formatter.formatCreatedAtPeriodWithTime(record.getCreatedAt()), // 오전/오후 시간 반환
                                    record.getAssets(),
                                    Formatter.number(record.getAmount()) // 세 자리마다 콤마와 "원" 추가
                            )).toList();

                    return new DailyRecord(
                            date.substring(8), // 날짜의 일만 반환
                            Formatter.number(dailyIncome), // 세 자리마다 콤마와 "원" 추가
                            Formatter.number(dailyExpense), // 세 자리마다 콤마와 "원" 추가
                            Formatter.number(dailyTotalAmount), // 세 자리마다 콤마와 "원" 추가
                            dailyTransactionDetails
                    );
                }).toList();

        String yearStr = String.valueOf(year);
        String monthStr = String.valueOf(month);

        return new _DailyMainDTORecord(
                yearStr,
                monthStr,
                Formatter.number(monthlyIncome), // 세 자리마다 콤마와 "원" 추가
                Formatter.number(monthlyExpense), // 세 자리마다 콤마와 "원" 추가
                Formatter.number(monthlyTotalAmount), // 세 자리마다 콤마와 "원" 추가
                dailyRecords
        );
    }
}
