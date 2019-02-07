package io.electrica.connector.brassring.application.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.brassring.application.v1.model.BrassRingApplicationPayload;
import io.electrica.connector.brassring.application.v1.model.Envelope;
import io.electrica.connector.brassring.application.v1.model.Form;
import io.electrica.connector.brassring.application.v1.model.Status;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.context.IbmAuthorization;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ApplicationUpdateExecutor implements ConnectorExecutor {

    @VisibleForTesting
    static final String CUSTOM_ERROR_MESSAGE =
            "Got error in API response. Find description in payload: code, short description, long description";
    @VisibleForTesting
    static final String MANIFEST_PROPERTY = "update.manifest";
    @VisibleForTesting
    static final String CUSTOM_ERROR_CODE = "update.ibm";
    @VisibleForTesting
    static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=utf-8");

    private final String url;
    private final OkHttpClient httpClient;
    private final XmlMapper mapper;

    private final String manifest;
    private final BrassRingApplicationPayload payload;
    private final IbmAuthorization authorization;

    ApplicationUpdateExecutor(
            ServiceFacade facade,
            String url,
            OkHttpClient httpClient,
            XmlMapper mapper
    ) throws IntegrationException {
        this.url = url;
        this.httpClient = httpClient;
        this.mapper = mapper;
        manifest = facade.getContext().getConnectionProperties().getStringRequired(MANIFEST_PROPERTY);
        payload = facade.readPayload(BrassRingApplicationPayload.class);
        authorization = facade.getIbmAuthorization();
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String bodyString;
        try {
            Form form = Form.of(payload);
            String formPayload = mapper.writeValueAsString(form);
            Envelope envelope = Envelope.of(manifest, authorization, formPayload);
            bodyString = mapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            throw Exceptions.serialization("Envelope XML building error", e);
        }

        String responseBody;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MEDIA_TYPE, bodyString))
                    .build();
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (body == null) {
                throw Exceptions.io("Empty response");
            }
            responseBody = body.string();
        } catch (IOException e) {
            throw Exceptions.io("Network error", e);
        }

        try {
            Envelope envelope = mapper.readValue(responseBody, Envelope.class);
            Envelope.TransactInfo transactInfo = envelope.getTransactInfo();
            if (transactInfo == null) {
                throw Exceptions.deserialization("Missed `TransactInfo` tag in response");
            }
            Status status = transactInfo.getStatus();
            if (status == null) {
                throw Exceptions.deserialization("Missed `TransactInfo.Status` tag in response");
            }
            if (!Objects.equals("200", status.getCode())) {
                throw Exceptions.custom()
                        .code(CUSTOM_ERROR_CODE)
                        .message(CUSTOM_ERROR_MESSAGE)
                        .payload(Arrays.asList(
                                status.getCode(),
                                status.getShortDescription(),
                                status.getLongDescription())
                        )
                        .build();
            }
        } catch (IOException e) {
            throw Exceptions.deserialization("Cant parse response", e);
        }
        return null;
    }
}
