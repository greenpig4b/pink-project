package com.pinkproject.transaction;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse._MonthlyTransactionMainRecord;
import com.pinkproject.transaction.TransactionResponse._SaveTransactionRespRecord;
import com.pinkproject.transaction.TransactionResponse._UpdateTransactionRespRecord;
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
    @GetMapping("/transactions/monthly") // TODO: API 경로 설정
    public ResponseEntity<?> monthlyTransactions(@RequestParam Integer year, @RequestParam Integer month) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }
        _MonthlyTransactionMainRecord respDTO = transactionService.getMonthlyTransactionMain(1, year, month); // TODO: 세션유저 빼둠

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 가계부 저장
    @PostMapping("/transactions") // TODO: api빼둠
    public ResponseEntity<?> saveTransaction(@RequestBody _SaveTransactionRecord reqRecord) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }
        _SaveTransactionRespRecord respDTO = transactionService.saveTransaction(reqRecord, 1); // TODO: 세션유저 빼둠

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 가계부 수정
    @PutMapping("/transactions/{id}") // TODO: API 경로 설정
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Integer id, @RequestBody _UpdateTransactionRecord reqRecord) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }
        _UpdateTransactionRespRecord response = transactionService.updateTransaction(id, reqRecord);

        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 가계부 삭제
    @DeleteMapping("/transactions/{id}") // TODO: api빼둠
    public ResponseEntity<?> deleteTransaction(@PathVariable("id") Integer id) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }
        transactionService.deleteTransaction(id, 1); // TODO: 세션유저 빼둠
        return ResponseEntity.ok(new ApiUtil<>(null));
    }
}