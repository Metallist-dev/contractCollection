package de.metallistdev.contractcollection.application.utilities;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

@Slf4j
public abstract class EncryptionUtil {
    private static final int KEYSIZE = 256;

    @Getter
    private static final int GCM_IV_LENGTH = 12;

    @Getter
    private static final int GCM_TAG_LENGTH = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final String KEY_ALGO_LONG = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGO_SHORT = "AES";
    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";

    /**
     * private constructor
     */
    private EncryptionUtil() {
        throw new IllegalStateException("utility class");
    }

    /**
     * generates a key derived from a provided password using AES encryption algorithm
     * @param password  provided plaintext password
     * @param iv        randomized byte sequence, so that no generated key equals another
     * @return          secret key
     */
    protected static SecretKey generateKeyFromPassword(String password, byte[] iv)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), iv, ITERATION_COUNT, KEYSIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGO_LONG);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), KEY_ALGO_SHORT);
    }

    /**
     * generates a parameter specs for AES in GCM mode including an IV, so that no cipher equals another even with the
     * exact same input
     * (encryption only!)
     * @return  the GCM parameters
     */
    protected static GCMParameterSpec generateGCMSpecs() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
    }

    /**
     * recreates parameter specs for AES in GCM mode based on given IV
     * (decryption only!)
     * @param iv    saved initialization vector for decryption
     * @return      parameter specs for AES-GCM
     */
    protected static GCMParameterSpec regenerateGCMSpecs(byte[] iv) {
        return new GCMParameterSpec(
                GCM_TAG_LENGTH * Byte.SIZE,
                iv);
    }

    /**
     * encrypts a given plaintext using AES-GCM
     * @param input     plaintext to encrypt
     * @param password  plaintext password
     * @return          ciphertext
     */
    public static byte[] encrypt(String input, String password)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        // encryption
        GCMParameterSpec gcmParameterSpec = generateGCMSpecs();
        SecretKey secretKey = generateKeyFromPassword(password, gcmParameterSpec.getIV());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] encrypted = cipher.doFinal(input.getBytes());

        // collect all data needed for decryption
        ByteBuffer byteBuffer = ByteBuffer.allocate(
                4 + gcmParameterSpec.getIV().length + encrypted.length);
        log.debug("output: ivLength  = {}", gcmParameterSpec.getIV().length);
        byteBuffer.putInt(gcmParameterSpec.getIV().length);
        log.debug("output: iv        = {}", Arrays.toString(gcmParameterSpec.getIV()));
        byteBuffer.put(gcmParameterSpec.getIV());
        log.debug("output: encrypted = {}", Arrays.toString(encrypted));
        byteBuffer.put(encrypted);
        return byteBuffer.array();
    }

    /**
     * decrypts a given ciphertext using a provided key and IV
     * @param cipherData byte array containing IV length, IV and ciphertext
     * @return           plaintext
     */
    public static String decrypt(byte[] cipherData, String password)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException,
            IllegalArgumentException {
        // load data
        ByteBuffer byteBuffer = ByteBuffer.wrap(cipherData);

        // fetch IV length
        int ivLength = byteBuffer.getInt();
        if (ivLength < 12 || ivLength >= 16) {
            IllegalArgumentException e = new IllegalArgumentException("IV length is incorrect.");
            log.error(e.getMessage(), e);
            throw e;
        }

        // fetch IV
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);

        // fetch ciphertext
        byte[] ciphertext = new byte[byteBuffer.remaining()];
        byteBuffer.get(ciphertext);

        // actual decryption
        SecretKey secretKey = generateKeyFromPassword(password, iv);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        GCMParameterSpec gcmParameterSpec = regenerateGCMSpecs(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        return new String(cipher.doFinal(ciphertext));
    }
}

/* sources / inspiration:
 * V0.2:
 * https://www.javainterviewpoint.com/java-aes-256-gcm-encryption-and-decryption/
 * https://stackoverflow.com/questions/41321549/tag-mismatch-error-in-aes-256-gcm-decryption-using-java
 * https://stackoverflow.com/questions/53814589/java-aes-gcm-tag-mismatch
 * https://nullbeans.com/how-to-encrypt-decrypt-files-byte-arrays-in-java-using-aes-gcm/
 *
 * V0.1:
 * https://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption
 * https://www.baeldung.com/java-aes-encryption-decryption
 */