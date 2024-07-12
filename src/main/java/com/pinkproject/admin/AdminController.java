package com.pinkproject.admin;


import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.admin.AdminRequest.*;
import com.pinkproject.admin.AdminResponse._SaveFaqAdminRespRecord;
import com.pinkproject.faq.FaqService;
import com.pinkproject.notice.Notice;
import com.pinkproject.notice.NoticeRepository;
import com.pinkproject.notice.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequiredArgsConstructor
@Controller

public class AdminController {
    private final AdminService adminService;
    private final NoticeService noticeService;
    private final FaqService faqService;
    private final HttpSession session;



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
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
        }
        return "admin/notice";
    }

    @GetMapping("/admin/faq")
    public String faq(HttpServletRequest request) {
        List<_DetailFaqAdminRecord> faqs = faqService.getFaqs();
        faqs.forEach(f -> System.out.println(f.title() + " - " + f.content() + " - " + f.username()));
        request.setAttribute("faqs", faqs);
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
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
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
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
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
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


    @GetMapping("/admin/faq/save")
    public String saveFaqForm(HttpServletRequest request) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
        }
        return "admin/faq-save";
    }

    @PostMapping("/admin/faq/save")
    public String saveFaq(@ModelAttribute _SaveFaqAdminRecord saveFaqAdminRecord, HttpServletRequest request) {
        try {
            SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
            Admin admin = adminService.findByUsername(sessionAdmin.getUsername());
            faqService.saveFaq(saveFaqAdminRecord, admin);
            return "redirect:/admin/faq";
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            return "admin/faq-save";
        }
    }
    @GetMapping("/admin/notice/save")
    public String saveNoticeForm(HttpServletRequest request) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
        }
        return "admin/notice-save";
    }

    @PostMapping("/admin/notice/save")
    public String saveNotice(@ModelAttribute _SaveNoticeAdminRecord saveNoticeAdminRecord, HttpServletRequest request) {
        try {
            SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
            Admin admin = adminService.findByUsername(sessionAdmin.getUsername());
            noticeService.saveNotice(saveNoticeAdminRecord, admin);
            return "redirect:/admin/notice";
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            return "admin/notice-save";
        }
    }

}
