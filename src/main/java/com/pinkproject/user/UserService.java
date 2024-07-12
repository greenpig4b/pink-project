package com.pinkproject.user;

import com.pinkproject._core.error.exception.Exception400;
import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse._JoinRespRecord;
import com.pinkproject.user.UserResponse._LoginRespRecord;
import com.pinkproject.user.UserResponse._UserRespRecord;
import com.pinkproject.user.UserResponse._UserUpdateRespRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public _LoginRespRecord getUser(_LoginRecord reqRecord) {
        User user = userRepository.findByEmailAndPassword(reqRecord.email(), reqRecord.password());
        _LoginRespRecord.UserRecord userRecord = new _LoginRespRecord.UserRecord(user.getId(),user.getEmail(), user.getPassword());
        String jwt = JwtUtil.create(user);
        
        return new _LoginRespRecord(userRecord, jwt);
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

        return new _UserUpdateRespRecord(user.getId(), user.getPassword());
    }
}
