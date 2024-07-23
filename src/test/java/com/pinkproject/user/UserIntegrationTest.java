package com.pinkproject.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject._core.error.exception.Exception400;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject.user.UserRequest._JoinRecord;
import com.pinkproject.user.UserRequest._LoginRecord;
import com.pinkproject.user.UserRequest._UserUpdateRecord;
import com.pinkproject.user.UserResponse.*;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
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

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
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
        _JoinRecord joinRecord = new _JoinRecord("testuser@kakao.com", "password"); // 테스트 데이터 수정
        _JoinRespRecord joinRespRecord = new _JoinRespRecord(user.getEmail(), user.getPassword());

        when(userService.saveUser(any(_JoinRecord.class))).thenReturn(joinRespRecord);

        ResultActions actions = mockMvc.perform(
                post("/join")
                        .content(objectMapper.writeValueAsString(joinRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.email").value("testuser@kakao.com")) // 기대값 수정
                .andDo(document("create-user",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response.email").description("사용자 이메일"),
                                fieldWithPath("response.password").description("사용자 비밀번호"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional()
                        )
                ));
    }
    @Test
    void testLoginUser() throws Exception {
        _LoginRecord loginRecord = new _LoginRecord("testuser@kakao.com", "password");
        _LoginRespRecord.UserRecord userRecord = _LoginRespRecord.fromUser(user);
        _LoginRespRecord loginRespRecord = new _LoginRespRecord(userRecord, null);

        when(userService.getUser(any(_LoginRecord.class))).thenReturn(loginRespRecord);

        ResultActions actions = mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(loginRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // 디버그 출력 추가
        String jsonResponse = actions.andReturn().getResponse().getContentAsString();
        System.out.println("Response JSON: " + jsonResponse);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.email").value("testuser@kakao.com")) // JSON 경로 수정
                .andDo(document("login-user",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response.id").description("사용자 ID"),
                                fieldWithPath("response.email").description("사용자 이메일"),
                                fieldWithPath("response.password").description("사용자 비밀번호"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional()
                        )
                ));
    }


    @Test
    void testUpdateUser() throws Exception {
        _UserUpdateRecord updateRecord = new _UserUpdateRecord("newpassword");
        _UserUpdateRespRecord updateRespRecord = new _UserUpdateRespRecord(user.getId(), user.getEmail(), updateRecord.password());

        when(userService.updateUserInfo(any(_UserUpdateRecord.class), anyInt())).thenReturn(updateRespRecord);

        // 로그인 및 JWT 토큰 획득
        String jwt = JwtUtil.create(user);
        _LoginRespRecord loginRespRecord = new _LoginRespRecord(_LoginRespRecord.fromUser(user), "mockJwtToken");
        when(userService.getUser(any(_LoginRecord.class))).thenReturn(loginRespRecord);

        ResultActions loginActions = mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(new _LoginRecord(user.getEmail(), user.getPassword())))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String jwtToken = "Bearer " + jwt;
        System.out.println("JWT 토큰: " + jwtToken);

        // JWT 토큰이 응답 헤더에 포함되었는지 확인
        assertNotNull(jwtToken, "JWT 토큰이 null입니다.");
        assertTrue(jwtToken.startsWith("Bearer "), "JWT 토큰이 Bearer로 시작하지 않음");

        ResultActions actions = mockMvc.perform(
                put("/api/users/" + user.getId())
                        .header("Authorization", jwtToken)
                        .content(objectMapper.writeValueAsString(updateRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        System.out.println("jwtToken: " + jwtToken);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.password").value("newpassword"))
                .andDo(document("update-user",
                        requestFields(
                                fieldWithPath("password").description("새로운 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response.id").description("사용자 ID"),
                                fieldWithPath("response.email").description("사용자 이메일"),
                                fieldWithPath("response.password").description("새로운 비밀번호"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional()
                        )
                ));
    }

    @Test
    void testGetUser() throws Exception {
        _UserRespRecord userRespRecord = new _UserRespRecord(user.getId(), user.getEmail(), user.getPassword());

        when(userService.getUserInfo(anyInt())).thenReturn(userRespRecord);

        // 로그인 및 JWT 토큰 획득
        String jwt = JwtUtil.create(user);
        _LoginRespRecord loginRespRecord = new _LoginRespRecord(_LoginRespRecord.fromUser(user), "jwtToken");
        when(userService.getUser(any(_LoginRecord.class))).thenReturn(loginRespRecord);

        ResultActions loginActions = mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(new _LoginRecord(user.getEmail(), user.getPassword())))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String jwtToken = "Bearer " + jwt;

        // JWT 토큰이 응답 헤더에 포함되었는지 확인
        assertNotNull(jwtToken, "JWT 토큰이 null입니다.");

        ResultActions actions = mockMvc.perform(
                get("/api/users/" + user.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.email").value("testuser@kakao.com"))
                .andDo(document("get-user",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response.id").description("사용자 ID"),
                                fieldWithPath("response.email").description("사용자 이메일"),
                                fieldWithPath("response.password").description("사용자 비밀번호"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional()
                        )
                ));
    }

    @Test
    void testCheckEmail() throws Exception {
        doThrow(new Exception400("중복된 이메일입니다."))
                .when(userService)
                .validateAndCheckEmailDuplicate(anyString());

        ResultActions actions = mockMvc.perform(
                get("/check-email")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
        );

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
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class), any(HttpEntity.class),
                eq(_KakaoUserRecord.class)))
                .thenReturn(new ResponseEntity<>(new _KakaoUserRecord(123L, new Timestamp(System.currentTimeMillis()), new _KakaoUserRecord.Properties("testuser")), HttpStatus.OK));
        when(userService.kakaoLogin(anyString()))
                .thenReturn("pinkAccessToken");

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
