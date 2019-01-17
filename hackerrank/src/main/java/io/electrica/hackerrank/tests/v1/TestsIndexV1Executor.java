package io.electrica.hackerrank.tests.v1;

import io.electrica.connector.hackerrank.work.v1.model.LimitOffset;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class TestsIndexV1Executor implements ConnectorExecutor {
    private static final String URL_QUERY_PARAMETERS = "?limit=%s&offset=%s";
    private final ServiceFacade facade;
    private final OkHttpClient httpClient;
    private final String url;
    private final String token;

    public TestsIndexV1Executor(ServiceFacade facade, OkHttpClient httpClient, String url) throws IntegrationException {
        this.facade = facade;
        this.httpClient = httpClient;
        this.token = facade.getTokenAuthorization().getToken();
        LimitOffset payload = facade.readPayload(LimitOffset.class);
        String urlParams = String.format(URL_QUERY_PARAMETERS, payload.getLimit(), payload.getOffset());
        this.url = url + urlParams;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        return new HTTPGetExecutor(httpClient, url, token).run();
    }
}
