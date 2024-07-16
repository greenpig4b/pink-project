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
    public void findAllWithAdmin_test(){

        //given
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
        adminRepository.save(admin);


        //when
        Faq faq1 = new Faq();
        faq1.setTitle("자주 찾는 질문 1");
        faq1.setContent("자주 찾는 질문 1의 내용");
        faq1.setAdmin(admin);
        faqRepository.save(faq1);

        Faq faq2 = new Faq();
        faq2.setTitle("자주 찾는 질문 2");
        faq2.setContent("자주 찾는 질문 2의 내용");
        faq2.setAdmin(admin);
        faqRepository.save(faq2);

        Faq faq3 = new Faq();
        faq3.setTitle("자주 찾는 질문 3");
        faq3.setContent("자주 찾는 질문 3의 내용");
        faq3.setAdmin(admin);
        faqRepository.save(faq3);

        Faq faq4 = new Faq();
        faq4.setTitle("자주 찾는 질문 4");
        faq4.setContent("자주 찾는 질문 4의 내용");
        faq4.setAdmin(admin);
        faqRepository.save(faq4);

        Faq faq5 = new Faq();
        faq5.setTitle("자주 찾는 질문 5");
        faq5.setContent("자주 찾는 질문 5의 내용");
        faq5.setAdmin(admin);
        faqRepository.save(faq5);



        Pageable pageable = PageRequest.of(0, 5);
        Page<Faq> faqPage = faqRepository.findAllWithAdmin(pageable);

        //then
        assertNotNull(faqPage);
        assertEquals(5, faqPage.getTotalElements());
        assertEquals(5, faqPage.getNumberOfElements());
        assertEquals("자주 찾는 질문 1", faqPage.getContent().get(0).getTitle());
        assertEquals("자주 찾는 질문 2", faqPage.getContent().get(1).getTitle());
        assertEquals("자주 찾는 질문 3", faqPage.getContent().get(2).getTitle());
        assertEquals("자주 찾는 질문 4", faqPage.getContent().get(3).getTitle());
        assertEquals("자주 찾는 질문 5", faqPage.getContent().get(4).getTitle());
        assertEquals(admin, faqPage.getContent().get(0).getAdmin().getUsername());
        assertEquals(admin, faqPage.getContent().get(1).getAdmin().getUsername());
        assertEquals(admin, faqPage.getContent().get(2).getAdmin().getUsername());
        assertEquals(admin, faqPage.getContent().get(3).getAdmin().getUsername());
        assertEquals(admin, faqPage.getContent().get(4).getAdmin().getUsername());

    }


}
