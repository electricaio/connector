package io.electrica.connector.spi.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenAuthorization implements Authorization {

    private final String token;

}
