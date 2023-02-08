package de.metallist.backend.utilities;

import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.testng.Assert.*;

public class EncryptionUtilTest {

    private final String password = "supersecret";

    private GCMParameterSpec gcmParameterSpec;

    @Test
    public void test_00_generateKeyFromPassword() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        SecretKey returnedKey = null;

        try {
            returnedKey = EncryptionUtil.generateKeyFromPassword(password, salt);
        } catch (Exception e) {
            fail();
        }
        assertNotEquals(returnedKey.getEncoded().length, 0);
    }

    @Test
    public void test_01_generateGCMSpecs() {
        gcmParameterSpec = EncryptionUtil.generateGCMSpecs();
        assertNotNull(gcmParameterSpec);
        assertEquals(gcmParameterSpec.getTLen(), 128);
        assertNotEquals(gcmParameterSpec.getIV().length, 0);
    }

    @Test
    public void test_02_regenerateGCMSpecs() {
        GCMParameterSpec newGcmParameterSpec;

        newGcmParameterSpec = EncryptionUtil.regenerateGCMSpecs(gcmParameterSpec.getIV());
        assertNotNull(newGcmParameterSpec);
        assertEquals(newGcmParameterSpec.getTLen(), gcmParameterSpec.getTLen());
        assertEquals(newGcmParameterSpec.getIV(), gcmParameterSpec.getIV());

        byte[] falseIV = new byte[16];
        new SecureRandom().nextBytes(falseIV);
        newGcmParameterSpec = EncryptionUtil.regenerateGCMSpecs(falseIV);
        assertNotNull(newGcmParameterSpec);
        assertNotEquals(newGcmParameterSpec.getIV(), gcmParameterSpec.getIV());
    }

    @Test
    public void test_03_encryptDecrypt() {
        String plaintext = "This is some test string.";
        byte[] encryptionOutput = "".getBytes();
        String encryptedText = "";
        String decryptedText = "";

        try {
            encryptionOutput = EncryptionUtil.encrypt(plaintext, password);
            encryptedText = Arrays.toString(encryptionOutput);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotEquals(encryptedText, "");

        try {
            decryptedText = EncryptionUtil.decrypt(encryptionOutput, password);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertNotEquals(decryptedText, "");
        assertEquals(decryptedText, plaintext);
    }
}