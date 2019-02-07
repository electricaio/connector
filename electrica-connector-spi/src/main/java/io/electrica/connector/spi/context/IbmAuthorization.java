package io.electrica.connector.spi.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IbmAuthorization implements Authorization {

    private final String integrationId;

    private final String clientId;
}
