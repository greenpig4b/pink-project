package com.pinkproject.record;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import com.pinkproject.user.SessionUser;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    public _DailyMainDTORecord getDailyMain(SessionUser sessionUser, Integer year, Integer month) {
        User user = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

// _DailyMainDTORecord에 담을 정보를 추린다.
// 1. 연/월이 필요하다. -> 컨트롤러에서 쿼리스트링으로 받아오자.
        String yearMonth = String.format("%04d-%02d", year, month);

// 2. 해당 월의 수입의 합, 지출의 합, 수입/지출의 합이 필요하다.

// DailyRecord에 담을 정보를 추린다.
// 3. 기록이 있는 날의 날짜가 필요하다.(기록이 없으면 날짜가 없어야 함)
// 4. 기록이 있는 날의 수입의 합, 지출의 합, 수입/지출의 합이 필요하다.

// DailyTransactionDetail에 담을 정보를 추린다.
// 5. 해당하는 날짜의 상세 내역이 필요하다.

        return null;
    }
}
