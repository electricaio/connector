package io.electrica.integration.spi;

import io.electrica.integration.spi.exception.IntegrationException;

import javax.annotation.Nullable;

public interface ConnectorExecutor {

    @Nullable
    Object run() throws IntegrationException;

}
