package com.pinkproject.transaction;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.transaction.TransactionRequest.SaveTransactionRecord._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest.UpdateTransactionRecord._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionRecord._DailyMainRecord;
import com.pinkproject.transaction.TransactionResponse.SavaTransactionRecord._SaveTransactionRespRecord;
import com.pinkproject.transaction.TransactionResponse.UpdateTransactionRecord._UpdateTransactionRespRecord;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final HttpSession session;

    // 기록 일일 화면 랜더링
    @GetMapping("/records/monthly") // TODO: api빼둠
    public ResponseEntity<?> dailyRecords(@RequestParam Integer year, @RequestParam Integer month) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }

        // 월별 거래 내역을 조회하고 응답으로 반환
        _DailyMainRecord respDTO = transactionService.getDailyMain(1, year, month); // TODO: 세션유저 빼둠
        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 가계부 저장
    @PostMapping("/records") // TODO: API 경로 설정
    public ResponseEntity<?> saveRecord(@RequestBody _SaveTransactionRecord reqRecord) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }

        // 요청된 거래 내역을 저장하고 응답으로 반환
        System.out.println(reqRecord);
        _SaveTransactionRespRecord respDTO = transactionService.saveTransaction(reqRecord, 1); // TODO: 세션유저 빼둠
        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 가계부 수정
    @PutMapping("/records/{id}") // TODO: API 경로 설정
    public ResponseEntity<?> updateRecord(@PathVariable("id") Integer id, @RequestBody _UpdateTransactionRecord reqRecord) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }

        // 요청된 거래 내역을 수정하고 응답으로 반환
        System.out.println(reqRecord);
        _UpdateTransactionRespRecord response = transactionService.updateTransaction(id, reqRecord);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 가계부 삭제
    @DeleteMapping("/records/{id}") // TODO: API 경로 설정
    public ResponseEntity<?> deleteRecord(@PathVariable("id") Integer id) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }

        // 요청된 거래 내역을 삭제
        transactionService.deleteTransaction(id, 1); // TODO: 세션유저 빼둠
        return ResponseEntity.ok(new ApiUtil<>(null));
    }
}