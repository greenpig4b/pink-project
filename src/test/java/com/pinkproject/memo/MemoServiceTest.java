package com.pinkproject.memo;

import com.pinkproject.memo.MemoRequest._SaveMemoRecord;
import com.pinkproject.memo.MemoRequest._UpdateMemoRecord;
import com.pinkproject.memo.MemoResponse._MonthlyMemoMainRecord;
import com.pinkproject.memo.MemoResponse._SaveMemoRespRecord;
import com.pinkproject.memo.MemoResponse._UpdateMemoRespRecord;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemoServiceTest {

    @Autowired
    private MemoService memoService;

    @MockBean
    private MemoRepository memoRepository;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;
    private Memo mockMemo;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();

        mockMemo = Memo.builder()
                .id(1)
                .user(mockUser)
                .title("Test Title")
                .content("Test Content")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getMonthlyMemoMain_test() {
        // given
        int year = 2024;
        int month = 7;
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(memoRepository.findByUserIdAndCreatedAtBetween(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(mockMemo));

        // when
        _MonthlyMemoMainRecord response = memoService.getMonthlyMemoMain(mockUser.getId(), year, month);

        // then
        assertThat(response.userId()).isEqualTo(mockUser.getId());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(memoRepository, times(1))
                .findByUserIdAndCreatedAtBetween(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void saveMemo_test() {
        // given
        _SaveMemoRecord reqRecord = new _SaveMemoRecord(
                mockUser.getId(),
                "2024-07-19",
                "Test Title",
                "Test Content"
        );
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(memoRepository.save(any(Memo.class))).thenReturn(mockMemo);

        // when
        _SaveMemoRespRecord response = memoService.saveMemo(reqRecord, mockUser.getId());

        // then
        assertThat(response.title()).isEqualTo(reqRecord.title());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void updateMemo_test() {
        // given
        _UpdateMemoRecord reqRecord = new _UpdateMemoRecord(1, mockUser.getId(), "Updated Title", "Updated Content");
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(memoRepository.findById(mockMemo.getId())).thenReturn(Optional.of(mockMemo));
        when(memoRepository.saveAndFlush(any(Memo.class))).thenReturn(mockMemo);

        // when
        _UpdateMemoRespRecord response = memoService.updateMemo(mockMemo.getId(), reqRecord);

        // then
        assertThat(response.title()).isEqualTo(reqRecord.title());
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(memoRepository, times(1)).findById(mockMemo.getId());
        verify(memoRepository, times(1)).saveAndFlush(any(Memo.class));
    }

    @Test
    void deleteMemo_test() {
        // given
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(memoRepository.findById(mockMemo.getId())).thenReturn(Optional.of(mockMemo));

        // when
        memoService.deleteMemo(mockMemo.getId(), mockUser.getId());

        // then
        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(memoRepository, times(1)).findById(mockMemo.getId());
        verify(memoRepository, times(1)).delete(any(Memo.class));
    }
}
