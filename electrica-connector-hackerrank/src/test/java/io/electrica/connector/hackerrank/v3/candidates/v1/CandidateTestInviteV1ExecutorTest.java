package io.electrica.connector.hackerrank.v3.candidates.v1;

import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3CandidatesAction;
import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3TestCandidateInvite;
import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3TestCandidatePayload;
import io.electrica.connector.hackerrank.v3.candidates.v1.model.HackerRankV3TestInvitationResponse;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.ElectricaEmulator;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.electrica.connector.hackerrank.v3.TestConstants.HR_ACCESS_TOKEN;
import static io.electrica.connector.hackerrank.v3.TestConstants.TEST_ID;
import static org.junit.jupiter.api.Assertions.*;

class CandidateTestInviteV1ExecutorTest {

    private ElectricaEmulator emulator = ElectricaEmulator.of(HackerRankCandidatesV1ExecutorFactory.class);

    @Test
    void unsupportedActionTest() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void invitesACandidateToTest() throws IntegrationException {
        HackerRankV3TestCandidateInvite candidateInvite = new HackerRankV3TestCandidateInvite();
        String email = UUID.randomUUID() + "test@electrica.io";
        candidateInvite.email(email).fullName("Electrica Test");
        InvocationContext context = InvocationContext.builder(HackerRankV3CandidatesAction.INVITECANDIDATE)
                .authorization(SecuredAuthorizations.token(HR_ACCESS_TOKEN))
                .payload(new HackerRankV3TestCandidatePayload().testId(TEST_ID).body(candidateInvite))
                .build();

        HackerRankV3TestInvitationResponse response = (HackerRankV3TestInvitationResponse) emulator
                .runIntegration(context);
        assertEquals(response.getEmail(), email);
        assertNotNull(response.getTestLink());
        assertNotNull(response.getId());
    }

}
