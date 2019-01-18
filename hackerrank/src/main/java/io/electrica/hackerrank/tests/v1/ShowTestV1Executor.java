package io.electrica.hackerrank.tests.v1;

import io.electrica.connector.hackerrank.work.v1.model.HackerRankTestsShowPayload;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class ShowTestV1Executor implements ConnectorExecutor {
    private final ServiceFacade facade;
    private final OkHttpClient httpClient;
    private final String url;

    public ShowTestV1Executor(ServiceFacade facade, OkHttpClient httpClient, String url) {
        this.facade = facade;
        this.httpClient = httpClient;
        this.url = url;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String token = facade.getTokenAuthorization().getToken();
        HackerRankTestsShowPayload idPayload = facade.readPayload(HackerRankTestsShowPayload.class);
        return new HTTPGetExecutor(httpClient, this.url + "/" + idPayload.getId(), token).run();
    }
}
