package com.taxcalc.util;

/**
 * Simple password hashing using Java's built-in hashCode.
 * (As attempted in the interview — password encrypted using hashcode of TaxPayer object)
 *
 * NOTE: In production always use BCrypt/PBKDF2. This mimics the interview approach.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Hashes a plain-text password combined with the username (salt).
     * Using String.hashCode() as the interview candidate intended.
     */
    public static String hash(String username, String plainPassword) {
        String combined = username + ":" + plainPassword;
        int hash = combined.hashCode();
        return Integer.toHexString(hash);
    }

    public static boolean verify(String username, String plainPassword, String storedHash) {
        return hash(username, plainPassword).equals(storedHash);
    }
}
