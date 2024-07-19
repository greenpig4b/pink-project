package com.pinkproject.user;

import com.pinkproject._core.error.exception.Exception400;
import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse.*;
import com.pinkproject.user.enums.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public _JoinRespRecord saveUser(_JoinRecord reqRecord) {
        Optional<User> userOP = userRepository.findByEmail(reqRecord.email());
        if (userOP.isPresent()) {
            throw new Exception400("이미 가입된 이메일입니다.");
        }

        User user = new User(reqRecord);
        userRepository.saveAndFlush(user);

        return _JoinRespRecord.builder()
                .email(reqRecord.email())
                .password(reqRecord.password())
                .build();
    }

    public void validateAndCheckEmailDuplicate(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception400("이미 가입된 이메일입니다.");
        }
    }

    public _LoginRespRecord getUser(_LoginRecord reqRecord) {
        User user = userRepository.findByEmailAndPassword(reqRecord.email(), reqRecord.password());
        _LoginRespRecord.UserRecord userRecord = new _LoginRespRecord.UserRecord(user.getId(), user.getEmail(), user.getPassword());
        String jwt = JwtUtil.create(user);

        return new _LoginRespRecord(userRecord, jwt);
    }

    // 카카오 로그인
    @Transactional
    public String kakaoLogin(String kakaoAccessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<_KakaoUserRecord> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                _KakaoUserRecord.class);

        String email = response.getBody().properties().nickname() + "@kakao.com";
        User userPS = userRepository.findByEmail(email)
                .orElse(null);

        if (userPS != null) {
            return JwtUtil.create(userPS);
        } else {
            User user = User.builder()
                    .email(email)
                    .password(UUID.randomUUID().toString())
                    .oauthProvider(OauthProvider.KAKAO)
                    .build();

            User returnUser = userRepository.save(user);
            return JwtUtil.create(returnUser);
        }
    }

    public _UserRespRecord getUserInfo(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다."));
        return new _UserRespRecord(user.getId(), user.getEmail(), user.getPassword());
    }

    @Transactional
    public _UserUpdateRespRecord updateUserInfo(_UserUpdateRecord reqRecord, Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        user.update(reqRecord);
        userRepository.saveAndFlush(user);

        return new _UserUpdateRespRecord(user.getId(), user.getEmail(), user.getPassword());
    }
}
