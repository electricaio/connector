package io.electrica.connector.test.security;

import io.electrica.connector.spi.context.BasicAuthorization;
import io.electrica.connector.spi.context.IbmAuthorization;
import io.electrica.connector.spi.context.TokenAuthorization;

/**
 * Class to gather all factory methods to create {@link SecuredAuthorization}.
 */
public class SecuredAuthorizations {

    private static final String SEPARATOR = "\n";

    private SecuredAuthorizations() {
    }

    /**
     * Use it to create encrypted credentials for Basic authorization type.
     */
    public static String encryptBasicCredentials(String username, String password) {
        return SecurityHelper.encrypt(username + SEPARATOR + password);
    }

    /**
     * Factory method to create Basic authorization.
     *
     * @see #encryptBasicCredentials(String, String)
     */
    public static SecuredAuthorization basic(String encryptedCredentials) {
        return () -> {
            String decrypted = SecurityHelper.decrypt(encryptedCredentials);
            String[] split = decrypted.split(SEPARATOR);
            String username = split[0];
            String password = split[1];
            return new BasicAuthorization(username, password);
        };
    }

    /**
     * Factory method to create Basic authorization from raw username and password.
     * <p>
     * Note: use it only to run test on developer machine and never commit not encrypted
     * credentials to public Github repo.
     *
     * @deprecated replace with {@link #basic(String)} before commit integration test to public repo
     */
    @Deprecated
    public static SecuredAuthorization developerBasic(String username, String password) {
        return () -> new BasicAuthorization(username, password);
    }

    /**
     * Use it to create encrypted credentials for Token authorization type.
     */
    public static String encryptTokenCredentials(String token) {
        return SecurityHelper.encrypt(token);
    }

    /**
     * Factory method to create Token authorization.
     *
     * @see #encryptTokenCredentials(String)
     */
    public static SecuredAuthorization token(String encryptedCredentials) {
        return () -> {
            String token = SecurityHelper.decrypt(encryptedCredentials);
            return new TokenAuthorization(token);
        };
    }

    /**
     * Factory method to create Token authorization from raw token.
     * <p>
     * Note: use it only to run test on developer machine and never commit not encrypted
     * credentials to public Github repo.
     *
     * @deprecated replace with {@link #token(String)} before commit integration test to public repo
     */
    @Deprecated
    public static SecuredAuthorization developerToken(String token) {
        return () -> new TokenAuthorization(token);
    }

    /**
     * Use it to create encrypted credentials for Ibm authorization type.
     */
    public static String encryptIbmCredentials(String integrationId, String clientId) {
        return SecurityHelper.encrypt(integrationId + SEPARATOR + clientId);
    }

    /**
     * Factory method to create Ibm authorization.
     *
     * @see #encryptIbmCredentials(String, String)
     */
    public static SecuredAuthorization ibm(String encryptedCredentials) {
        return () -> {
            String decrypted = SecurityHelper.decrypt(encryptedCredentials);
            String[] split = decrypted.split(SEPARATOR);
            String integrationId = split[0];
            String clientId = split[1];
            return new IbmAuthorization(integrationId, clientId);
        };
    }

    /**
     * Factory method to create Ibm authorization from raw integrationId and clientId.
     * <p>
     * Note: use it only to run test on developer machine and never commit not encrypted
     * credentials to public Github repo.
     *
     * @deprecated replace with {@link #ibm(String)} before commit integration test to public repo
     */
    @Deprecated
    public static SecuredAuthorization developerIbm(String integrationId, String clientId) {
        return () -> new IbmAuthorization(integrationId, clientId);
    }

}
