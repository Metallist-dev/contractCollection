package de.metallist.backend.utilities;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * implements an AES-based encryption and decryption
 *
 * @author Metallist-dev
 * @version 0.1
 */
@Slf4j
public abstract class EncryptionUtil {
    private static final int KEYSIZE = 256;
    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";


    public static SecretKey generateBasicKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEYSIZE);
        return keyGenerator.generateKey();
    }

    public static SecretKey generateKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static boolean compareKeys(String providedPassword, byte[] salt, byte[] actualKey) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(providedPassword.toCharArray(), salt, 65536, 256);
            SecretKeySpec providedKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            log.debug("Password: " + providedPassword);
            log.debug("salt: " + Arrays.toString(salt));
            log.debug("Compare byte arrays:");
            log.debug("input:  " + Arrays.toString(providedKey.getEncoded()));
            log.debug("actual: " + Arrays.toString(actualKey));
            if (Arrays.equals(providedKey.getEncoded(), actualKey)) return true;
        } catch (Exception e) {
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String ciphertext, SecretKey key, IvParameterSpec iv)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(plainText);
    }
}

// https://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption
// https://www.baeldung.com/java-aes-encryption-decryption