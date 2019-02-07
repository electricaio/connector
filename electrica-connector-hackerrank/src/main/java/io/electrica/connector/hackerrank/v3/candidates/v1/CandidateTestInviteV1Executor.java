package io.electrica.connector.hackerrank.v3.candidates.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3TestCandidateInvite;
import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3TestCandidatePayload;
import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3TestInvitationResponse;
import io.electrica.connector.hackerrank.v3.common.v1.HTTPExecutor;
import io.electrica.connector.spi.ConnectorExecutor;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;

public class CandidateTestInviteV1Executor implements ConnectorExecutor {
    private final ServiceFacade facade;
    private final OkHttpClient httpClient;
    private final String url;
    private final ObjectMapper mapper;

    public CandidateTestInviteV1Executor(ServiceFacade facade, OkHttpClient httpClient, ObjectMapper mapper,
                                         String url) {
        this.facade = facade;
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.url = url;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String token = facade.getTokenAuthorization().getToken();
        HackerRankV3TestCandidatePayload candidatePayload = facade.readPayload(HackerRankV3TestCandidatePayload.class);
        String toJson = createCandidateInvite(candidatePayload.getBody());
        String urlWithTestId = String.format(this.url, candidatePayload.getTestId());
        return new HTTPExecutor(httpClient, mapper, urlWithTestId, token).buildPost(toJson)
                .run(HackerRankV3TestInvitationResponse.class);
    }

    private String createCandidateInvite(HackerRankV3TestCandidateInvite payload) throws IntegrationException {
        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw Exceptions.io("Json Processing Exception", e);
        }
    }

}
