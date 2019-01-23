package io.electrica.connector.hackerrank.tests.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.spi.exception.IntegrationException;
import lombok.*;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HTTPGetExecutorTest {

    private static final String GOOD_RESPONSE = "{\"field\":\"ok\"}";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static String url;
    private static MockWebServer server;

    private HTTPGetExecutor httpGetExecutor;
    private String token = "123";

    @BeforeAll
    static void setUpOkHttp() throws IOException {
        server = new MockWebServer();
        server.start();
        url = server.url("/test").toString();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    void setUp() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();
        httpGetExecutor = new HTTPGetExecutor(httpClient, new ObjectMapper(), url, token);
    }

    @Test
    void authorizationTokenPassedInRequest() throws IntegrationException, InterruptedException {
        goodResponse();
        httpGetExecutor.run(One.class);
        RecordedRequest recordedRequest = server.takeRequest();
        String authHeader = recordedRequest.getHeader(AUTHORIZATION_HEADER);
        assertEquals(authHeader, "Bearer " + token);
    }


    @Test
    void urlPassedInRequest() throws IntegrationException, InterruptedException {
        goodResponse();
        httpGetExecutor.run(One.class);
        RecordedRequest recordedRequest = server.takeRequest();
        String path = recordedRequest.getRequestUrl().toString();
        assertEquals(path, url);
    }

    @Test
    void unsuccessfulResponseThrowsAnError() {
        badResponse();
        assertThrows(IntegrationException.class, () -> httpGetExecutor.run(One.class));
    }

    @Test
    void successfulRequestReturnsBodyResult() throws IntegrationException {
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setBody(GOOD_RESPONSE);
        server.enqueue(mockedResponse);
        One result = httpGetExecutor.run(One.class);
        assertEquals(new One("ok"), result);
    }


    private void goodResponse() {
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setBody(GOOD_RESPONSE);
        server.enqueue(mockedResponse);
    }

    private void badResponse() {
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setResponseCode(404);
        mockedResponse.setBody("ERROR");
        server.enqueue(mockedResponse);
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    private static class One {
        private String field;
    }
}
