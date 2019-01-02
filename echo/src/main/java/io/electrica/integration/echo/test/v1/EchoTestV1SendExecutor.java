package io.electrica.integration.echo.test.v1;

import io.electrica.integration.echo.test.v1.model.EchoTestV1SendParameters;
import io.electrica.integration.echo.test.v1.model.EchoTestV1SendPayload;
import io.electrica.integration.echo.test.v1.model.EchoTestV1SendResult;
import io.electrica.integration.spi.ConnectorExecutor;
import io.electrica.integration.spi.ServiceFacade;
import io.electrica.integration.spi.exception.Exceptions;
import io.electrica.integration.spi.exception.IntegrationException;

import javax.annotation.Nullable;

import static io.electrica.integration.echo.test.v1.EchoTestV1ExecutorFactory.REQUESTED_ERROR_MESSAGE;

public class EchoTestV1SendExecutor implements ConnectorExecutor {

    private final EchoTestV1SendParameters parameters;
    private final EchoTestV1SendPayload payload;

    EchoTestV1SendExecutor(ServiceFacade facade) throws IntegrationException {
        parameters = facade.readParameters(EchoTestV1SendParameters.class);
        payload = facade.readPayload(EchoTestV1SendPayload.class);
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        if (parameters.isThrowException()) {
            throw Exceptions.generic(REQUESTED_ERROR_MESSAGE);
        }
        return new EchoTestV1SendResult().message(payload.getMessage());
    }
}
