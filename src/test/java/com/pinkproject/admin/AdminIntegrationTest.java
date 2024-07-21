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
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
    void setUp() {
        admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
        adminRepository.save(admin);
    }

    @Test
    void testFindByUsername() throws Exception {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "password");

        // when
        ResultActions actions = mockMvc.perform(
                post("/admin/authenticate")
                        .content(objectMapper.writeValueAsString(loginAdminRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andDo(document("admin-authenticate",
                        requestFields(
                                fieldWithPath("username").description("The admin's username"),
                                fieldWithPath("password").description("The admin's password")
                        ),
                        responseFields(
                                fieldWithPath("username").description("The admin's username"),
                                fieldWithPath("token").description("The authentication token")
                        )
                ));
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "password");

        // when
        ResultActions actions = mockMvc.perform(
                post("/admin/authenticate")
                        .content(objectMapper.writeValueAsString(loginAdminRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andDo(document("admin-authenticate-success",
                        requestFields(
                                fieldWithPath("username").description("The admin's username"),
                                fieldWithPath("password").description("The admin's password")
                        ),
                        responseFields(
                                fieldWithPath("username").description("The admin's username"),
                                fieldWithPath("token").description("The authentication token")
                        )
                ));
    }

    @Test
    void testAuthenticateFailure() throws Exception {
        // given
        _LoginAdminRecord loginAdminRecord = new _LoginAdminRecord("admin", "wrongpassword");

        // when
        ResultActions actions = mockMvc.perform(
                post("/admin/authenticate")
                        .content(objectMapper.writeValueAsString(loginAdminRecord))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isUnauthorized())
                .andDo(document("admin-authenticate-failure",
                        requestFields(
                                fieldWithPath("username").description("The admin's username"),
                                fieldWithPath("password").description("The admin's password")
                        ),
                        responseFields(
                                fieldWithPath("status").description("The response status"),
                                fieldWithPath("error").description("The error message")
                        )
                ));
    }
}
