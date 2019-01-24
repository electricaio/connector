package io.electrica.connector.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.electrica.connector.spi.ObjectProperties;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.context.ExecutionContext;
import io.electrica.connector.spi.impl.MapObjectProperties;
import io.electrica.connector.spi.service.Logger;
import io.electrica.connector.test.security.SecuredAuthorization;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.UUID;

/**
 * Implement {@link ServiceFacade} interface that will be passed to
 * {@link io.electrica.connector.spi.ConnectorExecutorFactory#create(ServiceFacade)}.
 *
 * @see #builder(Enum)
 * @see #builder(String)
 */
public class InvocationContext implements ServiceFacade {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InvocationContext.class);

    private final ObjectReader objectReader;
    private final ExecutionContext executionContext;

    private Logger logger;

    private InvocationContext(ObjectReader objectReader, ExecutionContext executionContext) {
        this.objectReader = objectReader;
        this.executionContext = executionContext;
    }

    public static Builder builder(Enum<?> action) {
        return builder(action.toString());
    }

    public static Builder builder(String action) {
        return new Builder(action);
    }

    void setErn(String ern) {
        this.logger = new Slf4JLoggerDelegate(log, MarkerFactory.getMarker(ern));
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ExecutionContext getContext() {
        return executionContext;
    }

    @Override
    public ObjectReader getObjectReader() {
        return objectReader;
    }

    public static class Builder {

        private static final String DEFAULT_CONNECTION_NAME = "ignored";

        private final String action;

        private ObjectMapper objectMapper = new ObjectMapper();
        private UUID invocationId = UUID.randomUUID();
        private UUID instanceId = UUID.randomUUID();
        private String connectionName = DEFAULT_CONNECTION_NAME;
        private ObjectProperties connectionProperties = ObjectProperties.EMPTY;
        private SecuredAuthorization authorization;
        private Object parameters;
        private Object payload;

        private Builder(String action) {
            this.action = action;
        }

        /**
         * Specify special mapper to use.
         * <p>
         * Just simple {@link ObjectMapper} instance by default.
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Set unique identifier of integration script invocation.
         * <p>
         * Random id by default.
         */
        public Builder invocationId(UUID invocationId) {
            this.invocationId = invocationId;
            return this;
        }

        /**
         * Set unique identifier SDK instance.
         * <p>
         * Random id by default.
         */
        public Builder instanceId(UUID instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        /**
         * Set connection name.
         * <p>
         * By default specified {@link #DEFAULT_CONNECTION_NAME} value.
         */
        public Builder connectionName(String connectionName) {
            this.connectionName = connectionName;
            return this;
        }

        /**
         * Specify {@link ObjectProperties} object for {@link ExecutionContext#getConnectionProperties()} field.
         * <p>
         * By default {@link ObjectProperties#EMPTY} will used.
         *
         * @see MapObjectProperties
         */
        public Builder connectionProperties(ObjectProperties connectionProperties) {
            this.connectionProperties = connectionProperties;
            return this;
        }

        /**
         * Set secured authorization created by {@link SecuredAuthorizations}.
         * <p>
         * By default is {@code null}.
         */
        public Builder authorization(SecuredAuthorization authorization) {
            this.authorization = authorization;
            return this;
        }

        /**
         * Specify parameters object of integration script invocation.
         * <p>
         * By default is {@code null}.
         */
        public Builder parameters(Object parameters) {
            this.parameters = parameters;
            return this;
        }

        /**
         * Specify payload object of integration script invocation.
         * <p>
         * By default is {@code null}.
         */
        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public InvocationContext build() {
            return new InvocationContext(objectMapper.reader(), new ExecutionContext(
                    invocationId,
                    instanceId,
                    connectionName,
                    connectionProperties,
                    authorization == null ? null : authorization.toSimple(),
                    action,
                    objectMapper.valueToTree(parameters),
                    objectMapper.valueToTree(payload)
            ));
        }
    }
}
