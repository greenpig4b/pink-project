package com.pinkproject.transaction;

import com.pinkproject._core.error.exception.Exception403;
import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.Formatter;
import com.pinkproject._core.utils.SummaryUtil;
import com.pinkproject.memo.Memo;
import com.pinkproject.memo.MemoRepository;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.*;
import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MemoRepository memoRepository;

    public _ChartRespRecord getChartTransaction(Integer sessionUserId, Integer year, Integer month, Integer week) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        _ChartRespRecord.MonthDTO monthDTO = getMonthtransaction(user.getId(), year, month);
        _ChartRespRecord.WeeklyDTO weeklyDTO = getWeeklyTransaction(user.getId(), year, month, week);

        return new _ChartRespRecord(month, week, monthDTO, weeklyDTO);
    }


    // 월간 수입 지출
    public _ChartRespRecord.MonthDTO getMonthtransaction(Integer sessionUserId, Integer year, Integer month) {

        // 0. 인증처리
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        // 1. 권한처리
        if (user.getId() != sessionUserId) {
            throw new Exception403("해당 수입 및 지출을 확인할 권한이 없습니다.");
        }

        List<Transaction> monthDTO = transactionRepository.findAllByYearAndMonth(year, month, sessionUserId);

        // 2. 수입 및 지출 찾기

        // 2-1 수입
        List<_ChartRespRecord.MonthDTO.MonthIcomeDTO> monthIncomeList = monthDTO.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).map(transaction -> _ChartRespRecord.MonthDTO.MonthIcomeDTO.builder().id(transaction.getId()).category(transaction.getTransactionType().getKorean()).amount(transaction.getAmount()).categoryImage(transaction.getCategoryIn().getEmoji()).build()).toList();

        // 2-2 지출
        List<_ChartRespRecord.MonthDTO.MonthSpendingDTO> monthSpendingList = monthDTO.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).map(transaction -> _ChartRespRecord.MonthDTO.MonthSpendingDTO.builder().id(transaction.getId()).category(transaction.getTransactionType().getKorean()).amount(transaction.getAmount())
                //TODO : 회의 후 결정 하기 위해서 일단 생성 해놨습니다.
                .categoryImage(transaction.getCategoryOut().getEmoji()).build()).toList();


        return new _ChartRespRecord.MonthDTO(monthIncomeList, monthSpendingList);

    }

    //-------------

    // 주간 수입 지출
    public _ChartRespRecord.WeeklyDTO getWeeklyTransaction(Integer sessionUserId, Integer year, Integer month, Integer week) {

        // 0. 인증처리
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        // 1. 권한처리
        if (user.getId() != sessionUserId) {
            throw new Exception403("해당 수입 및 지출을 확인할 권한이 없습니다.");
        }

        // 2.주의 시작 날짜와 끝 날짜 계산
        LocalDateTime startDate = getStartOfWeek(year, month, week);
        LocalDateTime endDate = startDate.plusDays(6);

        LocalDateTime startDateTime = startDate;
        LocalDateTime endDateTime = endDate;


        List<Transaction> weeklyDTO = transactionRepository.findAllByYearAndMonthAndWeek(year, month, startDateTime, endDateTime, sessionUserId);

        // 3. 수입 및 지출 나누기

        // 3-1  수입
        List<_ChartRespRecord.WeeklyDTO.WeekIcomeDTO> weekIncomeDTO = weeklyDTO.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).map(transaction -> _ChartRespRecord.WeeklyDTO.WeekIcomeDTO.builder().id(transaction.getId()).category(transaction.getTransactionType().getKorean()).amount(transaction.getAmount()).categoryImage(transaction.getCategoryOut().getEmoji()).build()).toList();


        // 3-2 지출
        List<_ChartRespRecord.WeeklyDTO.WeekSpendingDTO> weekSpendingDTO = weeklyDTO.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).map(transaction -> _ChartRespRecord.WeeklyDTO.WeekSpendingDTO.builder().id(transaction.getId()).category(transaction.getTransactionType().getKorean()).amount(transaction.getAmount()).categoryImage(transaction.getCategoryOut().getEmoji()).build()).toList();

        return new _ChartRespRecord.WeeklyDTO(weekIncomeDTO, weekSpendingDTO);
    }


    // 주 시작 날짜 계산 메서드
    private LocalDateTime getStartOfWeek(Integer year, Integer month, Integer week) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstDayOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));

        return firstDayOfWeek.plusWeeks(week - 1).atStartOfDay();
    }


    public _MonthlyTransactionMainRecord getMonthlyTransactionMain(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);

        Map<String, List<Transaction>> transactionsByDate = transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.getEffectiveDateTime().toLocalDate().toString()));

        List<_MonthlyTransactionMainRecord.DailyTransactionRecord> dailyTransactionRecords = transactionsByDate.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())).map(entry -> {
            LocalDate date = LocalDate.parse(entry.getKey());
            List<Transaction> dailyTransactionList = entry.getValue();

            SummaryUtil.DailySummary dailySummary = SummaryUtil.calculateDailySummary(dailyTransactionList);

            List<_MonthlyTransactionMainRecord.DailyTransactionRecord.DailyTransactionDetailRecord> dailyTransactionDetailRecords = dailyTransactionList.stream().sorted(Comparator.comparing(Transaction::getEffectiveDateTime).reversed()).map(transaction -> new _MonthlyTransactionMainRecord.DailyTransactionRecord.DailyTransactionDetailRecord(transaction.getId(), transaction.getTransactionType(), transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null, transaction.getCategoryIn() != null ? transaction.getCategoryIn().getEmoji() : null, transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null, transaction.getCategoryOut() != null ? transaction.getCategoryOut().getEmoji() : null, transaction.getDescription(), Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()), transaction.getAssets() != null ? transaction.getAssets().getKorean() : null, Formatter.number(transaction.getAmount()))).toList();

            return new _MonthlyTransactionMainRecord.DailyTransactionRecord(Formatter.formatDayOnly(date), Formatter.number(dailySummary.getDailyIncome()), Formatter.number(dailySummary.getDailyExpense()), Formatter.number(dailySummary.getDailyTotalAmount()), dailyTransactionDetailRecords);
        }).toList();

        return new _MonthlyTransactionMainRecord(sessionUserId, Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyTotalAmount()), dailyTransactionRecords);
    }

    @Transactional
    public _SaveTransactionRespRecord saveTransaction(_SaveTransactionRecord reqRecord, Integer sessionUserId) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        if (reqRecord.yearMonthDate() == null || reqRecord.time() == null) {
            throw new IllegalArgumentException("날짜와 시간을 모두 입력해야 합니다.");
        }

        LocalDateTime createdAt = Formatter.truncateToSeconds(LocalDateTime.parse(reqRecord.yearMonthDate() + "T" + reqRecord.time()));

        Transaction transaction = Transaction.builder().user(user).transactionType(reqRecord.transactionType()).categoryIn(reqRecord.categoryIn() != null ? reqRecord.categoryIn() : null).categoryOut(reqRecord.categoryOut() != null ? reqRecord.categoryOut() : null).assets(reqRecord.assets()).amount(reqRecord.amount()).description(reqRecord.description()).createdAt(createdAt).build();

        transaction = transactionRepository.saveAndFlush(transaction);

        LocalDate startDate = createdAt.toLocalDate().withDayOfMonth(1);
        LocalDate endDate = createdAt.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);
        SummaryUtil.DailySummary dailySummary = summary.getDailySummaries().get(createdAt.toLocalDate());

        return new _SaveTransactionRespRecord(transaction.getUser().getId(), Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyTotalAmount()), new _SaveTransactionRespRecord.DailySaveTransactionRecord(Formatter.formatDayOnly(createdAt.toLocalDate()), Formatter.number(dailySummary.getDailyIncome()), Formatter.number(dailySummary.getDailyExpense()), Formatter.number(dailySummary.getDailyTotalAmount()), new _SaveTransactionRespRecord.DailySaveTransactionRecord.DailySaveTransactionDetailRecord(transaction.getId(), transaction.getTransactionType(), transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null, transaction.getCategoryIn() != null ? transaction.getCategoryIn().getEmoji() : null, transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null, transaction.getCategoryOut() != null ? transaction.getCategoryOut().getEmoji() : null, transaction.getDescription(), Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()), transaction.getAssets() != null ? transaction.getAssets().getKorean() : null, Formatter.number(transaction.getAmount()))));
    }

    @Transactional
    public _UpdateTransactionRespRecord updateTransaction(Integer transactionId, _UpdateTransactionRecord reqRecord) {
        User user = userRepository.findById(reqRecord.userId()).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));

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

        return new _UpdateTransactionRespRecord(transaction.getUser().getId(), Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyTotalAmount()), new _UpdateTransactionRespRecord.DailyUpdateTransactionRecord(Formatter.formatDayOnly(updatedAt.toLocalDate()), Formatter.number(dailySummary.getDailyIncome()), Formatter.number(dailySummary.getDailyExpense()), Formatter.number(dailySummary.getDailyTotalAmount()), new _UpdateTransactionRespRecord.DailyUpdateTransactionRecord.DailyUpdateTransactionDetailRecord(transaction.getId(), transaction.getTransactionType(), transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null, transaction.getCategoryIn() != null ? transaction.getCategoryIn().getEmoji() : null, transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null, transaction.getCategoryOut() != null ? transaction.getCategoryOut().getEmoji() : null, transaction.getDescription(), Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()), transaction.getAssets() != null ? transaction.getAssets().getKorean() : null, Formatter.number(transaction.getAmount()))));
    }

    @Transactional
    public _DeleteTransactionRespRecord deleteTransaction(Integer transactionId, Integer sessionUserId) {
        System.out.println("transactionId = " + transactionId);
        System.out.println("sessionUserId = " + sessionUserId);
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));
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

        return new _DeleteTransactionRespRecord(user.getId(), Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyTotalAmount()), List.of(new _DeleteTransactionRespRecord.DailyDeleteTransactionRecord(Formatter.formatDayOnly(dateTime.toLocalDate()), Formatter.number(dailySummary.getDailyIncome()), Formatter.number(dailySummary.getDailyExpense()), Formatter.number(dailySummary.getDailyTotalAmount()))), "삭제가 완료되었습니다.");
    }

    public _MonthlyFinancialReport getMonthlyFinancialReportMain(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);
        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);

        SummaryUtil.Summary previousSummary = calculatePreviousMonthSummary(user, year, month);

        String previousMonthExpenseComparison = Formatter.calculatePercentageChange(previousSummary.getMonthlyExpense(), summary.getMonthlyExpense());
        String previousMonthIncomeComparison = Formatter.calculatePercentageChange(previousSummary.getMonthlyIncome(), summary.getMonthlyIncome());

        return new _MonthlyFinancialReport(sessionUserId, Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyTotalAmount()), Formatter.formatYearMonthDay(startDate), Formatter.formatYearMonthDay(endDate), new _MonthlyFinancialReport.MonthlyExpenseSummary(previousMonthExpenseComparison, Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyExpenseByAsset(Assets.CARD)), Formatter.number(summary.getMonthlyExpenseByAsset(Assets.CASH)), Formatter.number(summary.getMonthlyExpenseByAsset(Assets.BANK))), new _MonthlyFinancialReport.MonthlyIncomeSummary(previousMonthIncomeComparison, Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyIncomeByAsset(Assets.CARD)), Formatter.number(summary.getMonthlyIncomeByAsset(Assets.CASH)), Formatter.number(summary.getMonthlyIncomeByAsset(Assets.BANK))));
    }

    private SummaryUtil.Summary calculatePreviousMonthSummary(User user, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1).minusMonths(1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Transaction> previousTransactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);
        return SummaryUtil.calculateSummary(previousTransactions);
    }

    // 달력 페이지
    public _MonthlyCalendar getMonthlyCalendarSummaryAndDailyDetail(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        SummaryUtil.Summary summary = SummaryUtil.calculateSummary(transactions);

        List<Memo> memos = memoRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        List<_MonthlyCalendar.DailySummary> dailySummaries = transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.getCreatedAt().toLocalDate())).entrySet().stream().map(entry -> {
            LocalDate date = entry.getKey();
            List<Transaction> dailyTransactions = entry.getValue();

            boolean hasMemo = memos.stream().anyMatch(memo -> memo.getEffectiveDateTime().toLocalDate().equals(date));
            SummaryUtil.DailySummary dailySummary = SummaryUtil.calculateDailySummary(dailyTransactions);

            String dailyIncome = dailySummary.getDailyIncome() > 0 ? Formatter.number(dailySummary.getDailyIncome()) : "";
            String dailyExpense = dailySummary.getDailyExpense() > 0 ? Formatter.number(dailySummary.getDailyExpense()) : "";
            String dailyTotalAmount = (dailySummary.getDailyIncome() > 0 && dailySummary.getDailyExpense() > 0) ? Formatter.number(dailySummary.getDailyTotalAmount()) : "";

            List<_MonthlyCalendar.DailySummary.DailyDetail.Memo> dailyMemos = memos.stream().filter(memo -> memo.getEffectiveDateTime().toLocalDate().equals(date)).map(memo -> new _MonthlyCalendar.DailySummary.DailyDetail.Memo(memo.getId(), memo.getContent())).collect(Collectors.toList());

            List<_MonthlyCalendar.DailySummary.DailyDetail.TransactionDetail> transactionDetails = dailyTransactions.stream().map(transaction -> new _MonthlyCalendar.DailySummary.DailyDetail.TransactionDetail(transaction.getId(), transaction.getTransactionType(), transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : transaction.getCategoryOut().getKorean(), transaction.getDescription(), transaction.getAssets().getKorean(), Formatter.number(transaction.getAmount()))).collect(Collectors.toList());

            _MonthlyCalendar.DailySummary.DailyDetail dailyDetail = new _MonthlyCalendar.DailySummary.DailyDetail(Formatter.formatDay(date), Formatter.formatYearMonth(date), Formatter.formatDayOfWeek(date), dailyMemos, transactionDetails);

            return new _MonthlyCalendar.DailySummary(Formatter.formatDay(date), hasMemo, dailyIncome, dailyExpense, dailyTotalAmount, dailyDetail);
        }).collect(Collectors.toList());

        return new _MonthlyCalendar(sessionUserId, Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(summary.getMonthlyIncome()), Formatter.number(summary.getMonthlyExpense()), Formatter.number(summary.getMonthlyTotalAmount()), dailySummaries);
    }


}
