package io.electrica.connector.brassring.v1;

import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.brassring.application.v1.model.BrassRingV1Action;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ConnectorExecutorFactory;
import io.electrica.connector.spi.ConnectorProperties;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class BrassRingExecutorFactory implements ConnectorExecutorFactory {

    @VisibleForTesting
    static final String URL_TEMPLATE_PROPERTY = "url-template";
    @VisibleForTesting
    static final String MAX_IDLE_CONNECTIONS_PROPERTY = "http-client.max-idle-connections";
    @VisibleForTesting
    static final String KEEP_ALIVE_DURATION_MIN_PROPERTY = "http-client.keep-alive-duration-min";

    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 10;
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 60;
    private static final String DEFAULT_URL_TEMPLATE = "http://localhost/";


    private OkHttpClient httpClient;
    private String urlTemplate;

    @Override
    public String getErn() {
        return "ern://brassring:applications:1";
    }

    @Override
    public void setup(ConnectorProperties properties) throws IntegrationException {
        urlTemplate = properties.getString(URL_TEMPLATE_PROPERTY, DEFAULT_URL_TEMPLATE);

        int maxIdleConnections = properties.getInteger(MAX_IDLE_CONNECTIONS_PROPERTY, DEFAULT_MAX_IDLE_CONNECTIONS);
        int keepAliveDuration = properties.getInteger(KEEP_ALIVE_DURATION_MIN_PROPERTY, DEFAULT_KEEP_ALIVE_DURATION);
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .build();
    }

    @Override
    public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
        BrassRingV1Action action = facade.readAction(BrassRingV1Action.class);
        switch (action) {
            case PUT:
                return new BrassRinglV1RequestExecutor(httpClient, urlTemplate, facade);
            default:
                throw Exceptions.validation("Unsupported action: " + action);
        }
    }

}
