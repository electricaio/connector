package io.electrica.connector.slack.channel.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import io.electrica.connector.slack.channel.v1.model.SlackChannelV1Action;
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
public class SlackChannelV1ExecutorFactory implements ConnectorExecutorFactory {

    @VisibleForTesting
    static final String URL_TEMPLATE_PROPERTY = "url-template";
    @VisibleForTesting
    static final String MAX_IDLE_CONNECTIONS_PROPERTY = "http-client.max-idle-connections";
    @VisibleForTesting
    static final String KEEP_ALIVE_DURATION_MIN_PROPERTY = "http-client.keep-alive-duration-min";

    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 10;
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 60;
    private static final String DEFAULT_URL_TEMPLATE = "https://hooks.slack.com/services/%s";

    private OkHttpClient httpClient;
    private String urlTemplate;
    private ObjectMapper mapper;

    @Override
    public String getErn() {
        return "ern://slack:channel:1";
    }

    @Override
    public void setup(ConnectorProperties properties) throws IntegrationException {
        urlTemplate = properties.getString(URL_TEMPLATE_PROPERTY, DEFAULT_URL_TEMPLATE);

        int maxIdleConnections = properties.getInteger(MAX_IDLE_CONNECTIONS_PROPERTY, DEFAULT_MAX_IDLE_CONNECTIONS);
        int keepAliveDuration = properties.getInteger(KEEP_ALIVE_DURATION_MIN_PROPERTY, DEFAULT_KEEP_ALIVE_DURATION);
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .build();
        mapper = new ObjectMapper();
    }

    @Override
    public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
        SlackChannelV1Action action = facade.readAction(SlackChannelV1Action.class);
        switch (action) {
            case SENDTEXT:
                return new SlackChannelV1SendTextExecutor(httpClient, urlTemplate, facade);
            case SENDATTACHMENT:
                return new SlackChannelV1SendAttachmentExecutor(httpClient, urlTemplate, facade, mapper);
            default:
                throw Exceptions.validation("Unsupported action: " + action);
        }
    }
}
