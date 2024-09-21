package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.test;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {

    private static final int SECRET_LENGTH = 32; // 256-bit key

    public static String generateSecret() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public static void main(String[] args) {
        String secret = generateSecret();
        System.out.println("Generated Secret:[" + secret + "]");
    }
}