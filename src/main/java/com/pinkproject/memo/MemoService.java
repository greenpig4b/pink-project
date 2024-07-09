package com.pinkproject.memo;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject.memo.MemoRequest._SaveMemoRecord;
import com.pinkproject.memo.MemoRequest._UpdateMemoRecord;
import com.pinkproject.memo.MemoResponse._MonthlyMemoMainRecord;
import com.pinkproject.memo.MemoResponse._SaveMemoRespRecord;
import com.pinkproject.memo.MemoResponse._UpdateMemoRespRecord;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    public _MonthlyMemoMainRecord getMonthlyMemoMain(Integer sessionUserId, Integer year, Integer month) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보가 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Memo> memos = memoRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDateTime, endDateTime);

        Map<String, List<Memo>> memosByDate = memos.stream()
                .collect(Collectors.groupingBy(memo -> memo.getCreatedAt().toLocalDate().toString()));

        List<_MonthlyMemoMainRecord.DailyMemoRecords> dailyMemoRecordsList = memosByDate.entrySet().stream()
                .map(entry -> {
                    String date = entry.getKey();
                    List<Memo> dailyMemoList = entry.getValue();

                    List<_MonthlyMemoMainRecord.DailyMemoRecords.DailyMemoRecord> dailyMemoRecordList = dailyMemoList.stream()
                            .map(memo -> new _MonthlyMemoMainRecord.DailyMemoRecords.DailyMemoRecord(
                                    memo.getId(),
                                    memo.getTitle(),
                                    memo.getContent()))
                            .toList();

                    return new _MonthlyMemoMainRecord.DailyMemoRecords(date, dailyMemoRecordList);
                }).toList();

        return new _MonthlyMemoMainRecord(sessionUserId, dailyMemoRecordsList);
    }

    @Transactional
    public _SaveMemoRespRecord saveMemo(_SaveMemoRecord reqRecord, Integer sessionUserId) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Memo memo = Memo.builder()
                .user(user)
                .title(reqRecord.title())
                .content(reqRecord.content())
                .build();

        memo = memoRepository.save(memo);

        return new _SaveMemoRespRecord(
                memo.getId(),
                memo.getUser().getId(),
                memo.getTitle(),
                memo.getContent()
        );
    }

    // 메모 업데이트
    @Transactional
    public _UpdateMemoRespRecord updateMemo(Integer memoId, _UpdateMemoRecord reqRecord) {
        User user = userRepository.findById(reqRecord.userId()).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new Exception404("메모 정보를 찾을 수 없습니다."));

        if (!memo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("사용자 권한이 없습니다.");
        }

        memo.setId(reqRecord.id());
        memo.setUser(user);
        memo.setTitle(reqRecord.title());
        memo.setContent(reqRecord.content());

        memo = memoRepository.saveAndFlush(memo);

        return new _UpdateMemoRespRecord(
                memo.getId(),
                memo.getUser().getId(),
                memo.getTitle(),
                memo.getContent()
        );
    }

    @Transactional
    public void deleteMemo(Integer memoId, Integer sessionUserId) {
        User user = userRepository.findById(sessionUserId).orElseThrow(() -> new Exception404("유저 정보를 찾을 수 없습니다."));

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new Exception404("메모 정보를 찾을 수 없습니다."));

        if (!memo.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("사용자 권한이 없습니다.");
        }

        memoRepository.delete(memo);
    }
}