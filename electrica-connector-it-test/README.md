## About
This module provide following capabilities:
- allow to mock Electrica services during integration development and start your script locally;
- write integration tests for your script (will be started as part of Electrica SDK nightly build).

## Usage
- First of all add following dependency to your integration module:
```groovy
testCompile project(':electrica-connector-it-test')
```
- Create test class for your implementation of `ConnectorExecutorFactory` (e.q `ChannelV2ExecutorFactoryTest`)
- invoke emulator and check result
```java
MapConnectorProperties connectorProperties = MapConnectorProperties.builder()
        .addInteger(ChannelV2ExecutorFactory.MAX_IDLE_CONNECTIONS_PROPERTY, 1)
        .build();

ElectricaEmulator emulator = ElectricaEmulator.builder(ChannelV2ExecutorFactory.class)
        .connectorProperties(connectorProperties)
        .build();

Object result = emulator.runIntegration(InvocationContext.builder(ChannelV2Action.MESSAGE)
                .authorization(SecuredAuthorizations.token("token"))
                .payload(new ChannelV2SendMessagePayload().message("Integration test message"))
                .build()
        );
```

## Security
Special security layer implemented to allow users safely commit test account credentials for integration 
tests into Github public repository.

There is `SecuredAuthorization` provides two ways to specify integration script credentials:
1. directly to start integration script test on developer machine;
2. encrypted using Electrica public key to commit into the repo.

Please note that for security reasons we strongly recommend commit only encrypted credentials while using 
raw for development.

To encrypt your credentials, please invoke:
```java
SecuredAuthorizations.encryptBasicCredentials("username", "password");
```

And then commit it as following:
```java
SecuredAuthorizations.basic("<encrypted credentials from previous step>")
```

See `SecuredAuthorizations` for more details.

## Generate keypair
```bash
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -outform DER -pubout -out public.der
openssl pkcs8 -topk8 -nocrypt -in keypair.pem -outform DER -out private.der
```
