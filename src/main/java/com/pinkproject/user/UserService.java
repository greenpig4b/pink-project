package com.pinkproject.user;

import com.pinkproject._core.error.exception.Exception400;
import com.pinkproject.user.UserRequest.JoinRecord;
import com.pinkproject.user.UserResponse.JoinRespRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public JoinRespRecord saveUser(JoinRecord reqRecord) {
        Optional<User> userOP = userRepository.findByEmail(reqRecord.email());
        if (userOP.isPresent()) {
            throw new Exception400("이미 가입된 이메일입니다.");
        }

        User user = new User(reqRecord);
        userRepository.saveAndFlush(user);

        return JoinRespRecord.builder()
                .email(reqRecord.email())
                .password(reqRecord.password())
                .build();
    }
}
