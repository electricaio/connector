package io.electrica.integration.spi.context;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ExecutionContext {

    private final UUID invocationId;

    private final UUID instanceId;

    private final String connectionName;

    @Nullable
    private final Authorization authorization;

    private final String action;

    @Nullable
    private final JsonNode parameters;

    @Nullable
    private final JsonNode payload;

}
