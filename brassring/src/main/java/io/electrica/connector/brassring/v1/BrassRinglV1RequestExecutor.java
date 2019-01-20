package io.electrica.connector.brassring.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.electrica.connector.brassring.v1.model.Envelope;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.*;

import javax.annotation.Nullable;
import java.io.IOException;

import static io.electrica.connector.spi.Validations.requiredPayloadField;

public class BrassRinglV1RequestExecutor implements ConnectorExecutor {

    private static final MediaType APPLICATION_XML = MediaType.parse("application/xml; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String url;
    private final String token;
    private final Envelope payload;

    public BrassRinglV1RequestExecutor(OkHttpClient httpClient,
                                       String url,
                                       ServiceFacade facade) throws IntegrationException {
        this.httpClient = httpClient;
        this.url = url;
        this.token = facade.getTokenAuthorization().getToken();
        this.payload = facade.readPayload(Envelope.class);
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {

        String message;
        try {
            message = new XmlMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw Exceptions.io("XML Processing Exception", e);
        }
        requiredPayloadField(message, "Envelope");

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(APPLICATION_XML, message))
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw Exceptions.io(response.body().string());
            }
            return response.body().string();
        } catch (IOException e) {
            throw Exceptions.io("Network error", e);
        }
    }
}
