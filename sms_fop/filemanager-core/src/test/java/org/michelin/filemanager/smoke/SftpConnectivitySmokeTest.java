package org.michelin.filemanager.smoke;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.file.SftpFileSource;
import org.michelin.filemanager.file.SourceFile;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-260 — Live connectivity smoke test against the customer's SFTP endpoint.
 *
 * <p>Runs ONLY when {@code SFTP_HOST} is set. The companion files.com smoke
 * test uses the API key as its gate; this one uses the host so the two are
 * independently selectable.
 *
 * <p>Reads:
 * <ul>
 *   <li>{@code SFTP_HOST}              — required (the gate)</li>
 *   <li>{@code SFTP_PORT}              — optional, defaults to 22</li>
 *   <li>{@code SFTP_USER}              — required</li>
 *   <li>{@code SFTP_PRIVATE_KEY_PATH}  — required (key auth only — this smoke does not try password)</li>
 *   <li>{@code SFTP_KNOWN_HOSTS_PATH}  — strongly recommended (production posture)</li>
 *   <li>{@code SFTP_PICKUP_PATH}       — required; defaults to {@code /EU/DEV/BR/C10/Inbound}</li>
 *   <li>{@code SFTP_ARCHIVE_PATH} / {@code SFTP_REJECT_PATH} — required by construction but NOT
 *       exercised here. Defaults set so this test is read-only.</li>
 * </ul>
 *
 * <p>The single assertion is "the list call returned without throwing." An
 * empty folder is still a pass — auth + path resolution both worked.
 *
 * <p>Run alone:
 * <pre>
 *   source .env
 *   mvn -pl inventoryledger-core -Dtest=SftpConnectivitySmokeTest test
 * </pre>
 */
@EnabledIfEnvironmentVariable(named = "SFTP_HOST", matches = ".+")
class SftpConnectivitySmokeTest {

    @Test
    @DisplayName("TC-260: SFTP auth + list folder works with the configured key")
    void listsConfiguredFolder_withRealKey() throws Exception {
        String host    = System.getenv("SFTP_HOST");
        int    port    = parseInt(System.getenv("SFTP_PORT"), 22);
        String user    = require("SFTP_USER");
        String keyPath = require("SFTP_PRIVATE_KEY_PATH");
        String khPath  = orBlank(System.getenv("SFTP_KNOWN_HOSTS_PATH"));
        String pickup  = orDefault(System.getenv("SFTP_PICKUP_PATH"),  "/EU/DEV/BR/C10/Inbound");
        String archive = orDefault(System.getenv("SFTP_ARCHIVE_PATH"), "/EU/DEV/BR/C10/Archive");
        String reject  = orDefault(System.getenv("SFTP_REJECT_PATH"),  "/EU/DEV/BR/C10/Error");

        Config.FileConfig.SftpConfig cfg = new Config.FileConfig.SftpConfig(
                host, port, user,
                "",                   // no password — key auth only
                keyPath, "", khPath,
                pickup, archive, reject,
                15_000);

        SftpFileSource src = new SftpFileSource(cfg, Pattern.compile(".*"));

        System.out.println("---- sftp smoke ----");
        System.out.println("host    = " + host + ":" + port);
        System.out.println("user    = " + user);
        System.out.println("path    = " + pickup);
        System.out.println("hostkey = " + (khPath.isBlank() ? "<unpinned!>" : khPath));

        List<SourceFile> files = src.list();

        System.out.println("returned " + files.size() + " entries:");
        for (SourceFile f : files) {
            System.out.printf("  FILE %10d  %s%n", f.sizeBytes(), f.id());
        }
        System.out.println("---- ok ----");

        assertThat(files).isNotNull();
    }

    private static String orBlank(String s)             { return s == null ? "" : s; }
    private static String orDefault(String s, String d) { return (s == null || s.isBlank()) ? d : s; }
    private static String require(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(name + " must be set in env when SFTP_HOST is set");
        }
        return v;
    }
    private static int parseInt(String s, int dflt) {
        if (s == null || s.isBlank()) return dflt;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return dflt; }
    }
}
