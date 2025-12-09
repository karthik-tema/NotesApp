package com.example.notesapp;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567891234567"; // 16 character key

    private static SecretKeySpec getKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encryptedVal = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptedVal, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return plainText;
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decodedValue = Base64.decode(encryptedText, Base64.DEFAULT);
            byte[] decryptedVal = cipher.doFinal(decodedValue);
            return new String(decryptedVal, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedText;
        }
    }
}
