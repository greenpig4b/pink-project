package com.pinkproject.user;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse._JoinRespRecord;
import com.pinkproject.user.UserResponse._LoginRespRecord;
import com.pinkproject.user.UserResponse._UserRespRecord;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HttpSession session;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody _JoinRecord reqRecord) {
        System.out.println("요청" + reqRecord);
        _JoinRespRecord respRecord = userService.saveUser(reqRecord);
        System.out.println("응답" + respRecord);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody _LoginRecord reqRecord) {
        _LoginRespRecord respRecord = userService.getUser(reqRecord);

        return ResponseEntity.ok().header("Authorization", "Bearer " + respRecord.jwt()).body(new ApiUtil<>(respRecord.user()));
    }

    // 회원 정보 조회
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        _UserRespRecord respRecord = userService.getUserInfo(id);

        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }

    // 회원 정보 업데이트
    @PutMapping("/users/{id}") // TODO: API 매핑 필요
    public ResponseEntity<?> updateUserInfo(@RequestBody _UserUpdateRecord reqRecord, @PathVariable("id") Integer id) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
        SessionUser newSessionUser = userService.updateUserInfo(reqRecord, sessionUser.getId());
        session.setAttribute("sessionUser", newSessionUser);

        return ResponseEntity.ok(new ApiUtil<>(newSessionUser));
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<?> logout() { // 로그아웃
        session.invalidate();

        return ResponseEntity.ok(new ApiUtil<>("로그아웃 완료"));
    }
}
