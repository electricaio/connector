package io.electrica.integration.spi.exception;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class IntegrationException extends Exception {

    private final String code;
    @Nullable
    private final List<String> payload;

    protected IntegrationException(String code, String message) {
        this(code, message, (List<String>) null);
    }

    protected IntegrationException(String code, String message, @Nullable List<String> payload) {
        super(message);
        this.code = code;
        this.payload = payload;
    }

    protected IntegrationException(String code, String message, Throwable throwable) {
        this(code, message, null, throwable);
    }

    protected IntegrationException(String code, String message, @Nullable List<String> payload, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.payload = payload;
    }

}
