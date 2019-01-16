package io.electrica.hackerrank.tests.v3;

import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;

public class HTTPGetExecutor implements ConnectorExecutor {

    private final OkHttpClient httpClient;

    private final String url;
    private final String token;

    public HTTPGetExecutor(
            OkHttpClient httpClient,
            String url,
            String token
    ) {
        this.httpClient = httpClient;
        this.token = token;
        this.url = url;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + this.token)
                .get()
                .build();


        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw Exceptions.generic(response.body().string());
            }

            return response.body().string();
        } catch (IOException e) {
            throw Exceptions.io("Network error", e);
        }
    }
}
