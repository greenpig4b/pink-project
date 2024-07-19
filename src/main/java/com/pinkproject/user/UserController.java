package com.pinkproject.user;

import com.pinkproject._core.error.exception.Exception400;
import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse._JoinRespRecord;
import com.pinkproject.user.UserResponse._LoginRespRecord;
import com.pinkproject.user.UserResponse._UserRespRecord;
import com.pinkproject.user.UserResponse._UserUpdateRespRecord;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HttpSession session;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody _JoinRecord reqRecord) {
        _JoinRespRecord respRecord = userService.saveUser(reqRecord);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, String>> checkEmail(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.validateAndCheckEmailDuplicate(email);
            response.put("msg", "사용 가능한 이메일입니다.");
            return ResponseEntity.ok(response);
        } catch (Exception400 e) {
            response.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody _LoginRecord reqRecord) {
        _LoginRespRecord respRecord = userService.getUser(reqRecord);
        SessionUser sessionUser = new SessionUser(respRecord.user().id(), respRecord.user().email(), null);
        session.setAttribute("sessionUser", sessionUser);

        return ResponseEntity.ok().header("Authorization", "Bearer " + respRecord.jwt()).body(new ApiUtil<>(respRecord.user()));
    }

    // 카카오 로그인
    @GetMapping("/oauth/callback/kakao")
    public ResponseEntity<?> kakaoOauthcallback(@RequestParam("accessToken") String kakaoAccessToken) {
        System.out.println("스프링에서 받은 카카오토큰: " + kakaoAccessToken);

        String pinkAccessToken = userService.kakaoLogin(kakaoAccessToken);

        return ResponseEntity.ok().header("Authorization", "Bearer "+pinkAccessToken).body(new ApiUtil<>(null));
    }

    // 네이버 로그인
    @GetMapping("/oauth/callback/naver")
    public ResponseEntity<?> naverOauthcallback(@RequestParam("accessToken") String naverAccessToken) {
        System.out.println("스프링에서 받은 카카오토큰: " + naverAccessToken);

        String pinkAccessToken = userService.naverLogin(naverAccessToken);

        return ResponseEntity.ok().header("Authorization", "Bearer "+pinkAccessToken).body(new ApiUtil<>(null));
    }

    // 회원 정보 조회
    @GetMapping("/api/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return ResponseEntity.status(401).body(new ApiUtil<>("세션유저없음"));
        }
        _UserRespRecord respRecord = userService.getUserInfo(sessionUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 회원 정보 업데이트
    @PutMapping("/api/users/{id}")
    public ResponseEntity<?> updateUserInfo(@RequestBody _UserUpdateRecord reqRecord, @PathVariable("id") Integer id) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return ResponseEntity.status(401).body(new ApiUtil<>("세션유저없음"));
        }

        if (!sessionUser.getId().equals(id)) {
            return ResponseEntity.status(403).body(new ApiUtil<>("수정 권한 없음"));
        }

        _UserUpdateRespRecord respRecord = userService.updateUserInfo(reqRecord, sessionUser.getId());
        session.setAttribute("sessionUser", new SessionUser(respRecord.id(), respRecord.email(), sessionUser.getCreatedAt()));

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<?> logout() { // 로그아웃
        session.invalidate();

        return ResponseEntity.ok(new ApiUtil<>("로그아웃 완료"));
    }
}
