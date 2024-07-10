package com.pinkproject.admin;

import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import com.pinkproject.notice.Notice;
import com.pinkproject.notice.NoticeRepository;
import com.pinkproject.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeService noticeService;

    public boolean authenticate(_LoginAdminRecord loginAdminRecord) {
        Admin admin = adminRepository.findByUsername(loginAdminRecord.username());
        return admin != null && admin.getPassword().equals(loginAdminRecord.password());
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public List<Notice> getNotices() {
        return noticeService.getNotices();
    }
}
