package com.pinkproject.record;

import com.pinkproject._core.utils.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    // 기록 일일 화면 랜더링
    @GetMapping("daily/records")
    public ResponseEntity<?> dailyRecords() {

//        return ResponseEntity.ok(new ApiUtil<>(respDTO));
        return ResponseEntity.ok(new ApiUtil<>(null));
    }
}
