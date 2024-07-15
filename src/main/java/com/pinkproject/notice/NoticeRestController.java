package com.pinkproject.notice;


import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveNoticeAdminRecord;
import com.pinkproject.admin.AdminService;
import com.pinkproject.admin.SessionAdmin;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class NoticeRestController {
    
    private final NoticeService noticeService;
    private final HttpSession session;
    private final AdminRepository adminRepository;
    private final NoticeRepository noticeRepository;

    @GetMapping("/api/admin/notice")
    public ResponseEntity<?> getAllNotices() {
        List<_DetailNoticeAdminRecord> notices = noticeService.getAllNotices();

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);

        // 세션에서 admin 객체 가져와서 username 설정
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            response.put("username", sessionAdmin.getUsername());
        } else {
            response.put("username", ""); // 기본값 설정
        }

        // currentDateTime 설정
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedNow = now.format(formatter);
        response.put("currentDateTime", formattedNow);

        return new ResponseEntity<>(new ApiUtil<>(response), HttpStatus.OK);
    }

    @GetMapping("/api/admin/notice/{id}")
    public ResponseEntity<?> getNoticeById(@PathVariable Integer id) {
        _DetailNoticeAdminRecord notice = noticeService.getNoticeById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("notice", notice);

        // 세션에서 admin 객체 가져와서 username 설정
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            response.put("username", sessionAdmin.getUsername());
        } else {
            response.put("username", ""); // 기본값 설정
        }

        // currentDateTime 설정
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedNow = now.format(formatter);
        response.put("currentDateTime", formattedNow);

        return new ResponseEntity<>(new ApiUtil<>(response), HttpStatus.OK);
    }

    @PostMapping("/api/admin/notice/save")
    public ResponseEntity<?> createNotice(@RequestBody _SaveNoticeAdminRecord request) {
        try {
            Notice notice = noticeService.createNotice(request);
            return new ResponseEntity<>(new ApiUtil<>(notice), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating notice: {}", e.getMessage());
            ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating notice");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/api/admin/notice/delete/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Integer id) {
        try {
            noticeService.deleteNotice(id);
            return new ResponseEntity<>(new ApiUtil<>("공지사항 삭제 완료 되었습니다"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting notice: {}", e.getMessage());
            ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting notice");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
