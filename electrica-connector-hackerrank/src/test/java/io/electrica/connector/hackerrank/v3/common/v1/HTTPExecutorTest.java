package io.electrica.connector.hackerrank.v3.common.v1;

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
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HTTPExecutorTest {


    public static final String HTTP_POST = "POST";
    private static final String JSON_BODY = "{\"hello\":\"world\"}";
    private static final String GOOD_RESPONSE = "{\"field\":\"ok\"}";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static String url;
    private static MockWebServer server;

    private HTTPExecutor httpExecutor;
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
        httpExecutor = new HTTPExecutor(httpClient, new ObjectMapper(), url, token);
    }

    @Test
    void authorizationTokenPassedInRequest() throws IntegrationException, InterruptedException {
        goodResponse();
        httpExecutor.run(One.class);
        RecordedRequest recordedRequest = server.takeRequest();
        String authHeader = recordedRequest.getHeader(AUTHORIZATION_HEADER);
        assertEquals(authHeader, "Bearer " + token);
    }


    @Test
    void urlPassedInRequest() throws IntegrationException, InterruptedException {
        goodResponse();
        httpExecutor.run(One.class);
        RecordedRequest recordedRequest = server.takeRequest();
        String path = recordedRequest.getRequestUrl().toString();
        assertEquals(path, url);
    }

    @Test
    void unsuccessfulResponseThrowsAnError() {
        badResponse();
        assertThrows(IntegrationException.class, () -> httpExecutor.run(One.class));
    }

    @Test
    void successfulRequestReturnsBodyResult() throws IntegrationException {
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setBody(GOOD_RESPONSE);
        server.enqueue(mockedResponse);
        One result = httpExecutor.run(One.class);
        assertEquals(new One("ok"), result);
    }


    @Test
    void postMethodIsCalled() throws IntegrationException, InterruptedException {
        goodResponse();
        httpExecutor.buildPost(JSON_BODY).run(One.class);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals(recordedRequest.getMethod(), HTTP_POST);
    }

    @Test
    void jsonBodyPassedInPost() throws IntegrationException, InterruptedException {
        goodResponse();
        httpExecutor.buildPost(JSON_BODY).run(One.class);
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals(recordedRequest.getBody().readString(Charset.defaultCharset()), JSON_BODY);
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
