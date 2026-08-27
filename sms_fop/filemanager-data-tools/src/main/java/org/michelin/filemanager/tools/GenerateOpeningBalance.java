package org.michelin.filemanager.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.tools.config.ToolsConfig;
import org.michelin.filemanager.tools.config.ToolsConfigLoader;
import org.michelin.filemanager.tools.util.Rng;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a synthetic opening-balance CSV in the prod format.
 * Deterministic given (seed, tenants.count, master_data.*, opening_balance.rows).
 * Header: tenant_code;product_code;warehouse_code;lot_code;uom_code;qty;as_of_date;source_ref
 */
public class GenerateOpeningBalance {

    private static final Logger log = LoggerFactory.getLogger(GenerateOpeningBalance.class);

    public static final String HEADER =
            "tenant_code;product_code;warehouse_code;lot_code;uom_code;qty;as_of_date;source_ref";

    private final ToolsConfig cfg;

    public GenerateOpeningBalance(ToolsConfig cfg) {
        this.cfg = cfg;
    }

    public static void main(String[] args) throws Exception {
        ToolsConfig cfg = new ToolsConfigLoader().load(System.getenv());
        Path out = new GenerateOpeningBalance(cfg).run();
        log.info("wrote {} ({} rows)", out, cfg.openingBalance().rows());
    }

    public Path run() throws Exception {
        Rng rng = new Rng(cfg.seed());

        List<String> tenantCodes    = generateTenantCodes(cfg.tenants().count());
        List<String> productCodes   = generateProductCodes(cfg.masterData().productsPerTenant());
        List<String> warehouseCodes = generateWarehouseCodes(cfg.masterData().warehousesPerTenant());
        List<String> uomCodes       = generateUomCodes(cfg.masterData().uomsPerTenant());
        List<String> lotCodes       = generateLotCodes(cfg.masterData().lotsPerProduct());

        String date = "today".equalsIgnoreCase(cfg.openingBalance().date())
                ? LocalDate.now().toString()
                : cfg.openingBalance().date();

        Path outDir = Paths.get(cfg.openingBalance().outputDir());
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve(cfg.openingBalance().fileName());

        String delim = cfg.openingBalance().csvDelimiter();
        if (delim.length() != 1) {
            throw new IllegalArgumentException("csv_delimiter must be exactly 1 character; got: " + delim);
        }

        try (BufferedWriter w = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8)) {
            w.write(rebuildHeader(delim));
            w.newLine();
            for (int i = 0; i < cfg.openingBalance().rows(); i++) {
                String tenant    = rng.pick(tenantCodes);
                String product   = rng.pick(productCodes);
                String warehouse = rng.pick(warehouseCodes);
                String uom       = rng.pick(uomCodes);
                String lot       = lotCodes.isEmpty() ? "" : (rng.nextDouble() < 0.5 ? "" : rng.pick(lotCodes));
                double qty       = round4(1 + rng.nextDouble() * 999);

                w.write(String.join(delim,
                        tenant, product, warehouse, lot, uom,
                        String.format(java.util.Locale.ROOT, "%.4f", qty),
                        date, "opening_balance_synthetic"));
                w.newLine();
            }
        }
        return outFile;
    }

    private String rebuildHeader(String delim) {
        return HEADER.replace(";", delim);
    }

    private static double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    private static List<String> generateTenantCodes(int count) {
        // Stable, deterministic codes. Matches typical seed in master_data/seed.sql.
        List<String> defaults = List.of("ACME", "BETA", "GAMMA", "DELTA");
        List<String> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            out.add(i < defaults.size() ? defaults.get(i) : "TEN-" + (i + 1));
        }
        return out;
    }

    private static List<String> generateProductCodes(int count) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < count; i++) out.add(String.format("P%04d", 1001 + i));
        return out;
    }

    private static List<String> generateWarehouseCodes(int count) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < count; i++) out.add(String.format("WH-NL-%02d", i + 1));
        return out;
    }

    private static List<String> generateUomCodes(int count) {
        List<String> defaults = List.of("EA", "KG", "L", "BOX", "PALLET");
        List<String> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            out.add(i < defaults.size() ? defaults.get(i) : "U" + (i + 1));
        }
        return out;
    }

    private static List<String> generateLotCodes(int count) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < count; i++) out.add("LOT-" + (char) ('A' + i));
        return out;
    }
}
