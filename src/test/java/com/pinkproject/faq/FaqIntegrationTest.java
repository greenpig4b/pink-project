package com.pinkproject.faq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pinkproject._core.utils.CustomCharacterEscapes;
import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.faq.faqRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.enums.FaqEnum;
import jakarta.annotation.PostConstruct;
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
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class FaqIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FaqRepository faqRepository;

    private Faq faq;

    private RestDocumentationResultHandler document;

    @Autowired
    private WebApplicationContext context;

    @PostConstruct
    void init() {
        objectMapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.writerWithDefaultPrettyPrinter();
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        faqRepository.deleteAll();
        adminRepository.deleteAll();

        faq = new Faq();
        faq.setTitle("반품 정책은 어떻게 되나요?");
        faq.setContent("반품은 구매일로부터 30일 이내에 가능합니다.");
        faq.setClassification(FaqEnum.USE);
        faqRepository.save(faq);

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("password");
        adminRepository.save(admin);

        this.document = document("{method-name}");

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .alwaysDo(this.document)
                .build();
    }

    @Test
    void createFaq_test() throws Exception {
        // given
        _SaveFaqAdminRecord createFaqRequest = new _SaveFaqAdminRecord(
                "주문을 추적하는 방법은?",
                "주문 추적 링크가 이메일로 전송됩니다.",
                FaqEnum.USE.name()
        );

        // 로그인 요청
        MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders.post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"password\"}");

        ResultActions loginResult = mockMvc.perform(loginRequest)
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("username").description("관리자 유저네임"),
                                fieldWithPath("password").description("관리자 비밀번호")
                        )
                ));

        MockHttpSession session = (MockHttpSession) loginResult.andReturn().getRequest().getSession();
        if (session == null) {
            throw new RuntimeException("로그인 후 세션 검색 실패");
        }

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/admin/faq/save")
                        .session(session)
                        .content(objectMapper.writeValueAsString(createFaqRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.title").value("주문을 추적하는 방법은?"))
                .andExpect(jsonPath("$.response.content").value("주문 추적 링크가 이메일로 전송됩니다."))
                .andDo(document("create-faq",
                        requestFields(
                                fieldWithPath("title").description("FAQ 제목"),
                                fieldWithPath("content").description("FAQ 내용"),
                                fieldWithPath("classification").description("FAQ 분류")
                        ),
                        responseFields(
                                fieldWithPath("response.id").description("FAQ ID"),
                                fieldWithPath("response.title").description("FAQ 제목"),
                                fieldWithPath("response.content").description("FAQ 내용"),
                                fieldWithPath("response.classification").description("FAQ 분류"),
                                fieldWithPath("response.createdAt").description("생성 날짜"),
                                fieldWithPath("response.username").description("관리자 유저네임"),
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("오류 메시지")
                        )
                ));
    }
}
