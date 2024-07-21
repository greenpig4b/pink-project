package com.pinkproject.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinkproject.transaction.TransactionRequest._SaveTransactionRecord;
import com.pinkproject.transaction.TransactionRequest._UpdateTransactionRecord;
import com.pinkproject.transaction.TransactionResponse.*;
import com.pinkproject.transaction.enums.Assets;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;
import com.pinkproject.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpSession session;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        this.webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void testSaveTransaction() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        _SaveTransactionRecord record = new _SaveTransactionRecord(
                1,
                TransactionType.INCOME,
                "2023-07-01",
                "10:00:00",
                1000,
                CategoryIn.SALARY,
                CategoryOut.FOOD,
                Assets.BANK,
                "description"
        );

        // When
        _SaveTransactionRespRecord response = this.webTestClient.post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(record)
                .exchange()
                .expectStatus().isOk()
                .expectBody(_SaveTransactionRespRecord.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1);
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        _UpdateTransactionRecord record = new _UpdateTransactionRecord(
                1,
                1,
                TransactionType.EXPENSE,
                "2023-07-01",
                "12:00:00",
                500,
                CategoryIn.SALARY,
                CategoryOut.FOOD,
                Assets.BANK,
                "updated description"
        );

        // When
        _UpdateTransactionRespRecord response = this.webTestClient.put()
                .uri("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(record)
                .exchange()
                .expectStatus().isOk()
                .expectBody(_UpdateTransactionRespRecord.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1);
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        // When
        _DeleteTransactionRespRecord response = this.webTestClient.delete()
                .uri("/api/transactions/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(_DeleteTransactionRespRecord.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1);
    }

    @Test
    public void testGetMonthlyTransactions() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        // When
        _MonthlyTransactionMainRecord response = this.webTestClient.get()
                .uri("/api/transactions/monthly?year=2023&month=7")
                .exchange()
                .expectStatus().isOk()
                .expectBody(_MonthlyTransactionMainRecord.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1);
    }

    @Test
    public void testGetMonthlyFinancialReportMain() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        // When
        _MonthlyFinancialReport response = this.webTestClient.get()
                .uri("/api/financial-report?year=2023&month=7")
                .exchange()
                .expectStatus().isOk()
                .expectBody(_MonthlyFinancialReport.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1);
    }

    @Test
    public void testGetCalendar() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        // When
        _MonthlyCalendar response = this.webTestClient.get()
                .uri("/api/calendar?year=2023&month=7")
                .exchange()
                .expectStatus().isOk()
                .expectBody(_MonthlyCalendar.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1);
    }

    @Test
    public void testGetChart() throws Exception {
        // Given
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(1);
        sessionUser.setEmail("testUser@test.com");
        session.setAttribute("sessionUser", sessionUser);

        // When
        _ChartRespRecord response = this.webTestClient.get()
                .uri("/api/chart?year=2023&month=7&week=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(_ChartRespRecord.class)
                .returnResult().getResponseBody();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.monthCount()).isEqualTo(7);
    }
}
