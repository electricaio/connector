package io.electrica.connector.spi;

import io.electrica.connector.spi.exception.IntegrationException;

public interface ConnectorExecutorFactory {

    String getErn();

    default void afterLoad() {
        // nop
    }

    default void setup(ConnectorProperties properties) throws IntegrationException{
        // nop
    }

    ConnectorExecutor create(ServiceFacade facade) throws IntegrationException;

    default void beforeDestroy() {
        // nop
    }
}
