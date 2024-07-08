package com.pinkproject.transaction;

import com.pinkproject._core.error.exception.Exception403;
import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.Formatter;
import com.pinkproject.transaction.TransactionRequest.SaveTransactionRecord._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest.UpdateTransactionRecord._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionRecord.DailyTransactionDetailRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionRecord.DailyTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionRecord._DailyMainRecord;
import com.pinkproject.transaction.TransactionResponse.SavaTransactionRecord._SaveTransactionRespRecord;
import com.pinkproject.transaction.TransactionResponse.UpdateTransactionRecord._UpdateTransactionRespRecord;
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

    // 월간 거래 내역을 가져오는 메서드
    public _DailyMainRecord getDailyMain(Integer sessionUserId, Integer year, Integer month) {
        // 사용자 정보 조회
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        // 조회할 기간 설정 (해당 월의 첫째 날과 마지막 날)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 주어진 기간 동안의 거래 내역 조회
        List<Transaction> transactions = transactionRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        // 월간 수입, 지출, 총액 계산
        Integer monthlyIncome = transactions.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).mapToInt(Transaction::getAmount).sum();
        Integer monthlyExpense = transactions.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).mapToInt(Transaction::getAmount).sum();
        Integer monthlyTotalAmount = monthlyIncome - monthlyExpense;

        // 날짜별로 거래 내역을 그룹화
        Map<String, List<Transaction>> recordsByDate = transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.getEffectiveDateTime().toLocalDate().toString()));

        // 날짜별 거래 기록 생성
        List<DailyTransactionRecord> dailyTransactionRecords = recordsByDate.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LocalDate date = LocalDate.parse(entry.getKey());
                    List<Transaction> dailyTransactionList = entry.getValue();

                    Integer dailyIncome = dailyTransactionList.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME).mapToInt(Transaction::getAmount).sum();
                    Integer dailyExpense = dailyTransactionList.stream().filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE).mapToInt(Transaction::getAmount).sum();
                    Integer dailyTotalAmount = dailyIncome - dailyExpense;

                    // 일별 거래 세부 내역 생성
                    List<DailyTransactionDetailRecord> dailyTransactionDetailRecords = dailyTransactionList.stream()
                            .map(transaction -> new DailyTransactionDetailRecord(
                                    transaction.getTransactionType(),
                                    transaction.getCategoryIn() != null ? transaction.getCategoryIn().getKorean() : null,
                                    transaction.getCategoryOut() != null ? transaction.getCategoryOut().getKorean() : null,
                                    transaction.getDescription(),
                                    Formatter.formatCreatedAtPeriodWithTime(transaction.getEffectiveDateTime()),
                                    transaction.getAssets() != null ? transaction.getAssets().getKorean() : null, Formatter.number(transaction.getAmount()))).toList();

                    // 일별 거래 기록 반환
                    return new DailyTransactionRecord(Formatter.formatDayOnly(date), Formatter.number(dailyIncome), Formatter.number(dailyExpense), Formatter.number(dailyTotalAmount), dailyTransactionDetailRecords);
                }).toList();

        // 월별 및 일별 거래 내역 반환
        return new _DailyMainRecord(Formatter.formatYearWithSuffix(startDate), Formatter.formatMonthWithSuffix(startDate), Formatter.number(monthlyIncome), Formatter.number(monthlyExpense), Formatter.number(monthlyTotalAmount), dailyTransactionRecords);
    }

    // 거래 저장 메서드
    @Transactional
    public _SaveTransactionRespRecord saveTransaction(_SaveTransactionRecord reqRecord, Integer sessionUserId) {
        // 사용자 정보 조회
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        // 날짜와 시간 확인
        if (reqRecord.yearMonthDate() == null || reqRecord.time() == null) {
            throw new IllegalArgumentException("날짜와 시간을 모두 입력해야 합니다.");
        }

        // 생성 시간 설정
        LocalDateTime createdAt = Formatter.truncateToSeconds(LocalDateTime.parse(reqRecord.yearMonthDate() + "T" + reqRecord.time()));

        // 거래 엔티티 생성 및 저장
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

        transactionRepository.save(transaction);

        // 응답용 데이터 포맷 설정
        String formattedYearMonthDate = Formatter.formatDate(transaction.getCreatedAt());
        String formattedTime = Formatter.formatCreatedAtPeriodWithTime(transaction.getCreatedAt());

        // 저장된 거래 내역 반환
        return new _SaveTransactionRespRecord(
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

    // 거래 업데이트 메서드
    @Transactional
    public _UpdateTransactionRespRecord updateTransaction(Integer transactionId, _UpdateTransactionRecord reqRecord) {
        // 사용자 정보 조회
        User user = userRepository.findById(reqRecord.userId()).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        // 거래 정보 조회
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));

        // 권한 확인
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new Exception403("사용자 권한이 없습니다.");
        }

        // 날짜와 시간 확인
        if (reqRecord.yearMonthDate() == null || reqRecord.time() == null) {
            throw new IllegalArgumentException("날짜와 시간을 모두 입력해야 합니다.");
        }

        // 수정 시간 설정
        LocalDateTime updatedAt = Formatter.truncateToSeconds(LocalDateTime.parse(reqRecord.yearMonthDate() + "T" + reqRecord.time()));

        // 거래 정보 업데이트
        transaction.setTransactionType(reqRecord.transactionType());
        transaction.setCategoryIn(reqRecord.categoryIn() != null ? reqRecord.categoryIn() : null);
        transaction.setCategoryOut(reqRecord.categoryOut() != null ? reqRecord.categoryOut() : null);
        transaction.setAssets(reqRecord.assets());
        transaction.setAmount(reqRecord.amount());
        transaction.setDescription(reqRecord.description());
        transaction.setUpdatedAt(updatedAt);

        transactionRepository.save(transaction);

        // 응답용 데이터 포맷 설정
        String formattedYearMonthDate = Formatter.formatDate(transaction.getUpdatedAt());
        String formattedTime = Formatter.formatCreatedAtPeriodWithTime(transaction.getUpdatedAt());

        // 업데이트된 거래 내역 반환
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

    // 거래 삭제 메서드
    @Transactional
    public void deleteTransaction(Integer transactionId, Integer sessionUserId) {
        // 사용자 정보 조회
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        // 거래 정보 조회
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new Exception404("거래 정보를 찾을 수 없습니다."));

        // 권한 확인
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new Exception403("사용자 권한이 없습니다.");
        }

        // 거래 삭제
        transactionRepository.delete(transaction);
    }
}