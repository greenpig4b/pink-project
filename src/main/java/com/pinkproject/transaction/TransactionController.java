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

    // 통계페이지
    @GetMapping("/api/chart")
    public ResponseEntity<?> getChart(@RequestParam Integer year, @RequestParam Integer month, @RequestParam Integer week){

        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        _ChartRespRecord respRecord = transactionService.getChartTransaction(sessionUser.getId(),year,month,week);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }
}