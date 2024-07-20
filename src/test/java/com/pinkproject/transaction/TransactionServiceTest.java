package com.pinkproject.transaction;

import com.pinkproject._core.utils.SummaryUtil;
import com.pinkproject.memo.MemoRepository;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.*;
import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class TransactionServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private MemoRepository memoRepository;

    @MockBean
    private SummaryUtil summaryUtil;

    @Autowired
    private TransactionService transactionService;

    private User user;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("testUser@test.com");

        transaction = new Transaction();
        transaction.setId(1);
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setAmount(1000);
        transaction.setDescription("Test transaction");
        transaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSaveTransaction_test() {
        // given
        _SaveTransactionRecord record = new _SaveTransactionRecord(
                1,
                TransactionType.INCOME,
                "2023-07-01",
                "10:00:00",
                1000,
                CategoryIn.SALARY,
                CategoryOut.FOOD,
                Assets.BANK,
                "description"
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenReturn(transaction);

        SummaryUtil.Summary summary = mock(SummaryUtil.Summary.class);
        SummaryUtil.DailySummary dailySummary = mock(SummaryUtil.DailySummary.class);
        when(dailySummary.getDailyIncome()).thenReturn(1000);
        when(summary.getMonthlyIncome()).thenReturn(1000);
        when(summary.getMonthlyExpense()).thenReturn(500);
        when(summary.getMonthlyTotalAmount()).thenReturn(500);

        Map<LocalDate, SummaryUtil.DailySummary> dailySummaries = new HashMap<>();
        dailySummaries.put(LocalDate.parse("2023-07-01"), dailySummary);
        when(summary.getDailySummaries()).thenReturn(dailySummaries);

        try (MockedStatic<SummaryUtil> mockedStatic = mockStatic(SummaryUtil.class)) {
            mockedStatic.when(() -> SummaryUtil.calculateSummary(anyList())).thenReturn(summary);

            // when
            _SaveTransactionRespRecord response = transactionService.saveTransaction(record, user.getId());

            // then
            assertNotNull(response);
            verify(userRepository, times(1)).findById(user.getId());
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
        }
    }

    @Test
    void testUpdateTransaction_test() {
        // given
        _UpdateTransactionRecord record = new _UpdateTransactionRecord(
                1,
                1,
                TransactionType.EXPENSE,
                "2023-07-01",
                "12:00:00",
                500,
                CategoryIn.SALARY,
                CategoryOut.FOOD,
                Assets.BANK,
                "updated description"
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        SummaryUtil.Summary summary = mock(SummaryUtil.Summary.class);
        SummaryUtil.DailySummary dailySummary = mock(SummaryUtil.DailySummary.class);
        when(dailySummary.getDailyIncome()).thenReturn(1000);
        when(summary.getMonthlyIncome()).thenReturn(1000);
        when(summary.getMonthlyExpense()).thenReturn(500);
        when(summary.getMonthlyTotalAmount()).thenReturn(500);

        Map<LocalDate, SummaryUtil.DailySummary> dailySummaries = new HashMap<>();
        dailySummaries.put(LocalDate.parse("2023-07-01"), dailySummary);
        when(summary.getDailySummaries()).thenReturn(dailySummaries);

        try (MockedStatic<SummaryUtil> mockedStatic = mockStatic(SummaryUtil.class)) {
            mockedStatic.when(() -> SummaryUtil.calculateSummary(anyList())).thenReturn(summary);

            // when
            _UpdateTransactionRespRecord response = transactionService.updateTransaction(transaction.getId(), record);

            // then
            assertNotNull(response);
            verify(userRepository, times(1)).findById(user.getId());
            verify(transactionRepository, times(1)).findById(transaction.getId());
            verify(transactionRepository, times(1)).saveAndFlush(transaction);
        }
    }

    @Test
    void testDeleteTransaction_test() {
        // given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        SummaryUtil.Summary summary = mock(SummaryUtil.Summary.class);
        SummaryUtil.DailySummary dailySummary = mock(SummaryUtil.DailySummary.class);
        when(dailySummary.getDailyIncome()).thenReturn(1000);
        when(summary.getMonthlyIncome()).thenReturn(1000);
        when(summary.getMonthlyExpense()).thenReturn(500);
        when(summary.getMonthlyTotalAmount()).thenReturn(500);

        Map<LocalDate, SummaryUtil.DailySummary> dailySummaries = new HashMap<>();
        dailySummaries.put(LocalDate.parse("2023-07-01"), dailySummary);
        when(summary.getDailySummaries()).thenReturn(dailySummaries);

        try (MockedStatic<SummaryUtil> mockedStatic = mockStatic(SummaryUtil.class)) {
            mockedStatic.when(() -> SummaryUtil.calculateSummary(anyList())).thenReturn(summary);

            // when
            _DeleteTransactionRespRecord response = transactionService.deleteTransaction(transaction.getId(), user.getId());

            // then
            assertNotNull(response);
            verify(userRepository, times(1)).findById(user.getId());
            verify(transactionRepository, times(1)).findById(transaction.getId());
            verify(transactionRepository, times(1)).delete(transaction);
        }
    }

    @Test
    void testGetMonthlyTransactionMain_test() {
        // given
        int year = 2023;
        int month = 7;

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        SummaryUtil.Summary summary = mock(SummaryUtil.Summary.class);
        SummaryUtil.DailySummary dailySummary = mock(SummaryUtil.DailySummary.class);
        when(dailySummary.getDailyIncome()).thenReturn(1000);
        when(summary.getMonthlyIncome()).thenReturn(1000);
        when(summary.getMonthlyExpense()).thenReturn(500);
        when(summary.getMonthlyTotalAmount()).thenReturn(500);

        try (MockedStatic<SummaryUtil> mockedStatic = mockStatic(SummaryUtil.class)) {
            mockedStatic.when(() -> SummaryUtil.calculateSummary(anyList())).thenReturn(summary);

            // when
            _MonthlyTransactionMainRecord response = transactionService.getMonthlyTransactionMain(user.getId(), year, month);

            // then
            assertNotNull(response);
            verify(userRepository, times(1)).findById(user.getId());
        }
    }

    @Test
    void testGetChartTransaction_test() {
        // given
        int year = 2023;
        int month = 7;
        int week = 2;

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        _ChartRespRecord response = transactionService.getChartTransaction(user.getId(), year, month, week);

        // then
        assertNotNull(response);
        verify(userRepository, times(3)).findById(user.getId());
    }

    @Test
    void testGetMonthlyCalendarSummaryAndDailyDetail_test() {
        // given
        int year = 2023;
        int month = 7;

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        SummaryUtil.Summary summary = mock(SummaryUtil.Summary.class);
        SummaryUtil.DailySummary dailySummary = mock(SummaryUtil.DailySummary.class);
        when(dailySummary.getDailyIncome()).thenReturn(1000);
        when(summary.getMonthlyIncome()).thenReturn(1000);
        when(summary.getMonthlyExpense()).thenReturn(500);
        when(summary.getMonthlyTotalAmount()).thenReturn(500);

        try (MockedStatic<SummaryUtil> mockedStatic = mockStatic(SummaryUtil.class)) {
            mockedStatic.when(() -> SummaryUtil.calculateSummary(anyList())).thenReturn(summary);

            // when
            _MonthlyCalendar response = transactionService.getMonthlyCalendarSummaryAndDailyDetail(user.getId(), year, month);

            // then
            assertNotNull(response);
            verify(userRepository, times(1)).findById(user.getId());
        }
    }

    @Configuration
    @Profile("test")
    static class TestConfig {
        @Bean
        public HttpSession httpSession() {
            return mock(HttpSession.class);
        }

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionService transactionService(UserRepository userRepository, TransactionRepository transactionRepository, MemoRepository memoRepository) {
            return new TransactionService(userRepository, transactionRepository, memoRepository);
        }
    }
}
