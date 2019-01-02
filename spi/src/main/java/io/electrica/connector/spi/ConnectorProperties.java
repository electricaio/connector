package io.electrica.connector.spi;

import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;

import javax.annotation.Nullable;

import static io.electrica.connector.spi.Validations.required;

public interface ConnectorProperties {

    boolean contains(String key);

    @Nullable
    String getString(String key);

    default String getString(String key, String defaultValue) {
        String value = getString(key);
        return value == null ? defaultValue : value;
    }

    default String getStringRequired(String key) throws IntegrationException {
        return required(getString(key), "Required configuration parameter '%s'", key);
    }

    @Nullable
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    default Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getString(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    default boolean getBooleanRequired(String key) throws IntegrationException {
        return required(getBoolean(key), "Required configuration parameter '%s'", key);
    }

    @Nullable
    default Integer getInteger(String key) throws IntegrationException {
        return getInteger(key, null);
    }

    default Integer getInteger(String key, Integer defaultValue) throws IntegrationException {
        String value = getString(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw Exceptions.deserialization("Cant parse as integer: " + value, e);
        }
    }

    default int getIntegerRequired(String key) throws IntegrationException {
        return required(getInteger(key), "Required configuration parameter '%s'", key);
    }

    @Nullable
    default Long getLong(String key) throws IntegrationException {
        return getLong(key, null);
    }

    default Long getLong(String key, Long defaultValue) throws IntegrationException {
        String value = getString(key);
        try {
            return value == null ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw Exceptions.deserialization("Cant parse as long: " + value, e);
        }
    }

    default long getLongRequired(String key) throws IntegrationException {
        return required(getLong(key), "Required configuration parameter '%s'", key);
    }

    @Nullable
    default Double getDouble(String key) throws IntegrationException {
        return getDouble(key, null);
    }

    default Double getDouble(String key, Double defaultValue) throws IntegrationException {
        String value = getString(key);
        try {
            return value == null ? defaultValue : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw Exceptions.deserialization("Cant parse as double: " + value, e);
        }
    }

    default double getDoubleRequired(String key) throws IntegrationException {
        return required(getDouble(key), "Required configuration parameter '%s'", key);
    }

}
