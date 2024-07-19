package com.pinkproject.user;

import com.pinkproject.user.enums.OauthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_test() {

        //given
        User user = User.builder()
                .email("ssar1@kakao.com")
                .password("12345")
                .oauthProvider(OauthProvider.KAKAO)
                .build();
        userRepository.save(user);
        // 임의의 아이디를 DB에 저장

        //when
        Optional<User> findUser = userRepository.findByEmail("ssar1@kakao.com");
        // DB에 해당 아이디를 가져옴

        //then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getEmail()).isEqualTo("ssar1@kakao.com");
        // 확인
    }
}
