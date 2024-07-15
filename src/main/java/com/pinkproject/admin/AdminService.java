package com.pinkproject.admin;

import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import com.pinkproject.notice.NoticeRepository;
import com.pinkproject.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeService noticeService;

    public boolean authenticate(_LoginAdminRecord loginAdminRecord) {
        Admin admin = adminRepository.findByUsername(loginAdminRecord.username());
        if (admin == null) {
            log.info("Authentication failed: user not found");
            return false;
        }
        boolean isAuthenticated = admin.getPassword().equals(loginAdminRecord.password());
        log.info("Authentication result for user {}: {}", loginAdminRecord.username(), isAuthenticated);
        return isAuthenticated;
    }


    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

}
