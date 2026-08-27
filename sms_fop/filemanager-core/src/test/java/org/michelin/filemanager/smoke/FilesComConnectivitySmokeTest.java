package org.michelin.filemanager.smoke;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.file.FilesComGateway;
import org.michelin.filemanager.file.FilesComSdkGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-240 — Live connectivity smoke test against files.com.
 *
 * <p>Runs ONLY when {@code FILES_COM_API_KEY} is set in the environment. CI without
 * the key skips this class entirely (JUnit's {@link EnabledIfEnvironmentVariable}).
 *
 * <p>Reads:
 * <ul>
 *   <li>{@code FILES_COM_API_KEY}   — required (presence is what gates the test)</li>
 *   <li>{@code FILES_COM_BASE_URL}  — optional; blank means SDK default (app.files.com)</li>
 *   <li>{@code FILES_COM_SMOKE_PATH} — optional; defaults to {@code "/EU/DEV/BR/C10/Inbound"}
 *       (the Michelin tenant's DEV pickup folder — never PRD by default, to keep smoke
 *       runs from pinging production listings)</li>
 * </ul>
 *
 * <p>Prints every entry to {@code System.out} so the operator can see what
 * came back without re-running with extra logging. This is intentional for a
 * smoke test — the assertion only checks the call returned successfully.
 *
 * <p>Run it alone:
 * <pre>
 *   export FILES_COM_API_KEY=fk-live-xxx
 *   # optional: export FILES_COM_BASE_URL=https://acme.files.com
 *   # optional: export FILES_COM_SMOKE_PATH=/michelin/stocklevel/inbox
 *   mvn -pl inventoryledger-core -Dtest=FilesComConnectivitySmokeTest test
 * </pre>
 */
@EnabledIfEnvironmentVariable(named = "FILES_COM_API_KEY", matches = ".+")
class FilesComConnectivitySmokeTest {

    @Test
    @DisplayName("TC-240: files.com auth + list folder works with the configured token")
    void listsConfiguredFolder_withRealToken() throws Exception {
        String apiKey  = System.getenv("FILES_COM_API_KEY");
        String baseUrl = orBlank(System.getenv("FILES_COM_BASE_URL"));
        String path    = orDefault(System.getenv("FILES_COM_SMOKE_PATH"), "/EU/DEV/BR/C10/Inbound");

        Config.FileConfig.FilesComConfig cfg = new Config.FileConfig.FilesComConfig(
                apiKey, baseUrl,
                "", "", "",                    // pickup/archive/reject not exercised here
                10_000, 60_000, 50);

        FilesComGateway gateway = new FilesComSdkGateway(cfg);

        System.out.println("---- files.com smoke ----");
        System.out.println("baseUrl = " + (baseUrl.isBlank() ? "<sdk-default>" : baseUrl));
        System.out.println("path    = " + path);

        List<FilesComGateway.RemoteEntry> entries = gateway.listFolder(path, 50);

        System.out.println("returned " + entries.size() + " entries:");
        for (FilesComGateway.RemoteEntry e : entries) {
            System.out.printf("  %-8s %10d  %s%n",
                    e.isFolder() ? "DIR" : "FILE",
                    e.sizeBytes(),
                    e.path());
        }
        System.out.println("---- ok ----");

        // The call returning without throwing IS the assertion. An empty folder
        // is still success — auth passed and the path resolved.
        assertThat(entries).isNotNull();
    }

    private static String orBlank(String s)             { return s == null ? "" : s; }
    private static String orDefault(String s, String d) { return (s == null || s.isBlank()) ? d : s; }
}
