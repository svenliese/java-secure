package de.sl.secure;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AES {

    private final SecretKeySpec secretKey;

    AES(String pass) throws NoSuchAlgorithmException {
        secretKey = createKey(pass);
    }

    static SecretKeySpec createKey(final String pass) throws NoSuchAlgorithmException {
        byte[] key = pass.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }

    public String encrypt(final String strToEncrypt) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, "iv".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(final String strToDecrypt) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, "iv".getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }
}
