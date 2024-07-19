package com.pinkproject.user;

import com.pinkproject._core.error.exception.Exception400;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse._KakaoUserRecord;
import com.pinkproject.user.UserResponse._UserUpdateRespRecord;
import com.pinkproject.user.enums.OauthProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @InjectMocks
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeAll
    static void init() {
        // JWT 비밀 키 설정
        JwtUtil.SECRET_KEY = "test-secret";
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        ReflectionTestUtils.setField(userService, "restTemplate", restTemplate);
    }
    @Test
    void saveUser_test() {
        // given
        _JoinRecord joinRecord = new _JoinRecord("test@example.com", "password");
        User user = new User(joinRecord);

        // Mock 설정
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        // when
        var response = userService.saveUser(joinRecord);

        // then
        assertThat(response.email()).isEqualTo(joinRecord.email());
        assertThat(response.password()).isEqualTo(joinRecord.password());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void validateAndCheckEmailDuplicate_test() {
        // given
        _JoinRecord joinRecord = new _JoinRecord("test@example.com", "password");
        User user = new User(joinRecord);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // when & then
        assertThrows(Exception400.class, () -> userService.saveUser(joinRecord));
    }

    @Test
    void getUser_test() {
        // given
        _LoginRecord loginRecord = new _LoginRecord("test@example.com", "password");
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(user);

        // when
        var response = userService.getUser(loginRecord);

        // then
        assertThat(response.user().email()).isEqualTo(loginRecord.email());
        assertThat(response.user().password()).isEqualTo(loginRecord.password());

        verify(userRepository, times(1)).findByEmailAndPassword(anyString(), anyString());
    }

    @Test
    void getUserInfo_test() {
        // given
        Integer id = 1;
        User user = User.builder()
                .id(id)
                .email("nickname@kakao.com")
                .password(UUID.randomUUID().toString())
                .oauthProvider(OauthProvider.KAKAO)
                .build();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        // when
        var response = userService.getUserInfo(id);

        // then
        assertThat(response.email()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void updateUserInfo_test() {
        // given
        Integer id = 1;
        String newEmail = "new_nickname@kakao.com";
        _UserUpdateRecord reqRecord = new _UserUpdateRecord(newEmail);

        User user = User.builder()
                .id(id)
                .email("new_nickname@kakao.com")
                .password(UUID.randomUUID().toString())
                .oauthProvider(OauthProvider.KAKAO)
                .build();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        // when
        _UserUpdateRespRecord response = userService.updateUserInfo(reqRecord, id);

        // then
        assertThat(response.email()).isEqualTo(newEmail);
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }
}
