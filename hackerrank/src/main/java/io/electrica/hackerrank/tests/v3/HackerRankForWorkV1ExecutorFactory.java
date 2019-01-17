package io.electrica.hackerrank.tests.v3;

import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.hackerrank.work.v3.model.WorkV1Action;
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
public class HackerRankForWorkV1ExecutorFactory implements ConnectorExecutorFactory {


    @VisibleForTesting
    static final String TESTS_URL_TEMPLATE_PROPERTY = "tests.url-template";
    @VisibleForTesting
    static final String MAX_IDLE_CONNECTIONS_PROPERTY = "http-client.max-idle-connections";
    @VisibleForTesting
    static final String KEEP_ALIVE_DURATION_MIN_PROPERTY = "http-client.keep-alive-duration-min";
    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 10;
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 60;
    private OkHttpClient httpClient;

    private String testsUrlTemplate;

    @Override
    public String getErn() {
        return "ern://hackerrank:work:1";
    }

    private static final String DEFAULT_TESTS_URL_TEMPLATE = "https://www.hackerrank.com/x/api/v3/tests";

    @Override
    public void setup(ConnectorProperties properties) throws IntegrationException {
        testsUrlTemplate = properties.getString(TESTS_URL_TEMPLATE_PROPERTY, DEFAULT_TESTS_URL_TEMPLATE);

        int maxIdleConnections = properties.getInteger(MAX_IDLE_CONNECTIONS_PROPERTY, DEFAULT_MAX_IDLE_CONNECTIONS);
        int keepAliveDuration = properties.getInteger(KEEP_ALIVE_DURATION_MIN_PROPERTY, DEFAULT_KEEP_ALIVE_DURATION);
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .build();
    }

    @Override
    public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
        WorkV1Action action = facade.readAction(WorkV1Action.class);

        switch (action) {
            case TESTSINDEX:
                return new ForWorkV1TestsIndexExecutor(facade, httpClient, testsUrlTemplate);
            case TESTSSHOW:
                return new ForWorkV1TestsShowExecutor(facade, httpClient, testsUrlTemplate);
            default:
                throw Exceptions.validation("Unsupported action: " + action);
        }
    }

}
