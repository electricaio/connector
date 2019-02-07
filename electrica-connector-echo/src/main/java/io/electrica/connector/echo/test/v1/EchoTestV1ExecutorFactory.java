package io.electrica.connector.echo.test.v1;

import com.google.auto.service.AutoService;
import io.electrica.connector.echo.test.v1.model.EchoTestV1Action;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ConnectorExecutorFactory;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;

@AutoService(ConnectorExecutorFactory.class)
public class EchoTestV1ExecutorFactory implements ConnectorExecutorFactory {

    static final String REQUESTED_ERROR_MESSAGE = "Integration exception has been requested in parameters";

    @Override
    public String getErn() {
        return "ern://echo:test:1";
    }

    @Override
    public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
        EchoTestV1Action action = facade.readAction(EchoTestV1Action.class);
        switch (action) {
            case PING:
                return new EchoTestV1PingExecutor(facade);
            case SEND:
                return new EchoTestV1SendExecutor(facade);
            default:
                throw Exceptions.validation("Unsupported action: " + action);
        }
    }
}
