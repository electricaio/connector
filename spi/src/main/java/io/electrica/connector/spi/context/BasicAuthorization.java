package io.electrica.connector.spi.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicAuthorization implements Authorization {

    private final String username;

    private final String password;

}
