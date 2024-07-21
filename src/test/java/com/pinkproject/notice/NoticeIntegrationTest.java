package com.pinkproject.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject.notice.noticeRequest._SaveNoticeAdminRecord;
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
public class NoticeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoticeRepository noticeRepository;

    private Notice notice;

    @BeforeEach
    void setUp() {
        notice = new Notice();
        notice.setTitle("공지사항 제목 예시");
        notice.setContent("공지사항 내용 예시");
        noticeRepository.save(notice);
    }

    @Test
    void testCreateNotice() throws Exception {
        // given
        _SaveNoticeAdminRecord createNoticeRequest = new _SaveNoticeAdminRecord(
                "새로운 공지사항 제목",
                "새로운 공지사항 내용"
        );

        // when
        ResultActions actions = mockMvc.perform(
                post("/admin/notice/save")
                        .content(objectMapper.writeValueAsString(createNoticeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("새로운 공지사항 제목"))
                .andExpect(jsonPath("$.content").value("새로운 공지사항 내용"))
                .andDo(document("create-notice",
                        requestFields(
                                fieldWithPath("title").description("공지사항 제목"),
                                fieldWithPath("content").description("공지사항 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").description("공지사항 ID"),
                                fieldWithPath("title").description("공지사항 제목"),
                                fieldWithPath("content").description("공지사항 내용"),
                                fieldWithPath("createdAt").description("생성 날짜")
                        )
                ));
    }

    @Test
    void testGetNotices() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                post("/admin/notice")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document("get-notices",
                        responseFields(
                                fieldWithPath("[].id").description("공지사항 ID"),
                                fieldWithPath("[].title").description("공지사항 제목"),
                                fieldWithPath("[].content").description("공지사항 내용"),
                                fieldWithPath("[].username").description("작성자"),
                                fieldWithPath("[].createdAt").description("생성 날짜")
                        )
                ));
    }

    @Test
    void testGetNoticeById() throws Exception {
        // given
        Integer id = notice.getId();

        // when
        ResultActions actions = mockMvc.perform(
                post("/admin/notice/detail/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("공지사항 제목 예시"))
                .andExpect(jsonPath("$.content").value("공지사항 내용 예시"))
                .andDo(document("get-notice-by-id",
                        responseFields(
                                fieldWithPath("id").description("공지사항 ID"),
                                fieldWithPath("title").description("공지사항 제목"),
                                fieldWithPath("content").description("공지사항 내용"),
                                fieldWithPath("username").description("작성자"),
                                fieldWithPath("createdAt").description("생성 날짜")
                        )
                ));
    }
}
