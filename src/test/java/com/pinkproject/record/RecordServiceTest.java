package com.pinkproject.record;

import com.pinkproject.record.RecordResponse.DailyRecordsDTO._DailyMainDTORecord;
import com.pinkproject.user.SessionUser;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class RecordServiceTest {
    @InjectMocks
    private RecordService recordService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RecordRepository recordRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDailyMain_test() {
        // given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);

        User user = new User();
        user.setId(1);

        when(userRepository.findById(sessionUser.getId())).thenReturn(Optional.of(user));
        when(recordRepository.findByUserIdAndCreatedAtBetween(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // when
        _DailyMainDTORecord result = recordService.getDailyMain(sessionUser, 2024, 5);

        // then
        System.out.println("getDailyMain_test: " + result);
    }
}
