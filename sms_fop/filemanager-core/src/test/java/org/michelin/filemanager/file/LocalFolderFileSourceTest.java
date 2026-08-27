package org.michelin.filemanager.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TC-220..TC-221 — LocalFolderFileSource list/move behavior.
 */
class LocalFolderFileSourceTest {

    private LocalFolderFileSource source(Path root) {
        Path pickup  = root.resolve("inbound");
        Path archive = root.resolve("archive");
        Path reject  = root.resolve("rejected");
        try {
            Files.createDirectories(pickup);
            Files.createDirectories(archive);
            Files.createDirectories(reject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new LocalFolderFileSource(
            pickup, archive, reject,
            Pattern.compile("^opening_balance_\\d{8}\\.csv$"));
    }

    @Test
    @DisplayName("TC-220: list returns files matching name pattern")
    void list_returnsMatchingFiles(@TempDir Path tmp) throws Exception {
        LocalFolderFileSource src = source(tmp);
        Files.writeString(tmp.resolve("inbound/opening_balance_20260518.csv"), "header\n");
        Files.writeString(tmp.resolve("inbound/ignore_me.txt"),                 "x\n");

        List<SourceFile> files = src.list();

        assertThat(files).extracting(SourceFile::name)
            .containsExactly("opening_balance_20260518.csv");
    }

    @Test
    @DisplayName("TC-221: moveToArchive places file in archive folder with new name")
    void moveToArchive_movesFile(@TempDir Path tmp) throws Exception {
        LocalFolderFileSource src = source(tmp);
        Path file = tmp.resolve("inbound/opening_balance_20260518.csv");
        Files.writeString(file, "h\n");
        SourceFile sf = src.list().getFirst();

        src.moveToArchive(sf, "20260518_120000_opening_balance_20260518.csv");

        assertThat(Files.exists(file)).isFalse();
        assertThat(Files.exists(tmp.resolve("archive/20260518_120000_opening_balance_20260518.csv"))).isTrue();
    }
}
