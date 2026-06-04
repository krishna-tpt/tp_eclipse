package chatapplication_latest.util;

import java.util.regex.Pattern;

/**
 * Utility for email format validation.
 */
public final class EmailValidator {

    private EmailValidator() {}

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isValid(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}
