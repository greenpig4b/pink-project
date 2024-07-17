package com.pinkproject.notice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Test
    public void findAllWithAdmin_test() {

        Pageable pageable = PageRequest.of(0, 5); // 한 페이지에 총 5개의 항목이 존재함
        Page<Notice> noticePage = noticeRepository.findAllWithAdmin(pageable);

        //해당 게시글의 목록 보기 테스트 (내림차순)
        assertNotNull(noticePage);
        assertEquals(5, noticePage.getNumberOfElements()); // 5개의 공지사항
        assertEquals("서비스 이용약관 변경", noticePage.getContent().get(0).getTitle());
        assertEquals("여름맞이 업데이트", noticePage.getContent().get(1).getTitle());
        assertEquals("데이터 정기 백업 안내", noticePage.getContent().get(2).getTitle());
        assertEquals("고객센터 운영 시간 변경", noticePage.getContent().get(3).getTitle());
        assertEquals("모바일 앱 출시", noticePage.getContent().get(4).getTitle());

        //해당 admin이 작성한 공지사항 글인지
        assertEquals("admin", noticePage.getContent().get(0).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(1).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(2).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(3).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(4).getAdmin().getUsername());
    }

    @Test
    public void findByKeywordWithNotice_test() {
        String keyword = "안내";
        Pageable pageable = PageRequest.of(0, 5);

        Page<Notice> noticePage = noticeRepository.findByKeywordWithNotice(keyword, pageable);

        assertEquals(4, noticePage.getNumberOfElements()); // 3개의 공지사항
        assertEquals("데이터 정기 백업 안내", noticePage.getContent().get(0).getTitle());
        assertEquals("이벤트 안내", noticePage.getContent().get(1).getTitle());
        assertEquals("점검 안내", noticePage.getContent().get(2).getTitle());
        assertEquals("업데이트 안내", noticePage.getContent().get(3).getTitle());

        //admin 엔티티 초기화
        assertEquals("admin", noticePage.getContent().get(0).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(1).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(2).getAdmin().getUsername());
        assertEquals("admin", noticePage.getContent().get(3).getAdmin().getUsername());
    }
}
