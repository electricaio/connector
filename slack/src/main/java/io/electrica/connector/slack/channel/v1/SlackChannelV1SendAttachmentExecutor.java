package io.electrica.connector.slack.channel.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.slack.channel.v1.model.Attachment;
import io.electrica.connector.slack.channel.v1.model.SlackChannelV1SendAttachmentPayload;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static io.electrica.connector.spi.Validations.requiredPayloadField;

public class SlackChannelV1SendAttachmentExecutor implements ConnectorExecutor {

    private static final Integer MAX_ATTACHMENTS = 20;
    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient;
    private final String url;
    private final SlackChannelV1SendAttachmentPayload payload;
    private final ObjectMapper mapper;

    @VisibleForTesting
    static final String TOO_MANY_ATTACHMENTS = "Too Many attachments";
    @VisibleForTesting
    static final String AT_LEAST_ONE_ATTACHMENT_PRESENT = "At least one attachment should be present in the payload";

    SlackChannelV1SendAttachmentExecutor(
            OkHttpClient httpClient,
            String urlTemplate,
            ServiceFacade facade,
            ObjectMapper mapper
    ) throws IntegrationException {
        this.httpClient = httpClient;
        url = String.format(urlTemplate, facade.getTokenAuthorization().getToken());
        payload = facade.readPayload(SlackChannelV1SendAttachmentPayload.class);
        this.mapper = mapper;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        List<Attachment> attachments = requiredPayloadField(payload.getAttachments(), "attachments");
        validateAttachments(attachments);
        String toJson = createAttachmentMessage(payload);
        RequestBody body = RequestBody.create(APPLICATION_JSON, toJson);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw Exceptions.generic("Bad response: " + response);
            }
            return null;
        } catch (IOException e) {
            throw Exceptions.io("Network error", e);
        }
    }

    private String createAttachmentMessage(SlackChannelV1SendAttachmentPayload payload) throws IntegrationException {
        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw Exceptions.io("Json Processing Exception", e);
        }
    }


    private void validateAttachments(List<Attachment> attachmentList) throws IntegrationException {
        if (attachmentList != null && !attachmentList.isEmpty()) {
            if (attachmentList.size() > MAX_ATTACHMENTS) {
                throw Exceptions.validation(TOO_MANY_ATTACHMENTS);
            }
        } else {
            throw Exceptions.validation(AT_LEAST_ONE_ATTACHMENT_PRESENT);
        }
    }
}
