package com.pinkproject.notice;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeService noticeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    public void getAllNotices_test() {
        Admin admin = Admin.builder()
                .id(1)
                .username("admin")
                .password("password") // 패스워드 값을 설정합니다.
                .build();
        admin = entityManager.merge(admin);

        Notice notice1 = Notice.builder()
                .id(1)
                .title("notice1")
                .content("content1")
                .admin(admin)
                .createdAt(LocalDateTime.now())
                .build();

        Notice notice2 = Notice.builder()
                .id(2)
                .title("notice2")
                .content("content2")
                .admin(admin)
                .createdAt(LocalDateTime.now())
                .build();

        List<Notice> notices = Arrays.asList(notice1, notice2);
        when(noticeRepository.findAll()).thenReturn(notices);

        // Mock된 데이터 확인
        System.out.println("Mock된 Notice 리스트: " + notices);

        // 엔티티 매니저를 사용하여 엔티티 초기화
        notice1 = entityManager.merge(notice1);
        notice2 = entityManager.merge(notice2);
        entityManager.flush();

        List<_DetailNoticeAdminRecord> result = noticeService.getAllNotices();

        // 변환된 데이터 확인
        System.out.println("결과 리스트: " + result);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("notice1", result.get(0).title());
        Assertions.assertEquals("notice2", result.get(1).title());
        Assertions.assertEquals("content1", result.get(0).content());
        Assertions.assertEquals("content2", result.get(1).content());
        Assertions.assertEquals("admin", result.get(0).username());
        Assertions.assertEquals("admin", result.get(1).username());
    }
}
