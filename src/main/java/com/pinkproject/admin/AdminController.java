package com.pinkproject.admin;


import com.pinkproject.admin.AdminRequest.*;
import com.pinkproject.faq.FaqService;
import com.pinkproject.notice.NoticeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller

public class AdminController {
    private final AdminService adminService;
    private final HttpSession session;



    @GetMapping("/admin")
    public String loginForm() {
        return "admin/login-form";
    }

    @PostMapping("/admin/login")
    public String login(@RequestBody _LoginAdminRecord loginAdminRecord, HttpSession session) {
        boolean isAuthenticated = adminService.authenticate(loginAdminRecord);
        if (isAuthenticated) {
            Admin admin = adminService.findByUsername(loginAdminRecord.username());
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
