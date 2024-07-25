package com.pinkproject.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.notice.noticeRequest._SaveNoticeAdminRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class NoticeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoticeRepository noticeRepository;

    private Notice notice;
    private Admin admin;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // 각 테스트 전에 모든 데이터를 삭제하여 초기화
        noticeRepository.deleteAll();


        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcRestDocumentation.document("{class-name}/{method-name}"))
                .build();

        // 고유한 사용자 이름 생성
        String uniqueUsername = "admin_" + UUID.randomUUID().toString().substring(0, 8);
        admin = new Admin();
        admin.setUsername(uniqueUsername);
        admin.setPassword("password");
        adminRepository.save(admin);

        notice = Notice.builder()
                .title("공지사항 제목 예시")
                .content("공지사항 내용 예시")
                .admin(admin)
                .build();

        noticeRepository.save(notice);
    }

    @Test
    void createNotice_test() throws Exception {
        MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders.post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + admin.getUsername() + "\",\"password\":\"password\"}");

        ResultActions loginResult = mockMvc.perform(loginRequest)
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("username").description("관리자 유저네임"),
                                fieldWithPath("password").description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional(),
                                fieldWithPath("response.message").description("응답 메시지")
                        )
                ));

        MockHttpSession session = (MockHttpSession) loginResult.andReturn().getRequest().getSession();
        if (session == null) {
            throw new RuntimeException("로그인 후 세션 검색 실패");
        }

        _SaveNoticeAdminRecord createNoticeRequest = new _SaveNoticeAdminRecord(
                "새로운 공지사항 제목",
                "새로운 공지사항 내용"
        );

        ResultActions actions = mockMvc.perform(
                post("/api/admin/notice/save")
                        .session(session)
                        .content(objectMapper.writeValueAsString(createNoticeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.title").value("새로운 공지사항 제목"))
                .andExpect(jsonPath("$.response.content").value("새로운 공지사항 내용"))
                .andExpect(jsonPath("$.response.username").value(admin.getUsername()))
                .andDo(document("create-notice",
                        requestFields(
                                fieldWithPath("title").description("공지사항 제목"),
                                fieldWithPath("content").description("공지사항 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional(),
                                fieldWithPath("response.id").description("공지사항 ID"),
                                fieldWithPath("response.title").description("공지사항 제목"),
                                fieldWithPath("response.content").description("공지사항 내용"),
                                fieldWithPath("response.username").description("작성자"),
                                fieldWithPath("response.createdAt").description("생성 날짜")
                        )
                ));
    }

    @Test
    void getNotices_test() throws Exception {
        MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders.post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + admin.getUsername() + "\",\"password\":\"password\"}");

        ResultActions loginResult = mockMvc.perform(loginRequest)
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("username").description("관리자 유저네임"),
                                fieldWithPath("password").description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional(),
                                fieldWithPath("response.message").description("응답 메시지")
                        )
                ));

        MockHttpSession session = (MockHttpSession) loginResult.andReturn().getRequest().getSession();
        if (session == null) {
            throw new RuntimeException("로그인 후 세션 검색 실패");
        }

        ResultActions actions = mockMvc.perform(
                get("/api/admin/notice")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andDo(document("get-notices",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional(),
                                fieldWithPath("response.notices.[].id").description("공지사항 ID"),
                                fieldWithPath("response.notices.[].title").description("공지사항 제목"),
                                fieldWithPath("response.notices.[].content").description("공지사항 내용"),
                                fieldWithPath("response.notices.[].username").description("작성자"),
                                fieldWithPath("response.notices.[].createdAt").description("생성 날짜"),
                                fieldWithPath("response.currentDateTime").description("현재 날짜"),
                                fieldWithPath("response.username").description("관리자 유저네임")
                        )
                ));
    }

    @Test
    void getNoticeById_test() throws Exception {
        MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders.post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + admin.getUsername() + "\",\"password\":\"password\"}");

        ResultActions loginResult = mockMvc.perform(loginRequest)
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("username").description("관리자 유저네임"),
                                fieldWithPath("password").description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional(),
                                fieldWithPath("response.message").description("응답 메시지")
                        )
                ));

        MockHttpSession session = (MockHttpSession) loginResult.andReturn().getRequest().getSession();
        if (session == null) {
            throw new RuntimeException("로그인 후 세션 검색 실패");
        }

        Integer id = notice.getId();

        ResultActions actions = mockMvc.perform(
                get("/api/admin/notice/" + id)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String responseString = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseString);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.notice.id").value(id))
                .andExpect(jsonPath("$.response.notice.title").value("공지사항 제목 예시"))
                .andExpect(jsonPath("$.response.notice.content").value("공지사항 내용 예시"))
                .andExpect(jsonPath("$.response.notice.username").value(admin.getUsername()))
                .andDo(document("get-notice-by-id",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("에러 메시지").optional(),
                                fieldWithPath("response.notice.id").description("공지사항 ID"),
                                fieldWithPath("response.notice.title").description("공지사항 제목"),
                                fieldWithPath("response.notice.content").description("공지사항 내용"),
                                fieldWithPath("response.notice.username").description("작성자"),
                                fieldWithPath("response.notice.createdAt").description("생성 날짜"),
                                fieldWithPath("response.currentDateTime").description("현재 날짜"),
                                fieldWithPath("response.username").description("관리자 유저네임")
                        )
                ));
    }
}
