package com.pinkproject.record;

import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import com.pinkproject.user.SessionUser;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {
    @InjectMocks
    private RecordService recordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecordRepository recordRepository;

    @Test
    void getDailyMain_test() {
        // TODO: 현정!! 테스트 다시!!!
        // given
        User user = User.builder()
                .id(1)
                .email("ssar@kakao.com")
                .createdAt(LocalDateTime.of(2024, 1, 1, 9, 0))
                .oauthProvider("KAKAO")
                .build();

        SessionUser sessionUser = new SessionUser(user);

        when(userRepository.findById(sessionUser.getId())).thenReturn(Optional.of(user));
        when(recordRepository.findByUserIdAndCreatedAtBetween(any(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // when
        _DailyMainDTORecord result = recordService.getDailyMain(sessionUser, 2024, 5);

        // then
        System.out.println("getDailyMain_test: " + result);
    }
}
