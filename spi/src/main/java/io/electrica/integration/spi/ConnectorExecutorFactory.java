package io.electrica.integration.spi;

import io.electrica.integration.spi.exception.IntegrationException;

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
