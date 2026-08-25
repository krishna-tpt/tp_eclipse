package xmlgenerate;

import java.security.SecureRandom;
import java.util.Set;

/**
 * Utility to generate unique ids that visually match the existing
 * Salesforce-style ids in the payload (e.g. 0WOSc00000LFlwzOAD).
 *
 * NOTE: This generates a random 18-char id with the given 3-char prefix.
 * It does NOT compute the real Salesforce case-sensitive checksum (the
 * last 3 chars of a real SF id). It only guarantees the id is unique
 * within the current run (tracked via the `used` set). If the receiving
 * system validates that checksum, tell me and I will wire in the real
 * algorithm instead of random generation.
 */
public final class XmlIdUtil {

    private static final String ALPHANUM =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private XmlIdUtil() {
    }

    /** Generates an id: prefix + random alphanumeric chars, total length 18. */
    public static String generateId(String prefix, Set<String> used) {
        String id;
        do {
            StringBuilder sb = new StringBuilder(prefix);
            int remaining = 18 - prefix.length();
            for (int i = 0; i < remaining; i++) {
                sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
            }
            id = sb.toString();
        } while (!used.add(id));
        return id;
    }
}
