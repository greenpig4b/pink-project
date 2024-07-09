package com.pinkproject.memo;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.memo.MemoRequest._SaveMemoRecord;
import com.pinkproject.memo.MemoResponse._MonthlyMemoMainRecord;
import com.pinkproject.memo.MemoResponse._SaveMemoRespRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;

    // 월간 메모 랜더링
    @GetMapping("/memos/monthly")
    public ResponseEntity<?> monthlyMemos(@RequestParam Integer year, @RequestParam Integer month) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }
        _MonthlyMemoMainRecord respDTO = memoService.getMonthlyMemoMain(1, year, month); // TODO: 세션유저 빼둠

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    // 메모 저장
    @PostMapping("/memos") // TODO: API 경로 설정
    public ResponseEntity<?> saveTransaction(@RequestBody _SaveMemoRecord reqRecord) {
        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //     return ResponseEntity.status(401).build();
        // }
        System.out.println(reqRecord);
        _SaveMemoRespRecord respDTO = memoService.saveMemo(reqRecord, 1); // TODO: 세션유저 빼둠
        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }
//
//    // 메모 수정
//    @PutMapping("/memos/{id}") // TODO: API 경로 설정
//    public ResponseEntity<?> updateMemo(@PathVariable("id") Integer id, @RequestBody _UpdateMemoRecord reqRecord) {
//        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        // if (sessionUser == null) {
//        //     return ResponseEntity.status(401).build();
//        // }
//
//        System.out.println(reqRecord);
//        _UpdateMemoRespRecord response = memoService.updateMemo(id, reqRecord);
//        return ResponseEntity.ok(new ApiUtil<>(response));
//    }
//
//    // 메모 삭제
//    @DeleteMapping("/memos/{id}") // TODO: api빼둠
//    public ResponseEntity<?> deleteMemo(@PathVariable("id") Integer id) {
//        // SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        // if (sessionUser == null) {
//        //     return ResponseEntity.status(401).build();
//        // }
//        memoService.deleteMemo(id, 1); // TODO: 세션유저 빼둠
//        return ResponseEntity.ok(new ApiUtil<>(null));
//    }
}
