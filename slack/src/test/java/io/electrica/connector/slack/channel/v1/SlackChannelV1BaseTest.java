package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.spi.impl.MapConnectorProperties;
import io.electrica.connector.test.ElectricaEmulator;
import org.junit.jupiter.api.BeforeAll;

public abstract class SlackChannelV1BaseTest {

    protected static final String TEST_CHANNEL_TOKEN = "" +
            "JiMzedqX8thrLBlg49i9RPNA+d0KSY3nY5Ez2o4QPuwtRycWsMgE4CfiApASwNEwir8IuUJMjYTn+WoXR8mJMBS/Ws2gaGa8udj8c6CD" +
            "B6/0tF7ToB/l7sBadZ2W+sPgVPqC6hc8qIKwCoNUIgsLBs7bULTre4jyhAJPNlEhAYSIshb46cBGhGW9B+xVz2Oe7mJUdQ3vrZfyFdVM" +
            "vL0TAVED5SFNkHq8InEPUUaxcFxbzOpx9O+yU3cJjyEotrfm+E71IJbSJdoGKQGrMouDfki2c4a59/YM0JA3gYKvzTiN5pyFXSuS1OS0" +
            "Bp7AGE8KlMv1/oE0k/PeGCByN4soVA==";

    protected static ElectricaEmulator emulator;

    @BeforeAll
    static void setUp() {
        MapConnectorProperties connectorProperties = MapConnectorProperties.builder()
                .addInteger(SlackChannelV1ExecutorFactory.MAX_IDLE_CONNECTIONS_PROPERTY, 1)
                .addInteger(SlackChannelV1ExecutorFactory.KEEP_ALIVE_DURATION_MIN_PROPERTY, 1)
                .build();

        emulator = ElectricaEmulator.builder(SlackChannelV1ExecutorFactory.class)
                .connectorProperties(connectorProperties)
                .build();
    }
}

