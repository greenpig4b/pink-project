package com.pinkproject.transaction;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.*;
import com.pinkproject.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final HttpSession session;

    // 월간 transaction 랜더링
    @GetMapping("/api/transactions/monthly")
    public ResponseEntity<?> monthlyTransactions(@RequestParam Integer year, @RequestParam Integer month) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _MonthlyTransactionMainRecord respRecord = transactionService.getMonthlyTransactionMain(sessionUser.getId(), year, month);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 가계부 저장
    @PostMapping("/api/transactions")
    public ResponseEntity<?> saveTransaction(@RequestBody _SaveTransactionRecord reqRecord) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _SaveTransactionRespRecord respRecord = transactionService.saveTransaction(reqRecord, sessionUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 가계부 수정
    @PutMapping("/api/transactions/{transactionId}")
    public ResponseEntity<?> updateTransaction(@PathVariable("transactionId") Integer transactionId, @RequestBody _UpdateTransactionRecord reqRecord) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _UpdateTransactionRespRecord respRecord = transactionService.updateTransaction(transactionId, reqRecord);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 가계부 삭제
    @DeleteMapping("/api/transactions/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable("transactionId") Integer transactionId) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _DeleteTransactionRespRecord respRecord = transactionService.deleteTransaction(transactionId, sessionUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 결산 메인 페이지
    @GetMapping("/api/financial-report")
    public ResponseEntity<?> getMonthlyFinancialReportMain(@RequestParam Integer year, @RequestParam Integer month){
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _MonthlyFinancialReport respRecord = transactionService.getMonthlyFinancialReportMain(sessionUser.getId(), year, month);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 달력 페이지
    @GetMapping("/api/calendar")
    public ResponseEntity<?> getCalendar(@RequestParam Integer year, @RequestParam Integer month){
        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        _MonthlyCalendar respRecord = transactionService.getMonthlyCalendarSummaryAndDailyDetail(sessionUser.getId(), year, month);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }
    @GetMapping("/api/chart/monthly")
    public ResponseEntity<?> getMonthlyChart(
            @RequestParam Integer year,
            @RequestParam Integer month) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        Object respRecord = transactionService.getMonthtransaction(sessionUser.getId(), year, month);

        System.out.println(respRecord);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    @GetMapping("/api/chart/weekly")
    public ResponseEntity<?> getWeeklyChart(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate);
        Object respRecord = transactionService.getWeeklyTransaction(sessionUser.getId(), year, month, startDateTime, endDateTime);

        System.out.println(respRecord);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }
}