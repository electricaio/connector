package io.electrica.connector.hackerrank.v3.common.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.*;

import java.io.IOException;

public class HTTPExecutor {

    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;

    private final String url;
    private final String token;
    private final ObjectMapper mapper;
    private String method;
    private RequestBody jsonBody;


    public HTTPExecutor(
            OkHttpClient httpClient,
            ObjectMapper mapper,
            String url,
            String token
    ) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.token = token;
        this.url = url;
        this.method = "GET";
        this.jsonBody = null;
    }

    public HTTPExecutor buildPost(String jsonBody) {
        this.method = "POST";
        this.jsonBody = RequestBody.create(APPLICATION_JSON, jsonBody);
        return this;
    }


    public <R> R run(Class<R> resultType) throws IntegrationException {


        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.token)
                .method(this.method, this.jsonBody)
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
