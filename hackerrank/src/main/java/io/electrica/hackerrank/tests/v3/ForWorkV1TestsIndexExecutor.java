package io.electrica.hackerrank.tests.v3;

import io.electrica.connector.hackerrank.work.v3.model.LimitOffset;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class ForWorkV1TestsIndexExecutor implements ConnectorExecutor {
    private static final String URL_QUERY_PARAMETERS = "?limit=%s&offset=%s";
    private final ServiceFacade facade;
    private final OkHttpClient httpClient;
    private final String url;

    public ForWorkV1TestsIndexExecutor(ServiceFacade facade, OkHttpClient httpClient, String url) {
        this.facade = facade;
        this.httpClient = httpClient;
        this.url = url;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String token = facade.getTokenAuthorization().getToken();
        LimitOffset payload = facade.readPayload(LimitOffset.class);
        String urlParams = String.format(URL_QUERY_PARAMETERS, payload.getLimit(), payload.getOffset());
        return new HTTPGetExecutor(httpClient, this.url + urlParams, token).run();
    }
}
