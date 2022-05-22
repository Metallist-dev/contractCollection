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
    private static final int ITERATION_COUNT = 65536;
    private static final String KEY_ALGO_LONG = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGO_SHORT = "AES";
    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";

    /**
     * generates a new key derived from a provided password using AES encryption algorithm
     * @param password provided plaintext password
     * @param salt     randomized byte sequence, so that no generated key equals another
     * @return         secret key
     */
    public static SecretKey generateKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGO_LONG);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEYSIZE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), KEY_ALGO_SHORT);
    }

    /**
     * compares a provided key with th key saved in the regarding file to unlock
     * @param password  provided plaintext password
     * @param salt      randomized byte sequence, obtained from file to unlock
     * @param actualKey key hash obtained from file to unlock
     * @return          true, if keys match; false, if keys do not match
     */
    public static boolean compareKeys(String password, byte[] salt, byte[] actualKey) {
        log.info("Comparing provided key with saved key.");
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGO_LONG);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEYSIZE);
            SecretKeySpec providedKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), KEY_ALGO_SHORT);

            if (Arrays.equals(providedKey.getEncoded(), actualKey)) return true;
        } catch (Exception e) {
            log.debug(e.getMessage());
            log.debug(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    /**
     * generates a randomized initial vector, so that no cipher equals another even with the exact same input
     * @return  the vector parameters
     */
    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * encrypts a given plaintext with both a provided key and IV
     * @param input plaintext to encrypt
     * @param key   generated key
     * @param iv    generated IV
     * @return      ciphertext
     */
    public static String encrypt(String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * decrypts a given ciphertext using a provided key and IV
     * @param ciphertext encrypted text to decrypt
     * @param key        provided key (saved with file)
     * @param iv         provided IV (saved with file)
     * @return           plaintext
     */
    public static String decrypt(String ciphertext, SecretKey key, IvParameterSpec iv)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(plainText);
    }
}

// sources / inspiration:
// https://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption
// https://www.baeldung.com/java-aes-encryption-decryption