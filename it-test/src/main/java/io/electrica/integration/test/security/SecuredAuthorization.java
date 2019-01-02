package io.electrica.integration.test.security;

import io.electrica.integration.spi.context.Authorization;

/**
 * Abstraction that allow developers safely commit test account credentials for integration tests into public repo.
 *
 * @see SecuredAuthorizations
 */
public interface SecuredAuthorization {

    Authorization toSimple();
}
