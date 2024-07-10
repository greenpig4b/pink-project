package com.pinkproject.admin;


import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private HttpSession session;


    @GetMapping("/login")
    public String loginForm() {
        return "admin/login-form";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody _LoginAdminRecord loginAdminRecord) {
        boolean isAuthenticated = adminService.authenticate(loginAdminRecord);
        if (isAuthenticated) {
            session.setAttribute("admin", loginAdminRecord.username());
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/admin/login?error=true";
        }
    }
}
