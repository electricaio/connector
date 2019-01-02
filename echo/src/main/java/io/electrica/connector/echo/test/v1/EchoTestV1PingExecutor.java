package io.electrica.connector.echo.test.v1;

import io.electrica.connector.echo.test.v1.model.EchoTestV1PingParameters;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;

import javax.annotation.Nullable;

import static io.electrica.connector.echo.test.v1.EchoTestV1ExecutorFactory.REQUESTED_ERROR_MESSAGE;

public class EchoTestV1PingExecutor implements ConnectorExecutor {

    private final EchoTestV1PingParameters parameters;

    EchoTestV1PingExecutor(ServiceFacade facade) throws IntegrationException {
        parameters = facade.readParameters(EchoTestV1PingParameters.class);
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        if (parameters.isThrowException()) {
            throw Exceptions.generic(REQUESTED_ERROR_MESSAGE);
        }
        return null;
    }
}
