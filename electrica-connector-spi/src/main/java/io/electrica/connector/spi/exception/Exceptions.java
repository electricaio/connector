package io.electrica.connector.spi.exception;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

public class Exceptions {

    private Exceptions() {
    }

    public static Builder custom() {
        return new Builder();
    }

    public static IntegrationException generic(String message) {
        return generic(message, null);
    }

    public static IntegrationException generic(String message, @Nullable Throwable throwable) {
        return custom()
                .code(ExceptionCodes.GENERIC)
                .message(message)
                .throwable(throwable)
                .build();
    }

    public static IntegrationException serialization(String message) {
        return serialization(message, null);
    }

    public static IntegrationException serialization(String message, @Nullable Throwable throwable) {
        return custom()
                .code(ExceptionCodes.SERIALIZATION)
                .message(message)
                .throwable(throwable)
                .build();
    }

    public static IntegrationException deserialization(String message) {
        return deserialization(message, null);
    }

    public static IntegrationException deserialization(String message, @Nullable Throwable throwable) {
        return custom()
                .code(ExceptionCodes.DESERIALIZATION)
                .message(message)
                .throwable(throwable)
                .build();
    }

    public static IntegrationException validation(String message) {
        return custom()
                .code(ExceptionCodes.VALIDATION)
                .message(message)
                .build();
    }

    public static IntegrationException io(String message) {
        return custom()
                .code(ExceptionCodes.IO)
                .message(message)
                .build();
    }

    public static IntegrationException io(String message, Throwable throwable) {
        return custom()
                .code(ExceptionCodes.IO)
                .message(message)
                .throwable(throwable)
                .build();
    }

    public static class Builder {

        private String code;
        private String message;
        private List<String> payload;
        private Throwable throwable;

        private Builder() {
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder payload(List<String> payload) {
            this.payload = payload;
            return this;
        }

        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public IntegrationException build() {
            Preconditions.checkNotNull(code, "Mandatory parameter: code");
            Preconditions.checkNotNull(message, "Mandatory parameter: message");
            if (throwable == null) {
                return new IntegrationException(code, message, payload);
            } else {
                return new IntegrationException(code, message, payload, throwable);
            }
        }
    }
}
