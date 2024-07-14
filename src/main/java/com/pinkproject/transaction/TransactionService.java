package com.pinkproject.transaction;

import com.pinkproject._core.error.exception.Exception403;
import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.Formatter;
import com.pinkproject._core.utils.SummaryUtil;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse._DeleteTransactionRespRecord;
import com.pinkproject.transaction.TransactionResponse._MonthlyTransactionMainRecord;
import com.pinkproject.transaction.TransactionResponse._SaveTransactionRespRecord;
import com.pinkproject.transaction.TransactionResponse._UpdateTransactionRespRecord;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public _MonthlyTransactionMainRecord getMonthlyTransactionMain(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);

        Map<String, List<Transaction>> transactionsByDate = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getEffectiveDateTime().toLocalDate().toString()));

        List<_MonthlyTransactionMainRecord.DailyTransactionRecord> dailyTransactionRecords = transactionsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(entry -> {
                    LocalDate date = LocalDate.parse(entry.getKey());
                    List<Transaction> dailyTransactionList = entry.getValue();

                    SummaryUtil.DailySummary dailySummary = SummaryUtil.calculateDailySummary(dailyTransactionList);

                    List<_MonthlyTransactionMainRecord.DailyTransactionRecord.DailyTransactionDetailRecord> dailyTransactionDetailRecords = dailyTransactionList.stream()
                            .sorted(Comparator.comparing(Transaction::getEffectiveDateTime).reversed())
                            .map(transaction -> new _MonthlyTransactionMainRecord.DailyTransactionRecord.DailyTransactionDetailRecord(
                                    transaction.getId(),
                                    transaction.getTransactionType(),
                                    transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null,
                                    transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null,
                                    transaction.getDescription(),
                                    Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()),
                                    transaction.getAssets() != null ? transaction.getAssets().getKorean() : null,
                                    Formatter.number(transaction.getAmount())))
                            .toList();

                    return new _MonthlyTransactionMainRecord.DailyTransactionRecord(
                            Formatter.formatDayOnly(date),
                            Formatter.number(dailySummary.getDailyIncome()),
                            Formatter.number(dailySummary.getDailyExpense()),
                            Formatter.number(dailySummary.getDailyTotalAmount()),
                            dailyTransactionDetailRecords);
                })
                .toList();

        return new _MonthlyTransactionMainRecord(
                sessionUserId,
                Formatter.formatYearWithSuffix(startDate),
                Formatter.formatMonthWithSuffix(startDate),
                Formatter.number(summary.getMonthlyIncome()),
                Formatter.number(summary.getMonthlyExpense()),
                Formatter.number(summary.getMonthlyTotalAmount()),
                dailyTransactionRecords);
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

        LocalDate startDate = createdAt.toLocalDate().withDayOfMonth(1);
        LocalDate endDate = createdAt.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);
        SummaryUtil.DailySummary dailySummary = summary.getDailySummaries().get(createdAt.toLocalDate());

        return new _SaveTransactionRespRecord(
                transaction.getUser().getId(),
                Formatter.number(summary.getMonthlyIncome()),
                Formatter.number(summary.getMonthlyExpense()),
                Formatter.number(summary.getMonthlyTotalAmount()),
                List.of(new _SaveTransactionRespRecord.DailySaveTransactionRecord(
                        Formatter.formatDayOnly(createdAt.toLocalDate()),
                        Formatter.number(dailySummary.getDailyIncome()),
                        Formatter.number(dailySummary.getDailyExpense()),
                        Formatter.number(dailySummary.getDailyTotalAmount()),
                        new _SaveTransactionRespRecord.DailySaveTransactionRecord.DailySaveTransactionDetailRecord(
                                transaction.getId(),
                                transaction.getTransactionType(),
                                transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null,
                                transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null,
                                transaction.getDescription(),
                                Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()),
                                transaction.getAssets() != null ? transaction.getAssets().getKorean() : null,
                                Formatter.number(transaction.getAmount())
                        )
                ))
        );
    }

    @Transactional
    public _UpdateTransactionRespRecord updateTransaction(Integer transactionId, _UpdateTransactionRecord reqRecord) {
        User user = userRepository.findById(reqRecord.userId()).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new Exception403("사용자 권한이 없습니다.");
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

        LocalDate startDate = updatedAt.toLocalDate().withDayOfMonth(1);
        LocalDate endDate = updatedAt.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);
        SummaryUtil.DailySummary dailySummary = summary.getDailySummaries().get(updatedAt.toLocalDate());

        return new _UpdateTransactionRespRecord(
                transaction.getUser().getId(),
                Formatter.number(summary.getMonthlyIncome()),
                Formatter.number(summary.getMonthlyExpense()),
                Formatter.number(summary.getMonthlyTotalAmount()),
                List.of(new _UpdateTransactionRespRecord.DailyUpdateTransactionRecord(
                        Formatter.formatDayOnly(updatedAt.toLocalDate()),
                        Formatter.number(dailySummary.getDailyIncome()),
                        Formatter.number(dailySummary.getDailyExpense()),
                        Formatter.number(dailySummary.getDailyTotalAmount()),
                        new _UpdateTransactionRespRecord.DailyUpdateTransactionRecord.DailyUpdateTransactionDetailRecord(
                                transaction.getId(),
                                transaction.getTransactionType(),
                                transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null,
                                transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null,
                                transaction.getDescription(),
                                Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()),
                                transaction.getAssets() != null ? transaction.getAssets().getKorean() : null,
                                Formatter.number(transaction.getAmount())
                        )
                ))
        );
    }

    @Transactional
    public _DeleteTransactionRespRecord deleteTransaction(Integer transactionId, Integer sessionUserId) {
        System.out.println("transactionId = " + transactionId);
        System.out.println("sessionUserId = " + sessionUserId);
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));
        System.out.println(1);
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new Exception403("사용자 권한이 없습니다.");
        }

        transactionRepository.delete(transaction);

        LocalDateTime dateTime = transaction.getCreatedAt();
        LocalDate startDate = dateTime.toLocalDate().withDayOfMonth(1);
        LocalDate endDate = dateTime.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);
        SummaryUtil.DailySummary dailySummary = summary.getDailySummaries().getOrDefault(dateTime.toLocalDate(), new SummaryUtil.DailySummary(0, 0, 0));

        return new _DeleteTransactionRespRecord(
                user.getId(),
                Formatter.number(summary.getMonthlyIncome()),
                Formatter.number(summary.getMonthlyExpense()),
                Formatter.number(summary.getMonthlyTotalAmount()),
                List.of(new _DeleteTransactionRespRecord.DailyDeleteTransactionRecord(
                        Formatter.formatDayOnly(dateTime.toLocalDate()),
                        Formatter.number(dailySummary.getDailyIncome()),
                        Formatter.number(dailySummary.getDailyExpense()),
                        Formatter.number(dailySummary.getDailyTotalAmount())
                )),
                "삭제가 완료되었습니다."
        );
    }
}
