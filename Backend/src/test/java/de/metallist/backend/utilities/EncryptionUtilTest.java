package de.metallist.backend.utilities;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.security.SecureRandom;

import static org.testng.Assert.*;

public class EncryptionUtilTest {
    private SecretKey key;

    private IvParameterSpec iv;

    @BeforeClass
    private void setup() {
        this.key = null;
        this.iv = null;
    }

    @Test
    public void test_00_generateBasicKey() {
        SecretKey returnedKey = null;
        try {
            returnedKey = EncryptionUtil.generateBasicKey();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotNull(returnedKey);
    }

    @Test
    public void test_01_generateKeyFromPassword() {
        String password = "supersecret";
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        SecretKey returnedKey = null;

        try {
            returnedKey = EncryptionUtil.generateKeyFromPassword(password, salt);
        } catch (Exception e) {
            fail();
        }

        this.key = returnedKey;
    }

    @Test
    public void test_02_generateIV() {
        iv = EncryptionUtil.generateIV();
        assertNotNull(iv);
    }

    @Test
    public void test_03_encryptDecrypt() {
        if (key == null || iv == null) {
            this.test_01_generateKeyFromPassword();
            this.test_02_generateIV();
        }

        String plaintext = "This is some test string.";
        String encryptedText = "";
        String decryptedText = "";

        try {
            encryptedText = EncryptionUtil.encrypt(plaintext, key, iv);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotEquals(encryptedText, "");

        try {
            decryptedText = EncryptionUtil.decrypt(encryptedText, key, iv);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotEquals(decryptedText, "");
        assertEquals(decryptedText, plaintext);
    }
}