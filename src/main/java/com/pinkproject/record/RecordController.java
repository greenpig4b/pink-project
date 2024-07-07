package com.pinkproject.record;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;
    private final HttpSession session;

    // 기록 일일 화면 랜더링
    @GetMapping("/records/monthly") // TODO: api빼둠
    public ResponseEntity<?> dailyRecords(@RequestParam Integer year, @RequestParam Integer month) {
//        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return ResponseEntity.status(401).build();
//        }
        _DailyMainDTORecord respDTO = recordService.getDailyMain(1, year, month); // TODO: 세션유저 빼둠

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }
}
