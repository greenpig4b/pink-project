package com.pinkproject.memo;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject.memo.MemoRequest._SaveMemoRecord;
import com.pinkproject.memo.MemoRequest._UpdateMemoRecord;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
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

    @Autowired
    private WebApplicationContext context;

    private User user;
    private Memo memo;
    private String jwtToken;
    private RestDocumentationResultHandler document;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        memoRepository.deleteAll();
        userRepository.deleteAll();

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

        jwtToken = JwtUtil.create(user);

        this.document = MockMvcRestDocumentation.document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document)
                .build();
    }

    @Test
    void createMemo_test() throws Exception {
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
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(createMemoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value("새로운 메모 제목"))
                .andExpect(jsonPath("$.response.content").value("새로운 메모 내용"))
                .andDo(document("create-memo",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("yearMonthDate").description("메모 날짜 (yyyy-MM-dd 형식)"),
                                fieldWithPath("title").description("메모 제목"),
                                fieldWithPath("content").description("메모 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("오류 메시지"),
                                fieldWithPath("response.id").description("메모 ID"),
                                fieldWithPath("response.userId").description("유저 ID"),
                                fieldWithPath("response.monthDateDay").description("메모 날짜 (MM.dd 형식)"),
                                fieldWithPath("response.title").description("메모 제목"),
                                fieldWithPath("response.content").description("메모 내용")
                        )
                ));
    }

    @Test
    void getMonthlyMemos_test() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                get("/api/memos/monthly")
                        .param("year", "2024")
                        .param("month", "7")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document("get-monthly-memos",
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("오류 메시지"),
                                fieldWithPath("response.userId").description("유저 ID"),
                                fieldWithPath("response.year").description("년도"),
                                fieldWithPath("response.month").description("월"),
                                fieldWithPath("response.dailyMemoRecords").description("일별 메모 기록 리스트"),
                                fieldWithPath("response.dailyMemoRecords[].date").description("날짜"),
                                fieldWithPath("response.dailyMemoRecords[].dailyMemoRecordList").description("일별 메모 리스트"),
                                fieldWithPath("response.dailyMemoRecords[].dailyMemoRecordList[].id").description("메모 ID"),
                                fieldWithPath("response.dailyMemoRecords[].dailyMemoRecordList[].title").description("메모 제목"),
                                fieldWithPath("response.dailyMemoRecords[].dailyMemoRecordList[].content").description("메모 내용")
                        )
                ));
    }

    @Test
    void updateMemo_test() throws Exception {
        // given
        _UpdateMemoRecord updateMemoRequest = new _UpdateMemoRecord(
                memo.getId(),  // 메모 ID 추가
                user.getId(),
                "업데이트된 메모 제목",
                "업데이트된 메모 내용"
        );

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/memos/" + memo.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(updateMemoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value("업데이트된 메모 제목"))
                .andExpect(jsonPath("$.response.content").value("업데이트된 메모 내용"))
                .andDo(document("update-memo",
                        requestFields(
                                fieldWithPath("id").description("메모 ID"),
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("title").description("메모 제목"),
                                fieldWithPath("content").description("메모 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("오류 메시지"),
                                fieldWithPath("response.id").description("메모 ID"),
                                fieldWithPath("response.userId").description("유저 ID"),
                                fieldWithPath("response.title").description("메모 제목"),
                                fieldWithPath("response.content").description("메모 내용")
                        )
                ));
    }

    @Test
    void deleteMemo_test() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/memos/" + memo.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document("delete-memo",
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("errorMessage").description("오류 메시지"),
                                fieldWithPath("response").description("삭제된 메모에 대한 응답 데이터")
                        )
                ));
    }
}
