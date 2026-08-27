package org.michelin.filemanager.tools.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-604: Tools sources must never contain hard-coded production secrets.
 * Scans every .java / .yaml file under src/ for forbidden patterns.
 */
class NoHardcodedSecretsTest {

    private static final Path SRC = Paths.get("src");

    // Patterns that strongly indicate a hard-coded secret. False positives are acceptable;
    // false negatives are the failure mode we care about.
    private static final List<Pattern> FORBIDDEN = List.of(
        // AWS access key id
        Pattern.compile("\\bAKIA[0-9A-Z]{16}\\b"),
        // generic password=... or pwd=... with non-placeholder, non-test value
        Pattern.compile("(?i)password\\s*[:=]\\s*\"[^$]{6,}\""),
        Pattern.compile("(?i)pwd\\s*[:=]\\s*\"[^$]{6,}\""),
        // private key block
        Pattern.compile("-----BEGIN (RSA )?PRIVATE KEY-----"),
        // Bearer token literal
        Pattern.compile("(?i)bearer\\s+[A-Za-z0-9._\\-]{32,}")
    );

    @Test
    @DisplayName("TC-604: no hard-coded secrets in tools sources")
    void noHardcodedSecrets() throws IOException {
        List<String> findings = new ArrayList<>();

        try (Stream<Path> files = Files.walk(SRC)) {
            files.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java") || p.toString().endsWith(".yaml"))
                 .forEach(p -> scanFile(p, findings));
        }

        assertThat(findings).as("hard-coded secret patterns found").isEmpty();
    }

    private void scanFile(Path file, List<String> findings) {
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            // Test sources are allowed to use literal test-only secrets when no real value is shipped.
            // The forbidden patterns above are tight enough not to flag short test values.
            for (Pattern p : FORBIDDEN) {
                if (p.matcher(content).find()) {
                    findings.add(file + " matches " + p.pattern());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
