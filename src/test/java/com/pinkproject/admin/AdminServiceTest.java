package com.pinkproject.admin;

import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import com.pinkproject.notice.NoticeRepository;
import com.pinkproject.notice.NoticeService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class AdminServiceTest {

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private HttpSession httpSession;

    @Autowired
    private AdminService adminService;

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
    }

    @Test
    void testAuthenticateUserNotFound_test() {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("unknown", "password");
        when(adminRepository.findByUsername("unknown")).thenReturn(null);

        // when
        boolean result = adminService.authenticate(loginAdminRecord);

        // then
        assertFalse(result);
        verify(adminRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void testAuthenticateSuccess_test() {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "password");
        when(adminRepository.findByUsername("admin")).thenReturn(admin);

        // when
        boolean result = adminService.authenticate(loginAdminRecord);

        // then
        assertTrue(result);
        verify(adminRepository, times(1)).findByUsername("admin");
    }

    @Test
    void testAuthenticateFailure_test() {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "wrongpassword");
        when(adminRepository.findByUsername("admin")).thenReturn(admin);

        // when
        boolean result = adminService.authenticate(loginAdminRecord);

        // then
        assertFalse(result);
        verify(adminRepository, times(1)).findByUsername("admin");
    }



    @Test
    void testFindByUsername_test() {
        // given
        String username = "admin";
        when(adminRepository.findByUsername(username)).thenReturn(admin);

        // when
        Admin foundAdmin = adminService.findByUsername(username);

        // then
        assertNotNull(foundAdmin);
        assertEquals("admin", foundAdmin.getUsername());
        verify(adminRepository, times(1)).findByUsername(username);
    }
}
