package com.pinkproject.transaction;

import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.SessionUser;
import org.assertj.core.internal.Integers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.pinkproject.transaction.enums.CategoryIn.SALARY;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void findByUserIdAndCreatedAtBetween_test() {


        Integer userId = 1;
        int year = 2024;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Transaction> transaction = transactionRepository.findByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);

        assertThat(transaction.getFirst().getId()).isEqualTo(142);
        assertThat(transaction.getFirst().getAssets().getKorean()).isEqualTo("은행");
        assertThat(transaction.getFirst().getCategoryIn()).isEqualTo(SALARY);
    }


    // 월단위 테스트
    @Test
    void findAllByYearAndMonth() {
        // given
        Integer month = 5;
        Integer year = 2024;
        Integer userId = 1;

        // when
        List<Transaction> transactions = transactionRepository.findAllByYearAndMonth(year, month,userId);

        // then
        assertThat(transactions).isNotEmpty();

        List<Transaction> incomeTransactions = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME)
                .collect(Collectors.toList());

        List<Transaction> expenseTransactions = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        //  수입
        assertThat(incomeTransactions).isNotEmpty();
        assertThat(incomeTransactions).allMatch(transaction ->
                transaction.getCreatedAt().getYear() == 2024 &&
                        transaction.getCreatedAt().getMonthValue() == 5
        );

        // 지출
        assertThat(expenseTransactions).isNotEmpty();
        assertThat(expenseTransactions).allMatch(transaction ->
                transaction.getCreatedAt().getYear() == 2024 &&
                        transaction.getCreatedAt().getMonthValue() == 5
        );

        // Eye
        System.out.println("Total transactions: " + transactions.size());
        System.out.println("Income transactions: " + incomeTransactions.size());
        System.out.println("Expense transactions: " + expenseTransactions.size());
    }

    // 주간단위 테스트
    @Test
    void findAllByYearAndMonthAndWeek() {
        // given
        Integer year = 2024;
        Integer month = 5;
        Integer week = 2; // 두 번째 주
        Integer userId = 1;

        // 주의 시작 날짜와 끝 날짜 계산
        LocalDateTime startDate = getStartOfWeek(year, month, week);
        LocalDateTime endDate = startDate.plusDays(6);

        LocalDateTime startDateTime = startDate;
        LocalDateTime endDateTime = endDate;

        // when
        List<Transaction> transactions = transactionRepository.findAllByYearAndMonthAndWeek(year, month, startDateTime, endDateTime, userId);

        // then
        assertThat(transactions).isNotEmpty();

        List<Transaction> incomeTransactions = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME)
                .collect(Collectors.toList());

        List<Transaction> expenseTransactions = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        // ---- 수입
        assertThat(incomeTransactions).isNotEmpty();
        assertThat(incomeTransactions).allMatch(transaction ->
                transaction.getCreatedAt().getYear() == year &&
                        transaction.getCreatedAt().getMonthValue() == month &&
                        !transaction.getCreatedAt().isBefore(startDateTime) &&
                        !transaction.getCreatedAt().isAfter(endDateTime)
        );

        // ----- 지출
        assertThat(expenseTransactions).isNotEmpty();
        assertThat(expenseTransactions).allMatch(transaction ->
                transaction.getCreatedAt().getYear() == year &&
                        transaction.getCreatedAt().getMonthValue() == month &&
                        !transaction.getCreatedAt().isBefore(startDateTime) &&
                        !transaction.getCreatedAt().isAfter(endDateTime)
        );

        // Eye
        System.out.println("전체 지출리스트: " + transactions.size());
        System.out.println("수입리스트: " + incomeTransactions.size());
        System.out.println("지출리스트: " + expenseTransactions.size());
    }

    // 주 시작 날짜 계산 메서드
    private LocalDateTime getStartOfWeek(int year, int month, int week) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstDayOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));

        return firstDayOfWeek.plusWeeks(week - 1).atStartOfDay();
    }


}
