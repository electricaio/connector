package io.electrica.connector.hackerrank.v3.tests.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.hackerrank.v3.tests.v1.model.HackerRankV3TestsAction;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ConnectorExecutorFactory;
import io.electrica.connector.spi.ConnectorProperties;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

@AutoService(ConnectorExecutorFactory.class)
public class HackerRankTestsV1ExecutorFactory implements ConnectorExecutorFactory {

    @VisibleForTesting
    static final String URL_PROPERTY = "api.url";
    @VisibleForTesting
    static final String MAX_IDLE_CONNECTIONS_PROPERTY = "http-client.max-idle-connections";
    @VisibleForTesting
    static final String KEEP_ALIVE_DURATION_MIN_PROPERTY = "http-client.keep-alive-duration-min";
    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 10;
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 60;
    private static final String DEFAULT_URL = "https://www.hackerrank.com/x/api/v3/tests";

    private final ObjectMapper mapper = new ObjectMapper();
    private OkHttpClient httpClient;
    private String testsUrl;

    @Override
    public String getErn() {
        return "ern://hackerrank-v3:tests:1";
    }

    @Override
    public void setup(ConnectorProperties properties) throws IntegrationException {
        testsUrl = properties.getString(URL_PROPERTY, DEFAULT_URL);

        int maxIdleConnections = properties.getInteger(MAX_IDLE_CONNECTIONS_PROPERTY, DEFAULT_MAX_IDLE_CONNECTIONS);
        int keepAliveDuration = properties.getInteger(KEEP_ALIVE_DURATION_MIN_PROPERTY, DEFAULT_KEEP_ALIVE_DURATION);
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .build();
    }

    @Override
    public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
        HackerRankV3TestsAction action = facade.readAction(HackerRankV3TestsAction.class);
        switch (action) {
            case TESTSINDEX:
                return new TestsIndexV1Executor(facade, httpClient, mapper, testsUrl);
            case TESTSSHOW:
                return new ShowTestV1Executor(facade, httpClient, mapper, testsUrl);
            default:
                throw Exceptions.validation("Unsupported action: " + action);
        }
    }
}
