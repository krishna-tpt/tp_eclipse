package org.michelin.filemanager.tools.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolsConfigLoader {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Z_][A-Z0-9_]*)(?::([^}]*))?}");

    public ToolsConfig load(Map<String, String> env) {
        try (InputStream in = ToolsConfigLoader.class.getResourceAsStream("/tools.yaml")) {
            if (in == null) {
                throw new IllegalStateException("tools.yaml not found on classpath");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) new Yaml().load(in);
            return mapToConfig(resolveTree(data, env));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read tools.yaml", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object resolveTree(Object node, Map<String, String> env) {
        if (node instanceof String s) return resolvePlaceholders(s, env);
        if (node instanceof Map<?, ?> m) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                out.put(String.valueOf(e.getKey()), resolveTree(e.getValue(), env));
            }
            return out;
        }
        if (node instanceof List<?> l) {
            List<Object> out = new ArrayList<>();
            for (Object item : l) out.add(resolveTree(item, env));
            return out;
        }
        return node;
    }

    private String resolvePlaceholders(String input, Map<String, String> env) {
        Matcher m = PLACEHOLDER.matcher(input);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String var = m.group(1);
            String def = m.group(2);
            String value = env.get(var);
            if (value == null || value.isEmpty()) {
                if (def == null) {
                    throw new IllegalStateException("Required env var " + var + " is not set");
                }
                value = def;
            }
            m.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        m.appendTail(out);
        return out.toString();
    }

    @SuppressWarnings("unchecked")
    private ToolsConfig mapToConfig(Object resolved) {
        Map<String, Object> data = (Map<String, Object>) resolved;
        long seed = longOf(data.get("seed"), 0L);

        Map<String, Object> dbMap = (Map<String, Object>) data.getOrDefault("db", Collections.emptyMap());
        ToolsConfig.DbConfig db = new ToolsConfig.DbConfig(
                str(dbMap.get("url"), ""),
                str(dbMap.get("user"), ""),
                str(dbMap.get("password"), "")
        );

        Map<String, Object> tenantsMap = (Map<String, Object>) data.getOrDefault("tenants", Collections.emptyMap());
        ToolsConfig.Tenants tenants = new ToolsConfig.Tenants(intOf(tenantsMap.get("count"), 2));

        Map<String, Object> mdMap = (Map<String, Object>) data.getOrDefault("master_data", Collections.emptyMap());
        ToolsConfig.MasterData md = new ToolsConfig.MasterData(
                intOf(mdMap.get("products_per_tenant"), 20),
                intOf(mdMap.get("warehouses_per_tenant"), 3),
                intOf(mdMap.get("lots_per_product"), 2),
                intOf(mdMap.get("uoms_per_tenant"), 3)
        );

        Map<String, Object> obMap = (Map<String, Object>) data.getOrDefault("opening_balance", Collections.emptyMap());
        ToolsConfig.OpeningBalance ob = new ToolsConfig.OpeningBalance(
                intOf(obMap.get("rows"), 100),
                str(obMap.get("output_dir"), "./generated/opening_balance"),
                str(obMap.get("file_name"), "opening_balance_synthetic.csv"),
                str(obMap.get("csv_delimiter"), ";"),
                str(obMap.get("date"), "today")
        );

        Map<String, Object> txMap = (Map<String, Object>) data.getOrDefault("transactions", Collections.emptyMap());
        ToolsConfig.Transactions tx = new ToolsConfig.Transactions(
                intOf(txMap.get("count"), 50),
                doubleOf(txMap.get("inbound_ratio"), 0.6),
                doubleOf(txMap.get("transfer_pair_ratio"), 0.1),
                boolOf(txMap.get("via_function"), true)
        );

        Map<String, Object> oMap = (Map<String, Object>) data.getOrDefault("orders", Collections.emptyMap());
        ToolsConfig.Orders orders = new ToolsConfig.Orders(
                intOf(oMap.get("count"), 20),
                intOf(oMap.get("min_lines"), 1),
                intOf(oMap.get("max_lines"), 5),
                str(oMap.get("initial_state"), "open")
        );

        return new ToolsConfig(seed, db, tenants, md, ob, tx, orders);
    }

    private static String str(Object v, String dflt) { return v == null ? dflt : v.toString(); }
    private static int intOf(Object v, int dflt) {
        if (v == null) return dflt;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (NumberFormatException e) { return dflt; }
    }
    private static long longOf(Object v, long dflt) {
        if (v == null) return dflt;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException e) { return dflt; }
    }
    private static double doubleOf(Object v, double dflt) {
        if (v == null) return dflt;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (NumberFormatException e) { return dflt; }
    }
    private static boolean boolOf(Object v, boolean dflt) {
        if (v == null) return dflt;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }
}
