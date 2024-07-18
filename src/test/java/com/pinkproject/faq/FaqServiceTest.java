package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import com.pinkproject.admin.enums.FaqEnum;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class FaqServiceTest {

    @MockBean
    private FaqRepository faqRepository;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private HttpSession session;

    @Autowired
    private FaqService faqService;

    private Admin admin;
    private Faq faq1;
    private Faq faq2;

    @BeforeEach
    public void setUp() {
        admin = Admin.builder()
                .id(1)
                .username("admin")
                .password("password")
                .build();

        faq1 = Faq.builder()
                .id(1)
                .title("faq1")
                .content("content1")
                .admin(admin)
                .classification(FaqEnum.COMMON)
                .createdAt(LocalDateTime.now())
                .build();

        faq2 = Faq.builder()
                .id(2)
                .title("faq2")
                .content("content2")
                .admin(admin)
                .classification(FaqEnum.COMMON)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void getFaq_test() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Faq> faqPage = new PageImpl<>(Arrays.asList(faq1, faq2));
        when(faqRepository.findAllWithAdmin(pageRequest)).thenReturn(faqPage);

        Page<_DetailFaqAdminRecord> result = faqService.getFaqs(0);

        assertEquals(2, result.getTotalElements());
        assertEquals("faq1", result.getContent().get(0).title());
        assertEquals("faq2", result.getContent().get(1).title());
    }

    @Test
    public void getAllFaqs_test() {
        when(faqRepository.findAll()).thenReturn(Arrays.asList(faq1,faq2));

        List<_DetailFaqAdminRecord> result = faqService.getAllFaqs();

        assertEquals(2, result.size());
        assertEquals("faq1", result.get(0).title());
        assertEquals("faq2", result.get(1).title());
    }
}
