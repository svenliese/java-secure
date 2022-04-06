package de.sl.secure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AESTest {

    @Test
    void shouldEncryptDecrypt() {
        try {
            final AES aes = new AES("mypass");
            final String origin = "das ist meine Nachricht\nalles klar\n";
            final String encrypted = aes.encrypt(origin);
            Assertions.assertNotEquals(origin, encrypted);
            final String decrypted = aes.decrypt(encrypted);
            Assertions.assertEquals(origin, decrypted);
        } catch(Exception ex) {
            Assertions.fail(ex);
        }
    }
}
