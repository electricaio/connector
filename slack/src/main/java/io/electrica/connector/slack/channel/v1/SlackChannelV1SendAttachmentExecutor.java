package io.electrica.connector.slack.channel.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private final OkHttpClient httpClient;
    private final String url;
    private final SlackChannelV1SendAttachmentPayload payload;
    private final ObjectMapper mapper;

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
        if (attachments.size() > MAX_ATTACHMENTS) {
            throw Exceptions.validation("Restrict the attachments to less than " + MAX_ATTACHMENTS);
        }
        String toJson = createAttachmentMessage(attachments);
        RequestBody body = RequestBody.create(MediaType.get("application/json"), toJson);

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

    private String createAttachmentMessage(List<Attachment> attachments) {
        ObjectNode parent = mapper.createObjectNode();
        ArrayNode arrayNode = parent.putArray("attachments");
        for (Attachment attachment : attachments) {
            JsonNode node = mapper.valueToTree(attachment);
            arrayNode.add(node);
        }
        return parent.toString();
    }
}
