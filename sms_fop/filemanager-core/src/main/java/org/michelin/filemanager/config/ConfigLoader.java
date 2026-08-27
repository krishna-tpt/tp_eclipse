package org.michelin.filemanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    private static final String DEFAULT_PROFILE = "prod";

    /**
     * Production entry point. Layered config, last-wins on conflicts:
     *   1. classpath:application.yaml                            (base, ships in JAR)
     *   2. classpath:application-<APP_PROFILE>.yaml              (profile overlay, in JAR)
     *   3. ${CONFIG_DIR}/*.yaml in alphabetical order            (external overlays, optional)
     *   4. env-var substitution applied to the merged tree       (${VAR} / ${VAR:default})
     *
     * The CONFIG_DIR step bridges to a centralized config-repo pattern: mount
     * common-service-configs.yaml and <service>.yaml at /opt/app/config/ in
     * K8s and set CONFIG_DIR=/opt/app/config — files are loaded in alphabetical
     * order, so name them accordingly when ordering matters (common-* sorts
     * before <service>-*, so the service file wins on conflicts).
     */
    public Config load(Map<String, String> env) {
        String profile = env.getOrDefault("APP_PROFILE", DEFAULT_PROFILE);

        Map<String, Object> base = loadFromClasspath("/application.yaml");
        if (base == null) {
            throw new ConfigValidationException("application.yaml not found on classpath");
        }

        Map<String, Object> profileOverlay = loadFromClasspath("/application-" + profile + ".yaml");
        if (profileOverlay != null) {
            deepMerge(base, profileOverlay);
        }

        String configDir = env.get("CONFIG_DIR");
        if (configDir != null && !configDir.isBlank()) {
            for (Map<String, Object> overlay : loadOverlaysFromDir(Paths.get(configDir))) {
                deepMerge(base, overlay);
            }
        }

        return resolveAndMap(base, env);
    }

    /**
     * Reads every *.yaml / *.yml file in {@code dir} in alphabetical order.
     * An empty directory returns an empty list. A non-existent or non-directory
     * path throws — better to fail loudly than silently ignore a typo'd mount.
     */
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> loadOverlaysFromDir(Path dir) {
        if (!Files.exists(dir)) {
            throw new ConfigValidationException("CONFIG_DIR does not exist: " + dir);
        }
        if (!Files.isDirectory(dir)) {
            throw new ConfigValidationException("CONFIG_DIR is not a directory: " + dir);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase();
                        return name.endsWith(".yaml") || name.endsWith(".yml");
                    })
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
            for (Path p : files) {
                try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                    Object loaded = new Yaml().load(r);
                    if (loaded == null) {
                        log.info("config overlay {} is empty; skipping", p);
                        continue;
                    }
                    if (!(loaded instanceof Map)) {
                        throw new ConfigValidationException(
                            "config overlay must be a YAML map at the top level: " + p);
                    }
                    log.info("loaded config overlay: {}", p);
                    out.add((Map<String, Object>) loaded);
                } catch (IOException e) {
                    throw new ConfigValidationException("failed to read config overlay: " + p, e);
                }
            }
        } catch (IOException e) {
            throw new ConfigValidationException("failed to list CONFIG_DIR: " + dir, e);
        }
        return out;
    }

    /** Test entry point. Loads a single YAML file from disk; no profile overlay. */
    public Config loadFromPath(Path path, Map<String, String> env) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) new Yaml().load(r);
            if (data == null) {
                throw new ConfigValidationException("YAML file is empty: " + path);
            }
            return resolveAndMap(data, env);
        } catch (IOException e) {
            throw new ConfigValidationException("Failed to read YAML: " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadFromClasspath(String resource) {
        try (InputStream in = ConfigLoader.class.getResourceAsStream(resource)) {
            if (in == null) return null;
            Object loaded = new Yaml().load(in);
            return (Map<String, Object>) loaded;
        } catch (IOException e) {
            throw new ConfigValidationException("Failed to read classpath resource: " + resource, e);
        }
    }

    private Config resolveAndMap(Map<String, Object> data, Map<String, String> env) {
        @SuppressWarnings("unchecked")
        Map<String, Object> resolved = (Map<String, Object>) resolveTree(data, env);
        return mapToConfig(resolved);
    }

    private Object resolveTree(Object node, Map<String, String> env) {
        if (node instanceof String s) {
            return resolvePlaceholders(s, env);
        }
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

    /**
     * Resolves ${VAR} or ${VAR:default} placeholders. Defaults may themselves
     * contain balanced braces — e.g. the regex {@code \d{8}} — so we count
     * braces instead of regex-matching, which the old [^}]* form could not.
     */
    private String resolvePlaceholders(String input, Map<String, String> env) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            int dollar = input.indexOf("${", i);
            if (dollar < 0) {
                out.append(input, i, input.length());
                break;
            }
            out.append(input, i, dollar);
            int close = findMatchingClose(input, dollar + 1);
            if (close < 0) {
                // Unbalanced — emit literally and stop scanning.
                out.append(input, dollar, input.length());
                break;
            }
            String body = input.substring(dollar + 2, close);
            int colon = body.indexOf(':');
            String var = (colon < 0) ? body : body.substring(0, colon);
            String defaultValue = (colon < 0) ? null : body.substring(colon + 1);
            if (!var.matches("[A-Z_][A-Z0-9_]*")) {
                // Not a valid env-var name — leave the placeholder untouched.
                out.append(input, dollar, close + 1);
                i = close + 1;
                continue;
            }
            String value = env.get(var);
            if (value == null || value.isEmpty()) {
                if (defaultValue == null) {
                    throw new ConfigValidationException("Required env var " + var + " is not set");
                }
                value = defaultValue;
            }
            out.append(value);
            i = close + 1;
        }
        return out.toString();
    }

    /** Returns the index of the '}' matching the '{' at {@code openIdx}, or -1. */
    private static int findMatchingClose(String s, int openIdx) {
        int depth = 0;
        for (int i = openIdx; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void deepMerge(Map<String, Object> base, Map<String, Object> overlay) {
        for (Map.Entry<String, Object> e : overlay.entrySet()) {
            String key = e.getKey();
            Object overlayVal = e.getValue();
            Object baseVal = base.get(key);
            if (baseVal instanceof Map && overlayVal instanceof Map) {
                deepMerge((Map<String, Object>) baseVal, (Map<String, Object>) overlayVal);
            } else {
                base.put(key, overlayVal);
            }
        }
    }

    // ------------------------------------------------------------------
    // Map -> Config record graph
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Config mapToConfig(Map<String, Object> data) {
        String profile = strOrDefault(data, "profile", DEFAULT_PROFILE);

        Map<String, Object> dbMap = (Map<String, Object>) requireMap(data, "db");
        Config.DbConfig db = new Config.DbConfig(
                requireStr(dbMap, "db.url"),
                strOrDefault(dbMap, "user", ""),
                strOrDefault(dbMap, "password", ""),
                intOrDefault(dbMap, "connect_timeout_s", 10),
                intOrDefault(dbMap, "statement_timeout_ms", 60000),
                strOrDefault(dbMap, "application_name", "inventoryledger")
        );

        Map<String, Object> fwMap = (Map<String, Object>) data.getOrDefault("flyway", Collections.emptyMap());
        Config.FlywayConfig flyway = new Config.FlywayConfig(
                boolOrDefault(fwMap, "enabled", true),
                strOrDefault(fwMap, "locations", "classpath:db/migration"),
                boolOrDefault(fwMap, "baseline_on_migrate", false),
                boolOrDefault(fwMap, "validate_on_migrate", true)
        );

        Map<String, Object> fileMap = (Map<String, Object>) requireMap(data, "file");
        Config.FileConfig.SftpConfig sftp = null;
        Map<String, Object> sftpMap = (Map<String, Object>) fileMap.get("sftp");
        if (sftpMap != null) {
            sftp = new Config.FileConfig.SftpConfig(
                    strOrDefault(sftpMap, "host", ""),
                    intOrDefault(sftpMap, "port", 22),
                    strOrDefault(sftpMap, "username", ""),
                    strOrDefault(sftpMap, "password", ""),
                    strOrDefault(sftpMap, "private_key_path", ""),
                    strOrDefault(sftpMap, "private_key_passphrase", ""),
                    strOrDefault(sftpMap, "known_hosts_path", ""),
                    strOrDefault(sftpMap, "pickup_path", ""),
                    strOrDefault(sftpMap, "archive_path", ""),
                    strOrDefault(sftpMap, "reject_path", ""),
                    intOrDefault(sftpMap, "connect_timeout_ms", 10000)
            );
        }
        Map<String, Object> localMap = (Map<String, Object>) fileMap.get("local");
        Config.FileConfig.LocalConfig local = null;
        if (localMap != null) {
            local = new Config.FileConfig.LocalConfig(
                    strOrDefault(localMap, "pickup_path", "./inbound"),
                    strOrDefault(localMap, "archive_path", "./archive"),
                    strOrDefault(localMap, "reject_path", "./rejected")
            );
        }
        Map<String, Object> filescomMap = (Map<String, Object>) fileMap.get("filescom");
        Config.FileConfig.FilesComConfig filescom = null;
        if (filescomMap != null) {
            filescom = new Config.FileConfig.FilesComConfig(
                    strOrDefault(filescomMap, "api_key", ""),
                    strOrDefault(filescomMap, "base_url", ""),
                    strOrDefault(filescomMap, "pickup_path", ""),
                    strOrDefault(filescomMap, "archive_path", ""),
                    strOrDefault(filescomMap, "reject_path", ""),
                    intOrDefault(filescomMap, "connect_timeout_ms", 10000),
                    intOrDefault(filescomMap, "read_timeout_ms", 60000),
                    intOrDefault(filescomMap, "page_size", 200)
            );
        }
        Config.FileConfig file = new Config.FileConfig(
                strOrDefault(fileMap, "source", "local"),
                strOrDefault(fileMap, "name_pattern", "^MICH_INV_STOCKLEVEL_413\\d{14}\\.dat$"),
                sftp, local, filescom
        );

        Map<String, Object> nMap = (Map<String, Object>) data.getOrDefault("notifier", Collections.emptyMap());
        List<String> urls = (List<String>) nMap.getOrDefault("webhook_urls", Collections.emptyList());
        Config.NotifierConfig notifier = new Config.NotifierConfig(
                urls,
                intOrDefault(nMap, "max_retries", 5),
                intOrDefault(nMap, "timeout_ms", 5000),
                intOrDefault(nMap, "retry_backoff_ms", 1000),
                intOrDefault(nMap, "batch_size", 50),
                intOrDefault(nMap, "connect_timeout_ms", 5000)
        );

        Map<String, Object> jMap = (Map<String, Object>) data.getOrDefault("job", Collections.emptyMap());
        Config.JobConfig job = new Config.JobConfig(
                boolOrDefault(jMap, "exit_on_first_failure", false),
                boolOrDefault(jMap, "drain_outbox", true),
                strOrDefault(jMap, "temp_file_prefix", "ob-"),
                strOrDefault(jMap, "timestamp_pattern", "yyyyMMdd_HHmmss")
        );

        Map<String, Object> lMap = (Map<String, Object>) data.getOrDefault("logging", Collections.emptyMap());
        Config.LoggingConfig logging = new Config.LoggingConfig(
                strOrDefault(lMap, "format", "json"),
                strOrDefault(lMap, "level", "INFO")
        );

        Config.RunMode runMode = Config.RunMode.parse(strOrDefault(data, "run_mode", "oneshot"));

        Map<String, Object> sMap = (Map<String, Object>) data.getOrDefault("schedule", Collections.emptyMap());
        Config.ScheduleConfig schedule = new Config.ScheduleConfig(
                strOrDefault(sMap, "daily_cron",  ""),
                strOrDefault(sMap, "hourly_cron", "")
        );

        Map<String, Object> hMap = (Map<String, Object>) data.getOrDefault("health", Collections.emptyMap());
        Config.HealthConfig health = new Config.HealthConfig(
                strOrDefault(hMap, "host", "0.0.0.0"),
                intOrDefault(hMap, "port", 8080)
        );

        return new Config(profile, runMode, db, flyway, file, notifier, job, logging, schedule, health);
    }

    private Object requireMap(Map<String, Object> data, String key) {
        Object v = data.get(key);
        if (!(v instanceof Map)) {
            throw new ConfigValidationException(key + " is required and must be a map");
        }
        return v;
    }

    private String requireStr(Map<String, Object> m, String fqPath) {
        String[] parts = fqPath.split("\\.");
        String key = parts[parts.length - 1];
        Object v = m.get(key);
        if (v == null || v.toString().isBlank()) {
            throw new ConfigValidationException(fqPath + " is required");
        }
        return v.toString();
    }

    private String strOrDefault(Map<String, Object> m, String key, String dflt) {
        Object v = m.get(key);
        return v == null ? dflt : v.toString();
    }

    private int intOrDefault(Map<String, Object> m, String key, int dflt) {
        Object v = m.get(key);
        if (v == null) return dflt;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); }
        catch (NumberFormatException e) {
            // Loud failure — a typo in env var should never silently fall back.
            throw new ConfigValidationException(
                "Config value for '" + key + "' is not a valid integer: " + v);
        }
    }

    private boolean boolOrDefault(Map<String, Object> m, String key, boolean dflt) {
        Object v = m.get(key);
        if (v == null) return dflt;
        if (v instanceof Boolean b) return b;
        String s = v.toString().trim();
        if (s.equalsIgnoreCase("true"))  return true;
        if (s.equalsIgnoreCase("false")) return false;
        throw new ConfigValidationException(
            "Config value for '" + key + "' is not a valid boolean: " + v);
    }
}
