package com.pinkproject.faq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject.faq.faqRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.enums.FaqEnum;
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

import java.time.LocalDateTime;

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
    private FaqRepository faqRepository;

    private Faq faq;

    @BeforeEach
    void setUp() {
        faq = new Faq();
        faq.setTitle("반품 정책은 어떻게 되나요?");
        faq.setContent("반품은 구매일로부터 30일 이내에 가능합니다.");
        faq.setClassification(FaqEnum.USE); // 적절한 분류 설정
        faqRepository.save(faq);
    }

    @Test
    void testCreateFaq() throws Exception {
        // given
        _SaveFaqAdminRecord createFaqRequest = new _SaveFaqAdminRecord(
                "주문을 추적하는 방법은?",
                "주문 추적 링크가 이메일로 전송됩니다.",
                FaqEnum.USE.name()
        );

        // when
        ResultActions actions = mockMvc.perform(
                post("/faq")
                        .content(objectMapper.writeValueAsString(createFaqRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("주문을 추적하는 방법은?"))
                .andExpect(jsonPath("$.content").value("주문 추적 링크가 이메일로 전송됩니다."))
                .andDo(document("create-faq",
                        requestFields(
                                fieldWithPath("title").description("FAQ 제목"),
                                fieldWithPath("content").description("FAQ 내용"),
                                fieldWithPath("classification").description("FAQ 분류")
                        ),
                        responseFields(
                                fieldWithPath("id").description("FAQ ID"),
                                fieldWithPath("title").description("FAQ 제목"),
                                fieldWithPath("content").description("FAQ 내용"),
                                fieldWithPath("classification").description("FAQ 분류"),
                                fieldWithPath("createdAt").description("생성 날짜")
                        )
                ));
    }
}
