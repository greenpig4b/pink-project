package com.pinkproject.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject._core.utils.JwtUtil;
import com.pinkproject._core.utils.SummaryUtil;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.*;
import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.SessionUser;
import com.pinkproject.user.User;
import com.pinkproject.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class, SpringExtension.class})
@ActiveProfiles("test")
public class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private MockHttpSession session;

    private String jwtToken;
    private User user;
    private Transaction transaction;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // 기존 데이터 삭제
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        // 사용자 데이터 생성 및 저장
        user = new User();
        user.setEmail("testUser@test.com");
        user.setPassword("password");
        userRepository.save(user);

        // 세션 설정
        session = new MockHttpSession();
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(user.getId());
        sessionUser.setEmail(user.getEmail());
        session.setAttribute("sessionUser", sessionUser);

        // JWT 토큰 생성
        jwtToken = JwtUtil.create(user);
        System.out.println("JWT token: " + jwtToken);

        // 거래 데이터 생성 및 저장
        transaction = Transaction.builder()
                .user(user)
                .transactionType(TransactionType.INCOME)
                .assets(Assets.BANK)
                .categoryIn(CategoryIn.SALARY)
                .amount(1000)
                .description("거래 테스트")
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }

    @Test
    public void saveTransaction_test() throws Exception {
        _SaveTransactionRecord record = new _SaveTransactionRecord(
                user.getId(),  // 사용자 ID
                TransactionType.INCOME,
                "2023-07-01",
                "10:00:00",
                1000,
                CategoryIn.SALARY,
                CategoryOut.FOOD,
                Assets.BANK,
                "description"
        );

        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .session(session)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString).path("response");

        _SaveTransactionRespRecord response = objectMapper.treeToValue(jsonResponse, _SaveTransactionRespRecord.class);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(user.getId());
    }

    @Test
    public void updateTransaction_test() throws Exception {
        SummaryUtil.DailySummary dailySummary = new SummaryUtil.DailySummary(500, 1000, 1500);
        Map<LocalDate, SummaryUtil.DailySummary> dailySummaries = Map.of(LocalDate.of(2023, 7, 1), dailySummary);
        SummaryUtil.Summary summary = new SummaryUtil.Summary(
                500, // monthlyIncome
                1000,
                1500,
                Map.of(Assets.BANK, 500),
                Map.of(Assets.BANK, 1000),
                dailySummaries // dailySummaries
        );

        try (MockedStatic<SummaryUtil> utilities = mockStatic(SummaryUtil.class)) {
            utilities.when(() -> SummaryUtil.calculateSummary(Mockito.anyList())).thenReturn(summary);

            _UpdateTransactionRecord record = new _UpdateTransactionRecord(
                    user.getId(),  // 사용자 ID
                    transaction.getId(),  // 트랜잭션 ID
                    TransactionType.EXPENSE,
                    "2023-07-01",
                    "12:00:00",
                    500,
                    CategoryIn.SALARY,
                    CategoryOut.FOOD,
                    Assets.BANK,
                    "updated description"
            );

            MvcResult result = mockMvc.perform(put("/api/transactions/" + transaction.getId())
                            .session(session)
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record)))
                    .andExpect(status().isOk())
                    .andReturn();

            String contentAsString = result.getResponse().getContentAsString();
            JsonNode jsonResponse = objectMapper.readTree(contentAsString).path("response");

            _UpdateTransactionRespRecord response = objectMapper.treeToValue(jsonResponse, _UpdateTransactionRespRecord.class);

            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(user.getId());
        }
    }

    @Test
    public void deleteTransaction_test() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/transactions/" + transaction.getId())
                        .session(session)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString).path("response");

        _DeleteTransactionRespRecord response = objectMapper.treeToValue(jsonResponse, _DeleteTransactionRespRecord.class);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(user.getId());
    }

    @Test
    public void getMonthlyTransactions_test() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/transactions/monthly")
                        .session(session)
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("year", "2023")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString).path("response");

        _MonthlyTransactionMainRecord response = objectMapper.treeToValue(jsonResponse, _MonthlyTransactionMainRecord.class);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(user.getId());
    }

    @Test
    public void getMonthlyFinancialReportMain_test() throws Exception {
        System.out.println("JWT token: " + jwtToken);
        System.out.println("Session User: " + session.getAttribute("sessionUser"));

        MvcResult result = mockMvc.perform(get("/api/financial-report")
                        .session(session)
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("year", "2023")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        System.out.println("Response String: " + responseString);

        JsonNode jsonResponse = objectMapper.readTree(responseString).path("response");
        System.out.println("JSON Response: " + jsonResponse);

        _MonthlyFinancialReport response = objectMapper.treeToValue(jsonResponse, _MonthlyFinancialReport.class);
        System.out.println("Monthly Financial Report: " + response);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(user.getId());
    }

    @Test
    public void getCalendar_test() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/calendar")
                        .session(session)
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("year", "2023")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString).path("response");

        _MonthlyCalendar response = objectMapper.treeToValue(jsonResponse, _MonthlyCalendar.class);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(user.getId());
    }

    @Test
    public void getChart_test() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/chart/monthly")
                        .session(session)
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("year", "2023")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString).path("response");

        _ChartRespRecord response = objectMapper.treeToValue(jsonResponse, _ChartRespRecord.class);

        assertThat(response).isNotNull();
        assertThat(response.monthCount()).isEqualTo(7);
    }
}
