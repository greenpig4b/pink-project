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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

}
