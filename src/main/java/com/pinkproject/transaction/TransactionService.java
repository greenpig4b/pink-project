package com.pinkproject.transaction;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.Formatter;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse._DailyTransactionMainRecord;
import com.pinkproject.transaction.TransactionResponse._SaveTransactionRespRecord;
import com.pinkproject.transaction.TransactionResponse._UpdateTransactionRespRecord;
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

    public _DailyTransactionMainRecord getDailyTransactionMain(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        Integer monthlyIncome = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).mapToInt(Transaction::getAmount).sum();
        Integer monthlyExpense = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).mapToInt(Transaction::getAmount).sum();
        Integer monthlyTotalAmount = monthlyIncome - monthlyExpense;

        Map<String, List<Transaction>> transactionsByDate = transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.getEffectiveDateTime().toLocalDate().toString()));

        List<_DailyTransactionMainRecord.DailyTransactionRecord> dailyTransactionRecords = transactionsByDate.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LocalDate date = LocalDate.parse(entry.getKey());
                    List<Transaction> dailyTransactionList = entry.getValue();

                    Integer dailyIncome = dailyTransactionList.stream()
                            .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).mapToInt(Transaction::getAmount).sum();
                    Integer dailyExpense = dailyTransactionList.stream()
                            .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).mapToInt(Transaction::getAmount).sum();
                    Integer dailyTotalAmount = dailyIncome - dailyExpense;

                    List<_DailyTransactionMainRecord.DailyTransactionRecord.DailyTransactionDetailRecord> dailyTransactionDetailRecords = dailyTransactionList.stream()
                            .map(transaction -> new _DailyTransactionMainRecord.DailyTransactionRecord.DailyTransactionDetailRecord(
                                    transaction.getId(),
                                    transaction.getTransactionType(),
                                    transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null,
                                    transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null,
                                    transaction.getDescription(),
                                    Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()),
                                    transaction.getAssets() != null ? transaction.getAssets().getKorean() : null, Formatter.number(transaction.getAmount()))).toList();

                    return new _DailyTransactionMainRecord.DailyTransactionRecord(Formatter.formatDayOnly(date), Formatter.number(dailyIncome), Formatter.number(dailyExpense), Formatter.number(dailyTotalAmount), dailyTransactionDetailRecords);
                }).toList();

        return new _DailyTransactionMainRecord(sessionUserId, Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(monthlyIncome), Formatter.number(monthlyExpense), Formatter.number(monthlyTotalAmount), dailyTransactionRecords);
    }

    @Transactional
    public _SaveTransactionRespRecord saveTransaction(_SaveTransactionRecord reqRecord, Integer sessionUserId) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        if (reqRecord.yearMonthDate() == null || reqRecord.time() == null) {
            throw new IllegalArgumentException("날짜와 시간을 모두 입력해야 합니다.");
        }

        LocalDateTime createdAt = Formatter.truncateToSeconds(LocalDateTime.parse(reqRecord.yearMonthDate() + "T" + reqRecord.time()));

        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(reqRecord.transactionType())
                .categoryIn(reqRecord.categoryIn() != null ? reqRecord.categoryIn() : null)
                .categoryOut(reqRecord.categoryOut() != null ? reqRecord.categoryOut() : null)
                .assets(reqRecord.assets())
                .amount(reqRecord.amount())
                .description(reqRecord.description())
                .createdAt(createdAt)
                .build();

        transaction = transactionRepository.saveAndFlush(transaction);

        String formattedYearMonthDate = Formatter.formatDate(transaction.getCreatedAt());
        String formattedTime = Formatter.formatCreatedAtPeriodWithTime(transaction.getCreatedAt());

        return new _SaveTransactionRespRecord(
                transaction.getUser().getId(),
                transaction.getTransactionType(),
                formattedYearMonthDate,
                formattedTime,
                Formatter.number(transaction.getAmount()),
                transaction.getCategoryIn(),
                transaction.getCategoryOut(),
                transaction.getAssets() != null ? transaction.getAssets().getKorean() : null,
                transaction.getDescription()
        );
    }

    @Transactional
    public _UpdateTransactionRespRecord updateTransaction(Integer transactionId, _UpdateTransactionRecord reqRecord) {
        User user = userRepository.findById(reqRecord.userId()).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("사용자 권한이 없습니다.");
        }

        if (reqRecord.yearMonthDate() == null || reqRecord.time() == null) {
            throw new IllegalArgumentException("날짜와 시간을 모두 입력해야 합니다.");
        }

        LocalDateTime updatedAt = Formatter.truncateToSeconds(LocalDateTime.parse(reqRecord.yearMonthDate() + "T" + reqRecord.time()));

        transaction.setTransactionType(reqRecord.transactionType());
        transaction.setCategoryIn(reqRecord.categoryIn() != null ? reqRecord.categoryIn() : null);
        transaction.setCategoryOut(reqRecord.categoryOut() != null ? reqRecord.categoryOut() : null);
        transaction.setAssets(reqRecord.assets());
        transaction.setAmount(reqRecord.amount());
        transaction.setDescription(reqRecord.description());
        transaction.setUpdatedAt(updatedAt);

        transactionRepository.saveAndFlush(transaction);

        String formattedYearMonthDate = Formatter.formatDate(transaction.getUpdatedAt());
        String formattedTime = Formatter.formatCreatedAtPeriodWithTime(transaction.getUpdatedAt());

        return new _UpdateTransactionRespRecord(
                transaction.getId(),
                transaction.getTransactionType(),
                formattedYearMonthDate,
                formattedTime,
                Formatter.number(transaction.getAmount()),
                transaction.getCategoryIn(),
                transaction.getCategoryOut(),
                transaction.getAssets() != null ? transaction.getAssets().getKorean() : null,
                transaction.getDescription()
        );
    }

    @Transactional
    public void deleteTransaction(Integer transactionId, Integer sessionUserId) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("사용자 권한이 없습니다.");
        }

        transactionRepository.delete(transaction);
    }
}