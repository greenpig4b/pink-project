package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class FaqRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FaqRepository faqRepository;


    @Test
    public void findAllWithAdmin_test() {



        // 페이징 설정
        Pageable pageable = PageRequest.of(0, 5);
        Page<Faq> faqPage = faqRepository.findAllWithAdmin(pageable);

        // then
        assertNotNull(faqPage);
        assertEquals(5, faqPage.getNumberOfElements()); // 페이지당 5개의 항목
        assertEquals("서비스 이용약관", faqPage.getContent().get(0).getTitle());
        assertEquals("다중 계정 사용", faqPage.getContent().get(1).getTitle());
        assertEquals("계정 잠금 해제", faqPage.getContent().get(2).getTitle());
        assertEquals("회원 탈퇴 방법", faqPage.getContent().get(3).getTitle());
        assertEquals("프로모션 코드 사용 방법", faqPage.getContent().get(4).getTitle());
//        assertEquals("비밀번호 변경", faqPage.getContent().get(4).getTitle());
        assertEquals("admin", faqPage.getContent().get(0).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(1).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(2).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(3).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(4).getAdmin().getUsername());
    }


    @Test
    public void findByKeywordWithFaq_test() {

        admin = adminRepository.findByUsername("admin");
        //given
        String keyword = "방법";
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<Faq> faqPage = faqRepository.findByKeywordWithFaq(keyword, pageable);

        //then
        assertNotNull(faqPage);
        assertEquals(7, faqPage.getTotalElements()); // "방법"이 포함된 FAQ는 3개
        assertEquals("회원 탈퇴 방법", faqPage.getContent().get(0).getTitle());  // 최근 생성된 항목
        assertEquals("데이터 백업 방법", faqPage.getContent().get(1).getTitle());
        assertEquals("가계부 작성 방법", faqPage.getContent().get(2).getTitle());
    }
}
