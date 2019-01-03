package io.electrica.connector.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.TextNode;
import io.electrica.connector.spi.context.Authorization;
import io.electrica.connector.spi.context.BasicAuthorization;
import io.electrica.connector.spi.context.ExecutionContext;
import io.electrica.connector.spi.context.TokenAuthorization;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.spi.service.Logger;

import static io.electrica.connector.spi.Validations.check;
import static io.electrica.connector.spi.Validations.required;

public interface ServiceFacade {

    String REQUIRED_PARAMETERS_ERROR_MESSAGE = "Parameters required";
    String REQUIRED_PAYLOAD_ERROR_MESSAGE = "Payload required";
    String REQUIRED_ACTION_ERROR_MESSAGE = "Action required";
    String REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE = "Token authorization required";
    String REQUIRED_BASIC_AUTHORIZATION_ERROR_MESSAGE = "Basic authorization required";

    Logger getLogger();

    ExecutionContext getContext();

    ObjectReader getObjectReader();

    default <T> T readParameters(Class<T> type) throws IntegrationException {
        try {
            JsonNode parameters = required(getContext().getParameters(), REQUIRED_PARAMETERS_ERROR_MESSAGE);
            return getObjectReader().treeToValue(parameters, type);
        } catch (JsonProcessingException e) {
            throw Exceptions.deserialization("Can't parse parameters", e);
        }
    }

    default <T> T readPayload(Class<T> type) throws IntegrationException {
        try {
            JsonNode payload = required(getContext().getPayload(), REQUIRED_PAYLOAD_ERROR_MESSAGE);
            return getObjectReader().treeToValue(payload, type);
        } catch (JsonProcessingException e) {
            throw Exceptions.deserialization("Can't parse payload", e);
        }
    }

    default <E extends Enum<E>> E readAction(Class<E> type) throws IntegrationException {
        try {
            String action = required(getContext().getAction(), REQUIRED_ACTION_ERROR_MESSAGE);
            E result = getObjectReader().treeToValue(TextNode.valueOf(action), type);
            return required(result, "Unknown action: %s", action);
        } catch (JsonProcessingException e) {
            throw Exceptions.deserialization("Can't parse action", e);
        }
    }

    default TokenAuthorization getTokenAuthorization() throws IntegrationException {
        Authorization authorization = getContext().getAuthorization();
        check(authorization instanceof TokenAuthorization, REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE);
        return (TokenAuthorization) authorization;
    }

    default BasicAuthorization getBasicAuthorization() throws IntegrationException {
        Authorization authorization = getContext().getAuthorization();
        check(authorization instanceof BasicAuthorization, REQUIRED_BASIC_AUTHORIZATION_ERROR_MESSAGE);
        return (BasicAuthorization) authorization;
    }

}
