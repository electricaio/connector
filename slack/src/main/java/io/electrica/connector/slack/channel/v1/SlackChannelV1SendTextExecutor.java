package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.slack.channel.v1.model.SlackChannelV2SendTextPayload;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.*;

import javax.annotation.Nullable;
import java.io.IOException;

import static io.electrica.connector.spi.Validations.requiredPayloadField;

public class SlackChannelV1SendTextExecutor implements ConnectorExecutor {

    private final OkHttpClient httpClient;

    private final String url;
    private final SlackChannelV2SendTextPayload payload;

    SlackChannelV1SendTextExecutor(
            OkHttpClient httpClient,
            String urlTemplate,
            ServiceFacade facade
    ) throws IntegrationException {
        this.httpClient = httpClient;
        url = String.format(urlTemplate, facade.getTokenAuthorization().getToken());
        payload = facade.readPayload(SlackChannelV2SendTextPayload.class);
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String message = requiredPayloadField(payload.getMessage(), "message");
        RequestBody body = RequestBody.create(
                MediaType.get("application/json"),
                String.format("{\"text\":\"%s\"}", message)
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw Exceptions.io("Bad response: " + response);
            }
            return null;
        } catch (IOException e) {
            throw Exceptions.io("Network error", e);
        }
    }
}
