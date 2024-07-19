package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class FaqRepositoryTest {

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

        // 해당 관리자가 작성한 글인지
        assertEquals("admin", faqPage.getContent().get(0).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(1).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(2).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(3).getAdmin().getUsername());
        assertEquals("admin", faqPage.getContent().get(4).getAdmin().getUsername());
    }


    @Test
    public void findByKeywordWithFaq_test() {
        //given
        String keyword = "방법";
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<Faq> faqPage = faqRepository.findByKeywordWithFaq(keyword, pageable);

        //then
        List<Faq> faqList = faqPage.getContent();
        assertEquals(7, faqPage.getTotalElements()); // "방법"이 포함된 FAQ는 7개
        assertEquals("회원 탈퇴 방법", faqPage.getContent().get(0).getTitle());  // 최근 생성된 항목
        assertEquals("프로모션 코드 사용 방법", faqPage.getContent().get(1).getTitle());
        assertEquals("고객센터 이용 방법", faqPage.getContent().get(2).getTitle());
        assertEquals("이벤트 참여 방법", faqPage.getContent().get(3).getTitle());
        assertEquals("데이터 백업 방법", faqPage.getContent().get(4).getTitle());
        // 두 번째 페이지 요청
        pageable = PageRequest.of(1, 5);
        faqPage = faqRepository.findByKeywordWithFaq(keyword, pageable);

        // 두 번째 페이지 항목 검사
        faqList = faqPage.getContent();
        assertEquals(2, faqList.size());  // 두 번째 페이지에는 2개의 항목이 남아 있음
        assertEquals("가계부 작성 방법", faqList.get(0).getTitle());
        assertEquals("회원가입 방법", faqList.get(1).getTitle());
    }
}
