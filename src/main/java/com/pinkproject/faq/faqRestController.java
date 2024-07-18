package com.pinkproject.faq;


import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.AdminService;
import com.pinkproject.admin.SessionAdmin;
import com.pinkproject.notice.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
public class faqRestController {

    private final AdminService adminService;
    private final FaqService faqService;
    private final HttpSession session;

    @GetMapping("/api/admin/faq")
    public ResponseEntity<?> getAllFaqs() {
        List<_DetailFaqAdminRecord> faqs = faqService.getAllFaqs();

        Map<String, Object> response = new HashMap<>();
        response.put("faqs", faqs);

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

    @GetMapping("/api/admin/faq/{id}")
    public ResponseEntity<?> getFaqById(@PathVariable Integer id) {
        _DetailFaqAdminRecord faq = faqService.getFaqById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("faq", faq);

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

    @PostMapping("/api/admin/faq/save")
    public ResponseEntity<?> saveFaq(@RequestBody _SaveFaqAdminRecord request, HttpServletRequest httpRequest) {
        try {
            SessionAdmin sessionAdmin = (SessionAdmin) httpRequest.getSession().getAttribute("admin");
            if (sessionAdmin == null) {
                ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.UNAUTHORIZED.value(), "Admin session not found.");
                return new ResponseEntity<>(401, HttpStatus.UNAUTHORIZED);
            }

            Admin admin = adminService.findByUsername(sessionAdmin.getUsername());
            if (admin == null) {
                ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.NOT_FOUND.value(), "Admin not found.");
                return new ResponseEntity<>(404, HttpStatus.NOT_FOUND);
            }

            Integer faqId = faqService.saveFaq(request, admin);
            _DetailFaqAdminRecord faq = faqService.getFaqById(faqId);

            return new ResponseEntity<>(new ApiUtil<>(faq), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating FAQ: {}", e.getMessage());
            ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "FAQ를 작성하기를 실패하였습니다.");
            return new ResponseEntity<>(500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/api/admin/faq/delete/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable Integer id) {
        try {
            faqService.deleteFaq(id);
            return new ResponseEntity<>(new ApiUtil<>("FAQ 삭제 완료"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting FAQ: {}", e.getMessage());
            ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting FAQ");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
