package com.pinkproject.transaction;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.transaction.TransactionRequest.SaveTransactionDTO._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.DailyTransactionDTO._DailyMainRecord;
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
//        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return ResponseEntity.status(401).build();
//        }
        _DailyMainRecord respDTO = transactionService.getDailyMain(1, year, month); // TODO: 세션유저 빼둠

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 가계부 저장
    @PostMapping("/records") // TODO: api빼둠
    public ResponseEntity<?> saveRecord(@RequestBody _SaveTransactionRecord reqDTO) {
//        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return ResponseEntity.status(401).build();
//        }
        System.out.println(reqDTO);
        transactionService.saveTransaction(reqDTO, 1); // TODO: 세션유저 빼둠

        return ResponseEntity.ok(new ApiUtil<>(null));
    }

    // 가계부 수정
    @PutMapping("/records") // TODO: api빼둠
    public ResponseEntity<?> updateRecord() {
//        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return ResponseEntity.status(401).build();
//        }

        return ResponseEntity.ok(new ApiUtil<>(null));
    }

    // 가계부 삭제
    @DeleteMapping("/records") // TODO: api빼둠
    public ResponseEntity<?> deleteRecord() {
//        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return ResponseEntity.status(401).build();
//        }

        return ResponseEntity.ok(new ApiUtil<>(null));
    }

}
