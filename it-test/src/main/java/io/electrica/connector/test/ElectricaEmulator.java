package io.electrica.connector.test;

import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ConnectorExecutorFactory;
import io.electrica.connector.spi.ConnectorProperties;
import io.electrica.connector.spi.exception.IntegrationException;
import lombok.SneakyThrows;

import static java.util.Objects.requireNonNull;

/**
 * Helper class to emulate Electrica cloud integration script execution on local machine.
 */
public class ElectricaEmulator implements AutoCloseable {

    private final ConnectorExecutorFactory executorFactory;
    private final ConnectorProperties connectorProperties;

    private ElectricaEmulator(ConnectorExecutorFactory executorFactory, ConnectorProperties connectorProperties) {
        this.executorFactory = requireNonNull(executorFactory, "executorFactory");
        this.connectorProperties = requireNonNull(connectorProperties, "connectorProperties");
    }

    /**
     * Creates simple Emulator instance for passed class of {@link ConnectorExecutorFactory}.
     *
     * @see #builder(Class)
     */
    public static ElectricaEmulator of(Class<? extends ConnectorExecutorFactory> executorFactoryClass) {
        return builder(executorFactoryClass).build();
    }

    /**
     * Creates builder of {@link ElectricaEmulator} instance for specified {@link ConnectorExecutorFactory} class.
     *
     * @see Builder
     */
    public static Builder builder(Class<? extends ConnectorExecutorFactory> executorFactoryClass) {
        return new Builder(executorFactoryClass);
    }

    /**
     * Execute integration script for specified invocation parameters.
     *
     * @param context object that describe script invocation
     * @return result of execution
     * @throws IntegrationException if any exception during integration occur
     */
    public Object runIntegration(InvocationContext context) throws IntegrationException {
        executorFactory.setup(connectorProperties);
        context.setErn(executorFactory.getErn());
        ConnectorExecutor executor = executorFactory.create(context);
        return executor.run();
    }

    /**
     * Execute integration script for specified invocation parameters and cast result to expected type.
     *
     * @param resultType type of result, should be casted to
     * @param context    object that describe script invocation
     * @param <T>        type of script result
     * @return result of execution
     * @throws IntegrationException if any exception during integration occur
     */
    public <T> T runIntegration(Class<T> resultType, InvocationContext context) throws IntegrationException {
        return resultType.cast(runIntegration(context));
    }

    public ConnectorExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    public ConnectorProperties getConnectorProperties() {
        return connectorProperties;
    }

    @Override
    public void close() {
        executorFactory.beforeDestroy();
    }

    public static class Builder {

        private final Class<? extends ConnectorExecutorFactory> executorFactoryClass;

        private ConnectorProperties connectorProperties = MapConnectorProperties.EMPTY;

        private Builder(Class<? extends ConnectorExecutorFactory> executorFactoryClass) {
            this.executorFactoryClass = executorFactoryClass;
        }

        /**
         * Specify {@link ConnectorProperties} object to pass to
         * {@link ConnectorExecutorFactory#setup(ConnectorProperties)} executor factory method.
         * <p>
         * By default {@link MapConnectorProperties#EMPTY} will used.
         *
         * @see MapConnectorProperties
         */
        public Builder connectorProperties(ConnectorProperties connectorProperties) {
            this.connectorProperties = connectorProperties;
            return this;
        }

        @SneakyThrows
        public ElectricaEmulator build() {
            ConnectorExecutorFactory executorFactory = executorFactoryClass.newInstance();
            executorFactory.afterLoad();
            return new ElectricaEmulator(executorFactory, connectorProperties);
        }
    }
}
