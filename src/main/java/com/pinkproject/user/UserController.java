package com.pinkproject.user;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.user.UserRequest.JoinRecord;
import com.pinkproject.user.UserResponse.JoinRespRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRecord reqRecord) {
        System.out.println("요청" + reqRecord);
        JoinRespRecord respRecord = userService.saveUser(reqRecord);
        System.out.println("응답" + respRecord);
        return ResponseEntity.ok(new ApiUtil<>(respRecord));
    }
    // 로그인

    // 로그아웃
}
