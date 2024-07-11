package com.pinkproject.admin;


import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import com.pinkproject.faq.FaqService;
import com.pinkproject.notice.Notice;
import com.pinkproject.notice.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequiredArgsConstructor
@Controller

public class AdminController {
    private final AdminService adminService;
    private final NoticeService noticeService;
    private final FaqService faqService;
    private final HttpSession session;
    private final View error;


    @GetMapping("/admin")
    public String loginForm() {
        return "admin/login-form";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord(username, password);
        boolean isAuthenticated = adminService.authenticate(loginAdminRecord);
        if (isAuthenticated) {
            Admin admin = adminService.findByUsername(username);
            SessionAdmin sessionAdmin = new SessionAdmin(admin);
            session.setAttribute("admin", sessionAdmin);
            return "redirect:/admin/notice";
        } else {
            return "redirect:/admin?error=true";
        }
    }

    @GetMapping("/admin/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/admin";
    }

    @GetMapping("/admin/notice")
    public String notice(HttpServletRequest request) {
        List<_DetailNoticeAdminRecord> notices = noticeService.getNotices();
        request.setAttribute("notices", notices);
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
        }
        return "admin/notice";
    }

    @GetMapping("/admin/faq")
    public String faq(HttpServletRequest request) {
        List<_DetailFaqAdminRecord> faqs = faqService.getFaqs();
        request.setAttribute("faqs", faqs);
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
        }
        return "admin/faq";
    }

    @GetMapping("/admin/faq/detail/{id}")
    public String faqDetail(@PathVariable Integer id, HttpServletRequest request) {
        _DetailFaqAdminRecord faqDetail = faqService.detailFaqAdminRecord(id);
        HttpSession session = request.getSession();
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
        }
        request.setAttribute("faq", faqDetail);
        request.setAttribute("title", faqDetail.title());
        request.setAttribute("content", faqDetail.content());
        request.setAttribute("date", faqDetail.date());

        return "admin/faq-detail";
    }

    @GetMapping("/admin/notice/detail/{id}")
    public String noticeDetail(@PathVariable Integer id, HttpServletRequest request) {
        _DetailNoticeAdminRecord noticeDetail = noticeService.detailNoticeAdminRecord(id);
        HttpSession session = request.getSession(); // 세션 객체를 얻음
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
        }
        request.setAttribute("notice", noticeDetail);
        request.setAttribute("title", noticeDetail.title());
        request.setAttribute("content", noticeDetail.content());
        request.setAttribute("date", noticeDetail.date());
        return "admin/notice-detail";
    }

    @DeleteMapping("/admin/faq/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteFaq(@PathVariable Integer id) {
        try {
            faqService.deleteFaq(id);
            return ResponseEntity.ok(new ApiUtil<>(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/admin/notice/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteNotice(@PathVariable Integer id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(new ApiUtil<>(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
