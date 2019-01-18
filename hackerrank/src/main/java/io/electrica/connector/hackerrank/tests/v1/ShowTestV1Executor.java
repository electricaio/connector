package io.electrica.connector.hackerrank.tests.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.hackerrank.tests.v1.model.HackerRankV3TestsShowPayload;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class ShowTestV1Executor implements ConnectorExecutor {
    private final ServiceFacade facade;
    private final OkHttpClient httpClient;
    private final String url;
    private final ObjectMapper mapper;

    public ShowTestV1Executor(ServiceFacade facade, OkHttpClient httpClient, ObjectMapper mapper, String url) {
        this.facade = facade;
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.url = url;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String token = facade.getTokenAuthorization().getToken();
        HackerRankV3TestsShowPayload idPayload = facade.readPayload(HackerRankV3TestsShowPayload.class);
        return new HTTPGetExecutor(httpClient, mapper, this.url + "/" + idPayload.getId(), token).run();
    }
}
