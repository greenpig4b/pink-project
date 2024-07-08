package com.pinkproject.transaction;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.Formatter;
import com.pinkproject.transaction.TransactionRequest.SaveTransactionDTO._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO.DailyTransactionDetailRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO.DailyTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO._DailyMainRecord;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public _DailyMainRecord getDailyMain(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        // _DailyMainDTORecord에 담을 정보를 추린다.
        // 조회 시작일과 종료일을 설정
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 주어진 기간 동안의 기록을 검색
        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        // 해당 월의 수입의 합, 지출의 합, 수입/지출의 합을 계산
        Integer monthlyIncome = transactions.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).mapToInt(Transaction::getAmount).sum();

        Integer monthlyExpense = transactions.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).mapToInt(Transaction::getAmount).sum();

        Integer monthlyTotalAmount = monthlyIncome - monthlyExpense;

        // 날짜별로 기록을 그룹화
        Map<String, List<Transaction>> recordsByDate = transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.getCreatedAt().toLocalDate().toString()));

        // 날짜별 기록을 생성하고 정렬
        List<DailyTransactionRecord> dailyTransactionRecords = recordsByDate.entrySet().stream().sorted(Map.Entry.comparingByKey())  // 날짜 기준으로 정렬
                .map(entry -> {
                    LocalDate date = LocalDate.parse(entry.getKey());
                    List<Transaction> dailyTransactionList = entry.getValue();

                    Integer dailyIncome = dailyTransactionList.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).mapToInt(Transaction::getAmount).sum();

                    Integer dailyExpense = dailyTransactionList.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).mapToInt(Transaction::getAmount).sum();

                    Integer dailyTotalAmount = dailyIncome - dailyExpense;

                    // 일별 거래 세부 정보를 생성
                    List<DailyTransactionDetailRecord> dailyTransactionDetailRecords = dailyTransactionList.stream()
                            .map(transaction -> new DailyTransactionDetailRecord(
                                    transaction.getTransactionType(),
                                    transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null,
                                    transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null,
                                    transaction.getDescription(),
                                    Formatter.formatCreatedAtPeriodWithTime(transaction.getCreatedAt()), // 오전/오후 시간 반환
                                    transaction.getAssets() != null ? transaction.getAssets().getKorean() : null, Formatter.number(transaction.getAmount()))).toList();

                    // 일별 기록을 생성
                    return new DailyTransactionRecord(Formatter.formatDayOnly(date), Formatter.number(dailyIncome), Formatter.number(dailyExpense), Formatter.number(dailyTotalAmount), dailyTransactionDetailRecords);
                }).toList();

        // 월별 및 일별 기록을 포함한 DTO 객체를 반환
        return new _DailyMainRecord(Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(monthlyIncome), Formatter.number(monthlyExpense), Formatter.number(monthlyTotalAmount), dailyTransactionRecords);
    }

    // 가계부 인서트
    @Transactional
    public void saveTransaction(_SaveTransactionRecord reqRecord, Integer sessionUserId) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        if (reqRecord.yearMonthDate() == null || reqRecord.time() == null) {
            throw new IllegalArgumentException("날짜와 시간을 모두 입력해야 합니다.");
        }

        LocalDateTime createdAt = LocalDateTime.parse(reqRecord.yearMonthDate() + "T" + reqRecord.time());

        Transaction.TransactionBuilder transaction = Transaction.builder()
                .user(user)
                .transactionType(reqRecord.transactionType())
                .assets(reqRecord.assets())
                .amount(reqRecord.amount())
                .description(reqRecord.description())
                .createdAt(createdAt);

        if (reqRecord.categoryIn() != null) {
            transaction.categoryIn(reqRecord.categoryIn());
        } else if (reqRecord.categoryOut() != null) {
            transaction.categoryOut(reqRecord.categoryOut());
        }

        transactionRepository.save(transaction.build());
    }
}
