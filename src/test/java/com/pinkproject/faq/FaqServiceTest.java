package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FaqServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private FaqService faqService;

    private Admin admin;
    private Faq faq1;
    private Faq faq2;



}
