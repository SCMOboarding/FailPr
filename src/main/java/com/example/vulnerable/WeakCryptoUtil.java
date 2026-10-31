package com.example.vulnerable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * VULNERABILITIES: Multiple weak/broken cryptography issues (CWE-327, CWE-328, CWE-330)
 */
public class WeakCryptoUtil {

    // VULNERABILITY: Hardcoded secret key (CWE-321) - HIGH
    private static final String SECRET_KEY    = "MyS3cr3tK3y12345";
    private static final String API_KEY       = "sk-prod-a1b2c3d4e5f6g7h8i9j0";
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    private static final String DB_PASSWORD   = "P@ssw0rd_Prod_2024!";

    // VULNERABILITY: Broken hash - MD5 (CWE-328) - HIGH
    public static String hashPasswordMd5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");  // BROKEN: MD5 is not collision-resistant
        byte[] digest = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(digest);
    }

    // VULNERABILITY: Broken hash - SHA-1 (CWE-328) - HIGH
    public static String hashPasswordSha1(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");  // WEAK: SHA-1 deprecated for passwords
        byte[] digest = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(digest);
    }

    // VULNERABILITY: Weak encryption - DES (CWE-327) - HIGH
    public static byte[] encryptDES(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(
                Arrays.copyOf(SECRET_KEY.getBytes(), 8), "DES");  // DES = 56-bit, broken
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");  // ECB mode is also insecure
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    // VULNERABILITY: AES-ECB mode (CWE-327) - HIGH
    // ECB does not provide semantic security (same plaintext = same ciphertext)
    public static byte[] encryptAesEcb(String data) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey key = kg.generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  // ECB is insecure
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    // VULNERABILITY: Insecure random number generator (CWE-330) - HIGH
    // java.util.Random is not cryptographically secure
    public static String generateToken() {
        Random random = new Random();  // Should use SecureRandom
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }

    // VULNERABILITY: Predictable seed for SecureRandom (CWE-336) - HIGH
    public static byte[] generateIv() {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(12345L);  // Hardcoded seed defeats the purpose of SecureRandom
        byte[] iv = new byte[16];
        sr.nextBytes(iv);
        return iv;
    }
}
