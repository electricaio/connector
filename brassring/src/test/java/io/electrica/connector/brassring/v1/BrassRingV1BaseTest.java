package io.electrica.connector.brassring.v1;

import io.electrica.connector.test.ElectricaEmulator;
import io.electrica.connector.test.MapConnectorProperties;

public abstract class BrassRingV1BaseTest {

    protected static final String BRASSRING_TOKEN = "" +
            "N1NgfqmDqPX6Zt0kxztAPNTQ5/EEjh3o5z3QBQ+/NrnzWjeZBVEIFpIT48O34YHu23Cv+861OrTgviF9dqTkuUaPv/ds2Uq9SPs" +
            "Z23IXpZTdXH6pTEt96QFkEZZLhe+QaJGz0yFmFItaVoZLf9rAirPSNoYS80TUGAyMhE5NFyP4zLqhkyi2ilvJunrJ8UX1cann0M" +
            "/YCJe5wyls2eWt0G/9Qhv/zT3N4ocjZCnK+Dh3d44F+/TyhPwFOVv0DE1qxv8MzmsH32igOTmNRCkj03iPHarRl8nGc+FL6yxN3" +
            "Q/5NNh9FsChoGzvXadf+XrmS0WKv1YANos95P60eU2XoQ==";

    protected static ElectricaEmulator emulator;

    protected static void init(String url) {
        MapConnectorProperties connectorProperties = MapConnectorProperties.builder()
                .addString(BrassRingExecutorFactory.URL_TEMPLATE_PROPERTY, url)
                .addInteger(BrassRingExecutorFactory.MAX_IDLE_CONNECTIONS_PROPERTY, 1)
                .addInteger(BrassRingExecutorFactory.KEEP_ALIVE_DURATION_MIN_PROPERTY, 1)
                .build();

        emulator = ElectricaEmulator.builder(BrassRingExecutorFactory.class)
                .connectorProperties(connectorProperties)
                .build();
    }
}
