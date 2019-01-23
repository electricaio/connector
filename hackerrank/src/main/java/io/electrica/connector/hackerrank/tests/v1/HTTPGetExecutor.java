package io.electrica.connector.hackerrank.tests.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

class HTTPGetExecutor {

    private final OkHttpClient httpClient;

    private final String url;
    private final String token;
    private final ObjectMapper mapper;

    HTTPGetExecutor(
            OkHttpClient httpClient,
            ObjectMapper mapper,
            String url,
            String token
    ) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.token = token;
        this.url = url;
    }

    <R> R run(Class<R> resultType) throws IntegrationException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.token)
                .get()
                .build();

        ResponseBody body;
        try {
            Response response = httpClient.newCall(request).execute();
            body = response.body();
            if (!response.isSuccessful()) {
                String message = response.toString() + "\n" + (body == null ? "" : body.toString());
                throw Exceptions.generic(message);
            }
        } catch (IOException e) {
            throw Exceptions.io("Network error", e);
        }

        if (body == null) {
            throw Exceptions.generic("Empty response body");
        }

        try {
            return mapper.readValue(body.charStream(), resultType);
        } catch (IOException e) {
            throw Exceptions.deserialization("Can't deserialize response", e);
        }
    }
}
