package chatapplication_latest.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Simple SHA-256 password hashing utility.
 * In production replace with BCrypt or Argon2.
 */
public final class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String plaintext) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            String hashed=HexFormat.of().formatHex(encoded);
            System.out.println("Hashed : "+hashed);
            return hashed;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean verify(String plaintext, String hash) {
        return hash(plaintext).equals(hash);
    }
}
