package io.electrica.connector.spi;

import io.electrica.connector.spi.exception.IntegrationException;

import javax.annotation.Nullable;

public interface ConnectorExecutor {

    @Nullable
    Object run() throws IntegrationException;

}
