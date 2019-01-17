package io.electrica.hackerrank.tests.v1;

import io.electrica.connector.spi.exception.IntegrationException;
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

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private HTTPGetExecutor httpGetExecutor;

    private static String url;
    private String token = "123";


    private static MockWebServer server;


    @BeforeAll
    static void setUpOkHttp() {
        server = new MockWebServer();
        try {
            server.start();
        } catch (IOException e) {
        }
        url = server.url("/test").toString();
    }

    @BeforeEach
    void setUp() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();
        httpGetExecutor = new HTTPGetExecutor(httpClient, url, token);
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }


    @Test
    void authorizationTokenPassedInRequest() throws IntegrationException, InterruptedException {
        goodResponse();
        httpGetExecutor.run();
        RecordedRequest recordedRequest = server.takeRequest();
        String authHeader = recordedRequest.getHeader(AUTHORIZATION_HEADER);
        assertEquals(authHeader, "Bearer " + token);
    }


    @Test
    void urlPassedInRequest() throws IntegrationException, InterruptedException {
        goodResponse();
        httpGetExecutor.run();
        RecordedRequest recordedRequest = server.takeRequest();
        String path = recordedRequest.getRequestUrl().toString();
        assertEquals(path, url);
    }

    @Test
    void unsuccessfulResponseThrowsAnError() throws IntegrationException, InterruptedException {
        badResponse();
        assertThrows(IntegrationException.class, () -> {
            httpGetExecutor.run();
        });
    }

    @Test
    void successfulRequestReturnsBodyResult() throws IntegrationException, InterruptedException {
        String goodResponse = "ok";
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setBody(goodResponse);
        server.enqueue(mockedResponse);
        Object result = httpGetExecutor.run();
        assertEquals(goodResponse, result);
    }


    private void goodResponse() {
        String goodResponse = "ok";
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setBody(goodResponse);
        server.enqueue(mockedResponse);
    }

    private void badResponse() {
        MockResponse mockedResponse = new MockResponse();
        mockedResponse.setResponseCode(404);
        mockedResponse.setBody("ERROR");
        server.enqueue(mockedResponse);
    }
}
