package io.electrica.hackerrank.tests.v3;

import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class ForWorkV1TestsShowExecutor implements ConnectorExecutor {
    private final ServiceFacade facade;
    private final OkHttpClient httpClient;
    private final String url;

    public ForWorkV1TestsShowExecutor(ServiceFacade facade, OkHttpClient httpClient, String url) {
        this.facade = facade;
        this.httpClient = httpClient;
        this.url = url;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String token = facade.getTokenAuthorization().getToken();
        int id = facade.readPayload(Integer.class);
        return new HTTPGetExecutor(httpClient, this.url + "/" + id, token).run();
    }
}
