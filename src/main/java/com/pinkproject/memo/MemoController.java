package com.pinkproject.memo;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.memo.MemoRequest._SaveMemoRecord;
import com.pinkproject.memo.MemoRequest._UpdateMemoRecord;
import com.pinkproject.memo.MemoResponse._MonthlyMemoMainRecord;
import com.pinkproject.memo.MemoResponse._SaveMemoRespRecord;
import com.pinkproject.memo.MemoResponse._UpdateMemoRespRecord;
import com.pinkproject.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;
    private final HttpSession session;

    // 월간 메모 랜더링
    @GetMapping("/api/memos/monthly")
    public ResponseEntity<?> monthlyMemos(@RequestParam Integer year, @RequestParam Integer month) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _MonthlyMemoMainRecord respDTO = memoService.getMonthlyMemoMain(sessionUser.getId(), year, month);

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 메모 저장
    @PostMapping("/api/memos")
    public ResponseEntity<?> saveTransaction(@RequestBody _SaveMemoRecord reqRecord) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _SaveMemoRespRecord respRecord = memoService.saveMemo(reqRecord, sessionUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 메모 수정
    @PutMapping("/api/memos/{memoId}")
    public ResponseEntity<?> updateMemo(@PathVariable("memoId") Integer memoId, @RequestBody _UpdateMemoRecord reqRecord) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        _UpdateMemoRespRecord response = memoService.updateMemo(memoId, reqRecord);

        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 메모 삭제
    @DeleteMapping("/api/memos/{memoId}")
    public ResponseEntity<?> deleteMemo(@PathVariable("memoId") Integer memoId) {
         SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
         if (sessionUser == null) {
             return ResponseEntity.status(401).build();
         }
        memoService.deleteMemo(memoId, sessionUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(null));
    }
}
