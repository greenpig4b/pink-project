package com.pinkproject.memo;

import com.pinkproject.transaction.TransactionRepository;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import com.pinkproject.user.enums.OauthProvider;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
public class MemoRepositoryTest {

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User user;


    @BeforeEach
    void setUp() {
        //데이터 초기화 하기 - 테스트 전 초기화 설정
        memoRepository.deleteAll();
        userRepository.deleteAll();
        transactionRepository.deleteAll();


        // 테스트 사용자 생성 및 저장
        user = User.builder()
                .email("testuser@example.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build();
        userRepository.save(user);

        // 테스트 메모 데이터 생성 및 저장
        Memo memo1 = Memo.builder()
                .user(user)
                .title("Memo 1")
                .content("Content 1")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        Memo memo2 = Memo.builder()
                .user(user)
                .title("Memo 2")
                .content("Content 2")
                .createdAt(LocalDateTime.now())
                .build();
        memoRepository.save(memo1);
        memoRepository.save(memo2);
    }


    @Test
    void findByUserIdAndCreatedAtBetween_test() {
        // given
        int userId = user.getId();
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        // when
        List<Memo> memos = memoRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);

        // then
        assertNotNull(memos);
        assertThat(memos).hasSize(2);

        // 내림차순 정렬 확인
        memos.sort(Comparator.comparing(Memo::getCreatedAt).reversed());
        assertThat(memos.get(0).getTitle()).isEqualTo("Memo 2");
        assertThat(memos.get(1).getTitle()).isEqualTo("Memo 1");
    }
}
