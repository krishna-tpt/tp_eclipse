package org.michelin.filemanager.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigValidationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-230..TC-235 — FilesComFileSource behavior, with the SDK seamed out via
 * {@link FakeFilesComGateway}.
 *
 * <p>What we deliberately don't test here:
 * <ul>
 *   <li>SDK static state ({@code FilesClient.apiKey}) — covered by
 *       {@link FilesComSdkGateway} construction at wiring time, not by this class.</li>
 *   <li>Live HTTPS — pushed behind an env-gated integration test (next iteration).</li>
 * </ul>
 */
class FilesComFileSourceTest {

    private static final Pattern PATTERN =
            Pattern.compile("^opening_balance_\\d{8}\\.csv$");

    private static Config.FileConfig.FilesComConfig cfg() {
        return new Config.FileConfig.FilesComConfig(
                "test-key", "", "/inbox", "/archive", "/reject",
                10_000, 60_000, 200);
    }

    @Test
    @DisplayName("TC-230: list returns only files matching name pattern, folders excluded")
    void list_filtersByPatternAndExcludesFolders() throws Exception {
        FakeFilesComGateway g = new FakeFilesComGateway();
        g.seedFile("/inbox/opening_balance_20260601.csv", "h\n");
        g.seedFile("/inbox/opening_balance_20260602.csv", "h\n");
        g.seedFile("/inbox/notes.txt",                    "x\n");
        g.seedFolder("/inbox/subdir");

        FilesComFileSource src = new FilesComFileSource(g, cfg(), PATTERN);

        List<SourceFile> files = src.list();

        assertThat(files).extracting(SourceFile::name).containsExactly(
                "opening_balance_20260601.csv",
                "opening_balance_20260602.csv");
        assertThat(g.listCalls).isEqualTo(1);
    }

    @Test
    @DisplayName("TC-231: SourceFile.id carries the full remote path so download can find it")
    void list_idIsFullRemotePath() throws Exception {
        FakeFilesComGateway g = new FakeFilesComGateway();
        g.seedFile("/inbox/opening_balance_20260601.csv", "h\n");

        FilesComFileSource src = new FilesComFileSource(g, cfg(), PATTERN);

        SourceFile only = src.list().getFirst();
        assertThat(only.id()).isEqualTo("/inbox/opening_balance_20260601.csv");
        assertThat(only.name()).isEqualTo("opening_balance_20260601.csv");
        assertThat(only.sizeBytes()).isEqualTo(2L);
    }

    @Test
    @DisplayName("TC-232: download writes remote bytes to the local dest path")
    void download_writesBytesToDest(@TempDir Path tmp) throws Exception {
        FakeFilesComGateway g = new FakeFilesComGateway();
        g.seedFile("/inbox/opening_balance_20260601.csv", "hello\n");

        FilesComFileSource src = new FilesComFileSource(g, cfg(), PATTERN);
        SourceFile sf = src.list().getFirst();
        Path dest = tmp.resolve("local/opening_balance_20260601.csv");

        src.download(sf, dest);

        assertThat(Files.readString(dest)).isEqualTo("hello\n");
        assertThat(g.downloadCalls).isEqualTo(1);
    }

    @Test
    @DisplayName("TC-233: moveToArchive sends the file to archive_path with the new name")
    void moveToArchive_movesToArchivePath() throws Exception {
        FakeFilesComGateway g = new FakeFilesComGateway();
        g.seedFile("/inbox/opening_balance_20260601.csv", "h\n");
        FilesComFileSource src = new FilesComFileSource(g, cfg(), PATTERN);
        SourceFile sf = src.list().getFirst();

        src.moveToArchive(sf, "20260601_120000_opening_balance_20260601.csv");

        assertThat(g.exists("/inbox/opening_balance_20260601.csv")).isFalse();
        assertThat(g.exists("/archive/20260601_120000_opening_balance_20260601.csv")).isTrue();
        assertThat(g.moveCalls).isEqualTo(1);
    }

    @Test
    @DisplayName("TC-234: moveToReject sends the file to reject_path with the new name")
    void moveToReject_movesToRejectPath() throws Exception {
        FakeFilesComGateway g = new FakeFilesComGateway();
        g.seedFile("/inbox/opening_balance_20260601.csv", "h\n");
        FilesComFileSource src = new FilesComFileSource(g, cfg(), PATTERN);
        SourceFile sf = src.list().getFirst();

        src.moveToReject(sf, "rejected_20260601.csv");

        assertThat(g.exists("/reject/rejected_20260601.csv")).isTrue();
    }

    @Test
    @DisplayName("TC-235: missing pickup_path fails fast at construction")
    void blankPickupPath_failsConstruction() {
        FakeFilesComGateway g = new FakeFilesComGateway();
        Config.FileConfig.FilesComConfig bad = new Config.FileConfig.FilesComConfig(
                "test-key", "", "", "/archive", "/reject", 10_000, 60_000, 200);

        assertThatThrownBy(() -> new FilesComFileSource(g, bad, PATTERN))
                .isInstanceOf(ConfigValidationException.class)
                .hasMessageContaining("file.filescom.pickup_path");
    }
}
