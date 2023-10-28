package com.example.bokuassignment;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.example.bokuassignment.configuration.ApiClientConfiguration.REQUEST_TIMEOUT;

@SpringBootTest
class BokuAssignmentApplicationTests {
    private MockWebServer mockWebServer;
    private AdapterService adapterService;

    @BeforeEach
    void setupMock() {
        mockWebServer = new MockWebServer();
        var proxyFactory = HttpServiceProxyFactory.builder(
                        WebClientAdapter.forClient(WebClient.builder()
                                .baseUrl(mockWebServer.url("/").url().toString()).build()))
                .blockTimeout(REQUEST_TIMEOUT)
                .build();

        adapterService = new AdapterService(
                proxyFactory.createClient(MerchantClient.class),
                proxyFactory.createClient(ProviderClient.class)
        );
    }

    @Test
    void happyPath() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"reply_message\":\"Test response\"}")
        );

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        adapterService.handlePaymentNotification(
                UUID.randomUUID(),
                "+372555666777",
                "TXT COINS",
                "13011",
                "Salat",
                LocalDateTime.now()
        );

        RecordedRequest merchantRequest = mockWebServer.takeRequest();

        assertThat(merchantRequest.getPath()).isEqualTo("/sms/txt");

        RecordedRequest providerRequest = mockWebServer.takeRequest();

        assertThat(providerRequest.getRequestUrl().encodedPath()).isEqualTo("/sms/send");
    }

    @Test
    void sadPath() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"reply_message\":\"Test response fail\"}")
        );

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        assertThatThrownBy(() -> {
            adapterService.handlePaymentNotification(
                    UUID.randomUUID(),
                    "+372555666777",
                    "FOR COINS",
                    "13011",
                    "Salat",
                    LocalDateTime.now()
            );
        }).isInstanceOf(Exception.class);

        RecordedRequest merchantRequest = mockWebServer.takeRequest();

        assertThat(merchantRequest.getPath()).isEqualTo("/sms/for");
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }
}
