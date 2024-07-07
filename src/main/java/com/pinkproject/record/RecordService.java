package com.pinkproject.record;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO.DailyRecord;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO.DailyTransactionDetail;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import com.pinkproject.record.enums.TransactionType;
import com.pinkproject.user.SessionUser;
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

    public _DailyMainDTORecord getDailyMain(SessionUser sessionUser, Integer year, Integer month) {
        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        // _DailyMainDTORecord에 담을 정보를 추린다.
        // 1. 연/월이 필요하다. -> 컨트롤러에서 쿼리스트링으로 받아오자.
        // 월의 첫째 날과 마지막 날 사이의 값을 모두 검색
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
                                    record.getCreatedAt().toString(),
                                    record.getAssets(),
                                    record.getAmount()
                            )).toList();

                    return new DailyRecord(
                            date,
                            dailyIncome,
                            dailyExpense,
                            dailyTotalAmount,
                            dailyTransactionDetails
                    );
                }).toList();

        String yearMonth = String.format("%04d-%02d", year, month);

        // 구한 값들을 레코드에 담아서 리턴한다.
        return new _DailyMainDTORecord(
                yearMonth,
                monthlyIncome,
                monthlyExpense,
                monthlyTotalAmount,
                dailyRecords
        );
        // DailyRecord에 담을 정보를 추린다.
        // 3. 기록이 있는 날의 날짜가 필요하다.(기록이 없으면 날짜가 없어야 함)
        // 4. 기록이 있는 날의 수입의 합, 지출의 합, 수입/지출의 합이 필요하다.

        // DailyTransactionDetail에 담을 정보를 추린다.
        // 5. 해당하는 날짜의 상세 내역이 필요하다.
    }
}
