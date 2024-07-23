package com.pinkproject.admin;

import com.pinkproject.admin.AdminRequest._LoginAdminRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    private Admin admin;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        adminRepository.deleteAll();

        admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
        try {
            adminRepository.save(admin);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testFindByUsername() throws Exception {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "password");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/admin/login")
                        .content(objectMapper.writeValueAsString(loginAdminRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        System.out.println("응답: " + actions.andReturn().getResponse().getContentAsString());

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.message").value("로그인 성공"))
                .andDo(document("admin-authenticate",
                        requestFields(
                                fieldWithPath("username").description("관리자의 사용자 이름"),
                                fieldWithPath("password").description("관리자의 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response.message").description("응답 메시지"),
                                fieldWithPath("errorMessage").description("오류 메시지").optional()
                        )
                ));
    }


    @Test
    void testAuthenticateSuccess() throws Exception {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "password");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/admin/login")
                        .content(objectMapper.writeValueAsString(loginAdminRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        System.out.println("응답: " + actions.andReturn().getResponse().getContentAsString());

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.message").value("로그인 성공"))
                .andDo(document("admin-authenticate-success",
                        requestFields(
                                fieldWithPath("username").description("관리자의 사용자 이름"),
                                fieldWithPath("password").description("관리자의 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response.message").description("응답 메시지"),
                                fieldWithPath("errorMessage").description("오류 메시지").optional()
                        )
                ));
    }


    @Test
    void testAuthenticateFailure() throws Exception {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "wrongpassword");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/admin/login")
                        .content(objectMapper.writeValueAsString(loginAdminRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        System.out.println("응답: " + actions.andReturn().getResponse().getContentAsString());

        // then
        actions.andExpect(status().isUnauthorized())
                .andDo(document("admin-authenticate-failure",
                        requestFields(
                                fieldWithPath("username").description("관리자의 사용자 이름"),
                                fieldWithPath("password").description("관리자의 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("response").description("응답 데이터").optional(),
                                fieldWithPath("errorMessage").description("오류 메시지")
                        )
                ));
    }
}
