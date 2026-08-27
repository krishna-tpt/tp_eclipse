package org.michelin.filemanager.tools.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.michelin.filemanager.tools.GenerateOpeningBalance;
import org.michelin.filemanager.tools.config.ToolsConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateOpeningBalanceTest {

    private ToolsConfig configFor(Path outDir, long seed, int rows) {
        return new ToolsConfig(
            seed,
            new ToolsConfig.DbConfig("jdbc:postgresql://localhost:5432/x", "u", "p"),
            new ToolsConfig.Tenants(2),
            new ToolsConfig.MasterData(5, 2, 1, 2),
            new ToolsConfig.OpeningBalance(rows, outDir.toString(), "ob.csv", ";", "2026-05-18"),
            new ToolsConfig.Transactions(0, 0.6, 0.1, true),
            new ToolsConfig.Orders(0, 1, 3, "open")
        );
    }

    @Test
    @DisplayName("TC-600: emits CSV with the prod header and configured row count")
    void emitsCsvWithProdHeader(@TempDir Path tmp) throws Exception {
        ToolsConfig cfg = configFor(tmp, 42L, 10);

        Path output = new GenerateOpeningBalance(cfg).run();

        List<String> lines = Files.readAllLines(output);
        assertThat(lines).hasSize(11); // header + 10 rows
        assertThat(lines.get(0)).isEqualTo(
            "tenant_code;product_code;warehouse_code;lot_code;uom_code;qty;as_of_date;source_ref"
        );
    }

    @Test
    @DisplayName("TC-601: same seed → identical output byte-for-byte")
    void sameSeedProducesIdenticalOutput(@TempDir Path tmp) throws Exception {
        Path a = tmp.resolve("a"); Files.createDirectories(a);
        Path b = tmp.resolve("b"); Files.createDirectories(b);

        Path outA = new GenerateOpeningBalance(configFor(a, 42L, 25)).run();
        Path outB = new GenerateOpeningBalance(configFor(b, 42L, 25)).run();

        byte[] bytesA = Files.readAllBytes(outA);
        byte[] bytesB = Files.readAllBytes(outB);

        assertThat(bytesA).isEqualTo(bytesB);
    }

    @Test
    @DisplayName("Different seed → different output")
    void differentSeedProducesDifferentOutput(@TempDir Path tmp) throws Exception {
        Path a = tmp.resolve("a"); Files.createDirectories(a);
        Path b = tmp.resolve("b"); Files.createDirectories(b);

        Path outA = new GenerateOpeningBalance(configFor(a, 42L, 50)).run();
        Path outB = new GenerateOpeningBalance(configFor(b, 99L, 50)).run();

        byte[] bytesA = Files.readAllBytes(outA);
        byte[] bytesB = Files.readAllBytes(outB);

        assertThat(bytesA).isNotEqualTo(bytesB);
    }
}
