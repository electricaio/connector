package io.electrica.connector.test.security;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

class SecurityHelper {

    private static final String ALGORITHM = "RSA";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";

    private static final String DEFAULT_PUBLIC_KEY_PATH = "/public.der";
    private static final String PUBLIC_KEY_PATH_ENV = "ELECTRICA_INTEGRATION_IT_TEST_PUBLIC_KEY_PATH";

    private static final String DEFAULT_PRIVATE_KEY_PATH = "../private.der";
    private static final String PRIVATE_KEY_PATH_ENV = "ELECTRICA_INTEGRATION_IT_TEST_PRIVATE_KEY_PATH";

    private static final AtomicReference<PublicKey> PUBLIC_KEY = new AtomicReference<>();
    private static final AtomicReference<PrivateKey> PRIVATE_KEY = new AtomicReference<>();

    private SecurityHelper() {
    }

    @SneakyThrows
    static String encrypt(String data) {
        PublicKey publicKey = PUBLIC_KEY.get();
        if (publicKey == null) {
            publicKey = readPublicKey();
            PUBLIC_KEY.set(publicKey);
        }

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encoded = cipher.doFinal(data.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(encoded);
    }

    @SneakyThrows
    static String decrypt(String data) {
        PrivateKey privateKey = PRIVATE_KEY.get();
        if (privateKey == null) {
            privateKey = readPrivateKey();
            PRIVATE_KEY.set(privateKey);
        }

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.getDecoder().decode(data);
        return new String(cipher.doFinal(dataBytes), UTF_8);
    }

    private static PublicKey readPublicKey() throws Exception {
        String publicKeyPath = System.getenv(PUBLIC_KEY_PATH_ENV);
        byte[] publicKey = publicKeyPath == null ?
                IOUtils.toByteArray(SecurityHelper.class.getResourceAsStream(DEFAULT_PUBLIC_KEY_PATH)) :
                Files.readAllBytes(Paths.get(publicKeyPath));
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(publicSpec);
    }

    private static PrivateKey readPrivateKey() throws Exception {
        String privateKeyPath = System.getenv(PRIVATE_KEY_PATH_ENV);
        if (privateKeyPath == null) {
            privateKeyPath = DEFAULT_PRIVATE_KEY_PATH;
        }
        try {
            byte[] privateKey = Files.readAllBytes(Paths.get(privateKeyPath));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchFileException | FileNotFoundException e) {
            throw new IllegalStateException("Integration test has been started manually. " +
                    "Please consider use not secured authorization for developer machine test purposes. " +
                    "See `it-test/README.md` for more details.", e);
        }
    }

}
