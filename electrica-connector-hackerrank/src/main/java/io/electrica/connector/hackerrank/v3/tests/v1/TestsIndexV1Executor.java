package io.electrica.connector.hackerrank.v3.tests.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.hackerrank.v3.common.v1.HTTPExecutor;
import io.electrica.connector.hackerrank.v3.tests.v1.model.HackerRankV3TestsIndexResponse;
import io.electrica.connector.hackerrank.v3.tests.v1.model.LimitOffset;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class TestsIndexV1Executor implements ConnectorExecutor {

    private static final String URL_QUERY_PARAMETERS = "?limit=%s&offset=%s";

    private final OkHttpClient httpClient;
    private final String url;
    private final String token;
    private final ObjectMapper mapper;

    public TestsIndexV1Executor(ServiceFacade facade, OkHttpClient httpClient, ObjectMapper mapper, String url)
            throws IntegrationException {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.token = facade.getTokenAuthorization().getToken();
        this.url = buildUrl(url, facade);
    }

    private static String buildUrl(String url, ServiceFacade facade) throws IntegrationException {
        LimitOffset payload = facade.readPayload(LimitOffset.class);
        String urlParameters = String.format(URL_QUERY_PARAMETERS, payload.getLimit(), payload.getOffset());
        return url + urlParameters;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        return new HTTPExecutor(httpClient, mapper, url, token)
                .run(HackerRankV3TestsIndexResponse.class);
    }
}
