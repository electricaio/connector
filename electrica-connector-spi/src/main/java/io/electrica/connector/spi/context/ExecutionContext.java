package io.electrica.connector.spi.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.electrica.connector.spi.ObjectProperties;
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

    private final ObjectProperties connectionProperties;

    @Nullable
    private final Authorization authorization;

    private final String action;

    @Nullable
    private final JsonNode parameters;

    @Nullable
    private final JsonNode payload;

}
