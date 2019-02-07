package io.electrica.connector.brassring.application.v1;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.brassring.application.v1.model.BrassRingApplicationAction;
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
public class ApplicationExecutorFactory implements ConnectorExecutorFactory {

    @VisibleForTesting
    static final String API_URL_PROPERTY = "api.url";
    private static final String MAX_IDLE_CONNECTIONS_PROPERTY = "http-client.max-idle-connections";
    private static final String KEEP_ALIVE_DURATION_MIN_PROPERTY = "http-client.keep-alive-duration-min";

    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 10;
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 60;
    private static final String DEFAULT_API_URL =
            "https://trm.brassring.com/jetstream/500/presentation/template/asp/HRISIntegration/msgdispatch.asp";

    private String url;
    private OkHttpClient httpClient;
    private XmlMapper mapper;

    @Override
    public String getErn() {
        return "ern://brassring:application:1";
    }

    @Override
    public void setup(ConnectorProperties properties) throws IntegrationException {
        url = properties.getString(API_URL_PROPERTY, DEFAULT_API_URL);

        int maxIdleConnections = properties.getInteger(MAX_IDLE_CONNECTIONS_PROPERTY, DEFAULT_MAX_IDLE_CONNECTIONS);
        int keepAliveDuration = properties.getInteger(KEEP_ALIVE_DURATION_MIN_PROPERTY, DEFAULT_KEEP_ALIVE_DURATION);
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .build();
        mapper = new XmlMapper();
    }

    @Override
    public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
        BrassRingApplicationAction action = facade.readAction(BrassRingApplicationAction.class);
        switch (action) {
            case UPDATE:
                return new ApplicationUpdateExecutor(facade, url, httpClient, mapper);
            default:
                throw Exceptions.validation("Unsupported action: " + action);
        }
    }
}
