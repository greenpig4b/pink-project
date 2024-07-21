package com.pinkproject.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse._KakaoUserRecord;
import com.pinkproject.user.enums.OauthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Mock
    private RestTemplate restTemplate;

    private User user;
    private String kakaoAccessToken;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1)
                .email("testuser@kakao.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build();

        kakaoAccessToken = "testAccessToken";
    }

    @Test
    void testCreateUser() throws Exception {
        _JoinRecord joinRecord = new _JoinRecord("test@example.com", "password");

        ResultActions actions = mockMvc.perform(
                post("/join")
                        .content(objectMapper.writeValueAsString(joinRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(document("create-user",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.email").description("사용자 이메일"),
                                fieldWithPath("data.password").description("사용자 비밀번호")
                        )
                ));
    }

    @Test
    void testLoginUser() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build();
        userRepository.save(user);

        _LoginRecord loginRecord = new _LoginRecord("test@example.com", "password");

        ResultActions actions = mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(loginRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(document("login-user",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.user.id").description("사용자 ID"),
                                fieldWithPath("data.user.email").description("사용자 이메일"),
                                fieldWithPath("data.user.password").description("사용자 비밀번호"),
                                fieldWithPath("data.jwt").description("JWT 토큰")
                        )
                ));
    }

    @Test
    void testGetUser() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build();
        userRepository.save(user);

        SessionUser sessionUser = new SessionUser(user);
        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(new _LoginRecord(user.getEmail(), user.getPassword())))
                .contentType(MediaType.APPLICATION_JSON)
        );

        ResultActions actions = mockMvc.perform(
                get("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andDo(document("get-user",
                        responseFields(
                                fieldWithPath("data.id").description("사용자 ID"),
                                fieldWithPath("data.email").description("사용자 이메일"),
                                fieldWithPath("data.password").description("사용자 비밀번호")
                        )
                ));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build();
        userRepository.save(user);

        SessionUser sessionUser = new SessionUser(user);
        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(new _LoginRecord(user.getEmail(), user.getPassword())))
                .contentType(MediaType.APPLICATION_JSON)
        );

        _UserUpdateRecord updateRecord = new _UserUpdateRecord("newpassword");

        ResultActions actions = mockMvc.perform(
                put("/api/users/" + user.getId())
                        .content(objectMapper.writeValueAsString(updateRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.password").value("newpassword"))
                .andDo(document("update-user",
                        requestFields(
                                fieldWithPath("password").description("새로운 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("사용자 ID"),
                                fieldWithPath("data.email").description("사용자 이메일"),
                                fieldWithPath("data.password").description("새로운 비밀번호")
                        )
                ));
    }

    @Test
    void testCheckEmail() throws Exception {
        userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build());

        ResultActions actions = mockMvc.perform(
                get("/check-email")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isBadRequest())
                .andDo(document("check-email",
                        queryParameters(
                                parameterWithName("email").description("확인할 이메일 주소")
                        ),
                        responseFields(
                                fieldWithPath("msg").description("응답 메시지")
                        )
                ));
    }

    @Test
    public void testKakaoLogin() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(_KakaoUserRecord.class)))
                .thenReturn(new ResponseEntity<>(new _KakaoUserRecord(123L, new Timestamp(System.currentTimeMillis()), new _KakaoUserRecord.Properties("testuser")), HttpStatus.OK));

        ResultActions actions = mockMvc.perform(get("/oauth/callback/kakao")
                        .param("accessToken", kakaoAccessToken))
                .andExpect(status().isOk());

        actions.andDo(document("kakao-login",
                queryParameters(
                        parameterWithName("accessToken").description("카카오 액세스 토큰")
                ),
                responseHeaders(
                        headerWithName("Authorization").description("JWT 토큰")
                )
        ));
    }
}
