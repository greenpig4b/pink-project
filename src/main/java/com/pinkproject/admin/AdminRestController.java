package com.pinkproject.admin;

import com.pinkproject._core.error.exception.Exception401;
import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveNoticeAdminRecord;
import com.pinkproject.faq.FaqService;
import com.pinkproject.notice.Notice;
import com.pinkproject.notice.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminService adminService;
    private final NoticeService noticeService;
    private final FaqService faqService;
    private final HttpSession session;

    @PostMapping("/api/admin/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord(username, password);

        try {
            log.info("Attempting authentication for user: {}", username);
            boolean isAuthenticated = adminService.authenticate(loginAdminRecord);
            log.info("Authentication result: {}", isAuthenticated);

            if (isAuthenticated) {
                Admin admin = adminService.findByUsername(username);
                log.info("User found: {}", admin);
                SessionAdmin sessionAdmin = new SessionAdmin(admin);
                session.setAttribute("admin", sessionAdmin);

                // JWT 토큰 생성
                String jwt = JwtUtil.create(admin);
                log.info("Generated JWT: {}", jwt);

                // 응답 데이터 구성
                Map<String, Object> response = new HashMap<>();
                response.put("message", "로그인 성공");


                return new ResponseEntity<>(new ApiUtil<>(response), HttpStatus.OK);
            } else {
                throw new Exception401("유효하지 않은 인증입니다.");
            }
        } catch (Exception401 e) {
            log.error("Authentication error: {}", e.getMessage());
            ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            ApiUtil<String> errorResponse = new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/admin/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        log.info("Logging out user");
        session.invalidate();

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "로그아웃 성공");

        return new ResponseEntity<>(new ApiUtil<>(response), HttpStatus.OK);
    }

}
