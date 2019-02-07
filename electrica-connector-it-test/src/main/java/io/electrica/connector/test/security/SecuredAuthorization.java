package io.electrica.connector.test.security;

import io.electrica.connector.spi.context.Authorization;

/**
 * Abstraction that allow developers safely commit test account credentials for integration tests into public repo.
 *
 * @see SecuredAuthorizations
 */
public interface SecuredAuthorization {

    Authorization toSimple();
}
