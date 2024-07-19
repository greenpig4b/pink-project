package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.faq.faqRequest._DetailFaqAdminRecord;
import com.pinkproject.faq.faqRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.SessionAdmin;
import com.pinkproject.admin.enums.FaqEnum;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
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

    @Test
    public void searchFaqs_test() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Faq> faqPage = new PageImpl<>(Arrays.asList(faq1, faq2));
        when(faqRepository.findByKeywordWithFaq("faq", pageRequest)).thenReturn(faqPage);

        Page<_DetailFaqAdminRecord> result = faqService.searchFaqs("faq",0);

        assertEquals(2, result.getTotalElements());
        assertEquals("faq1", result.getContent().get(0).title());
        assertEquals("faq2", result.getContent().get(1).title());
    }

    @Test
    public void getFaqById_test() {
        when(faqRepository.findById(1)).thenReturn(Optional.of(faq1));

        _DetailFaqAdminRecord result = faqService.getFaqById(1);

        assertNotNull(result);
        assertEquals("faq1", result.title());
    }

    @Test
    public void saveFaq_test() {
        _SaveFaqAdminRecord saveFaqAdminRecord = new _SaveFaqAdminRecord("faq1", "content1", "COMMON");
        when(faqRepository.save(any(Faq.class))).thenReturn(faq1);

        Integer result = faqService.saveFaq(saveFaqAdminRecord, admin);

        assertNotNull(result);
        assertEquals(faq1.getId(), result);
    }

    @Test
    @Transactional
    public void deleteFaq_test() {
        SessionAdmin sessionAdmin = new SessionAdmin(admin);
        when(session.getAttribute("admin")).thenReturn(sessionAdmin);
        when(adminRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(faqRepository.findById(faq1.getId())).thenReturn(Optional.of(faq1));

        Assertions.assertDoesNotThrow(() -> faqService.deleteFaq(faq1.getId()));

        Mockito.verify(faqRepository).delete(faq1);
    }
}
