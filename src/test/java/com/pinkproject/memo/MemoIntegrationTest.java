package com.pinkproject.memo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject.memo.MemoRequest._SaveMemoRecord;
import com.pinkproject.memo.MemoResponse._SaveMemoRespRecord;
import com.pinkproject.memo.MemoResponse._MonthlyMemoMainRecord;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import com.pinkproject.user.enums.OauthProvider;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class MemoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Memo memo;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("testuser@example.com")
                .password("password")
                .oauthProvider(OauthProvider.KAKAO)
                .build();
        userRepository.save(user);

        memo = Memo.builder()
                .user(user)
                .title("테스트 메모 제목")
                .content("테스트 메모 내용")
                .createdAt(LocalDateTime.now())
                .build();
        memoRepository.save(memo);
    }

    @Test
    void testCreateMemo() throws Exception {
        // given
        _SaveMemoRecord createMemoRequest = new _SaveMemoRecord(
                user.getId(),
                "2024-07-21",
                "새로운 메모 제목",
                "새로운 메모 내용"
        );

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/memos")
                        .content(objectMapper.writeValueAsString(createMemoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("새로운 메모 제목"))
                .andExpect(jsonPath("$.data.content").value("새로운 메모 내용"))
                .andDo(document("create-memo",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("yearMonthDate").description("메모 날짜 (yyyy-MM-dd 형식)"),
                                fieldWithPath("title").description("메모 제목"),
                                fieldWithPath("content").description("메모 내용")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("메모 ID"),
                                fieldWithPath("data.userId").description("유저 ID"),
                                fieldWithPath("data.monthDateDay").description("메모 날짜 (MM.dd 형식)"),
                                fieldWithPath("data.title").description("메모 제목"),
                                fieldWithPath("data.content").description("메모 내용")
                        )
                ));
    }

    @Test
    void testGetMonthlyMemos() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                get("/api/memos/monthly")
                        .param("year", "2024")
                        .param("month", "7")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document("get-monthly-memos",
                        responseFields(
                                fieldWithPath("data.userId").description("유저 ID"),
                                fieldWithPath("data.year").description("년도"),
                                fieldWithPath("data.month").description("월"),
                                fieldWithPath("data.dailyMemoRecords").description("일별 메모 기록 리스트"),
                                fieldWithPath("data.dailyMemoRecords[].date").description("날짜"),
                                fieldWithPath("data.dailyMemoRecords[].dailyMemoRecordList").description("일별 메모 리스트"),
                                fieldWithPath("data.dailyMemoRecords[].dailyMemoRecordList[].id").description("메모 ID"),
                                fieldWithPath("data.dailyMemoRecords[].dailyMemoRecordList[].title").description("메모 제목"),
                                fieldWithPath("data.dailyMemoRecords[].dailyMemoRecordList[].content").description("메모 내용")
                        )
                ));
    }

    @Test
    void testUpdateMemo() throws Exception {
        // given
        _SaveMemoRecord updateMemoRequest = new _SaveMemoRecord(
                user.getId(),
                "2024-07-21",
                "업데이트된 메모 제목",
                "업데이트된 메모 내용"
        );

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/memos/" + memo.getId())
                        .content(objectMapper.writeValueAsString(updateMemoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("업데이트된 메모 제목"))
                .andExpect(jsonPath("$.data.content").value("업데이트된 메모 내용"))
                .andDo(document("update-memo",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("yearMonthDate").description("메모 날짜 (yyyy-MM-dd 형식)"),
                                fieldWithPath("title").description("메모 제목"),
                                fieldWithPath("content").description("메모 내용")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("메모 ID"),
                                fieldWithPath("data.userId").description("유저 ID"),
                                fieldWithPath("data.title").description("메모 제목"),
                                fieldWithPath("data.content").description("메모 내용")
                        )
                ));
    }

    @Test
    void testDeleteMemo() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/memos/" + memo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document("delete-memo",
                        responseFields(
                                fieldWithPath("data").description("삭제된 메모에 대한 응답 데이터")
                        )
                ));
    }
}
