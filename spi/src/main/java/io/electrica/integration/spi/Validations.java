package io.electrica.integration.spi;

import io.electrica.integration.spi.exception.Exceptions;
import io.electrica.integration.spi.exception.IntegrationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Validations {

    private static final String REQUIRED_PAYLOAD_FIELD_MESSAGE_TEMPLATE = "Payload: '%s' required";
    private static final String REQUIRED_PARAMETERS_FIELD_MESSAGE_TEMPLATE = "Parameters: '%s' required";

    private Validations() {
    }


    public static void check(boolean condition, String message) throws IntegrationException {
        if (!condition) {
            throw Exceptions.validation(message);
        }
    }

    public static void check(boolean condition, String message, Object arg) throws IntegrationException {
        check(condition, String.format(message, arg));
    }

    public static void check(boolean condition, String message, Object arg1, Object arg2) throws IntegrationException {
        check(condition, String.format(message, arg1, arg2));
    }

    public static void check(boolean condition, String message, Object... args) throws IntegrationException {
        check(condition, String.format(message, args));
    }


    @Nonnull
    public static <T> T required(@Nullable T value, String message) throws IntegrationException {
        check(value != null, message);
        return value;
    }

    @Nonnull
    public static <T> T required(@Nullable T value, String message, Object arg) throws IntegrationException {
        check(value != null, message, arg);
        return value;
    }

    @Nonnull
    public static <T> T required(@Nullable T value, String message, Object arg1, Object arg2)
            throws IntegrationException {
        check(value != null, message, arg1, arg2);
        return value;
    }

    @Nonnull
    public static <T> T required(@Nullable T value, String message, Object... args) throws IntegrationException {
        check(value != null, message, args);
        return value;
    }

    public static String requiredParametersFieldErrorMessage(String fieldName) {
        return String.format(REQUIRED_PARAMETERS_FIELD_MESSAGE_TEMPLATE, fieldName);
    }

    @Nonnull
    public static <T> T requiredParametersField(@Nullable T value, String fieldName) throws IntegrationException {
        return required(value, requiredParametersFieldErrorMessage(fieldName));
    }

    public static String requiredPayloadFieldErrorMessage(String fieldName) {
        return String.format(REQUIRED_PAYLOAD_FIELD_MESSAGE_TEMPLATE, fieldName);
    }

    @Nonnull
    public static <T> T requiredPayloadField(@Nullable T value, String fieldName) throws IntegrationException {
        return required(value, requiredPayloadFieldErrorMessage(fieldName));
    }
}
