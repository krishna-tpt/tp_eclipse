package org.michelin.filemanager.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds an immutable Catalog by reading every *.yaml file in a directory of
 * interface-definition specs. Hand-mapped from YAML maps to records (no
 * jackson-yaml binding) so the validation messages are precise about which
 * key failed, in which file.
 */
public class CatalogLoader {

    private static final Logger log = LoggerFactory.getLogger(CatalogLoader.class);

    /** Test entry point. Reads a directory on disk. */
    public Catalog loadFromPath(Path dir) {
        if (!Files.isDirectory(dir))
            throw new CatalogValidationException("Catalog path is not a directory: " + dir);

        List<InterfaceDefinition> defs = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{yaml,yml}")) {
            for (Path file : stream) {
                defs.add(parseFile(file));
            }
        } catch (IOException e) {
            throw new CatalogValidationException("Failed to list catalog directory: " + dir, e);
        }
        if (defs.isEmpty())
            throw new CatalogValidationException("No catalog YAML files found under: " + dir);

        log.info("Loaded {} interface definition(s) from {}", defs.size(), dir);
        return new Catalog(defs);
    }

    /**
     * Production entry point. Reads {@code classpath:<prefix>/*.yaml}. Works for
     * both exploded classpath (IDE / Surefire) and packaged JARs (Failsafe /
     * production runtime) by opening the JAR as a zip FileSystem when needed.
     */
    public Catalog loadFromClasspath(String classpathPrefix) {
        String prefix = classpathPrefix.endsWith("/") ? classpathPrefix : classpathPrefix + "/";
        URL dirUrl = CatalogLoader.class.getClassLoader().getResource(prefix);
        if (dirUrl == null)
            throw new CatalogValidationException("Catalog classpath prefix not found: " + prefix);

        try {
            switch (dirUrl.getProtocol()) {
                case "file":
                    return loadFromPath(Paths.get(dirUrl.toURI()));
                case "jar":
                    return loadFromJar(dirUrl, prefix);
                default:
                    throw new CatalogValidationException(
                        "Unsupported catalog classpath protocol: " + dirUrl.getProtocol() + " (" + dirUrl + ")");
            }
        } catch (URISyntaxException | IOException e) {
            throw new CatalogValidationException("Cannot resolve catalog classpath: " + dirUrl, e);
        }
    }

    /**
     * Open the containing JAR as a zip FileSystem, locate {@code /<prefix>/},
     * and reuse {@link #loadFromPath} to read each YAML inside it.
     *
     * <p>The FileSystem is opened with reference-counting semantics: if another
     * loader has it open, we reuse the handle and do not close it (closing
     * would invalidate paths for that other holder). For our case — startup-time
     * one-shot load — this is fine; the FS lives for the JVM.
     */
    private Catalog loadFromJar(URL dirUrl, String prefix) throws URISyntaxException, IOException {
        // dirUrl looks like jar:file:/path/to.jar!/interfaces/
        String urlString = dirUrl.toString();
        int separator = urlString.indexOf("!/");
        if (separator < 0)
            throw new CatalogValidationException("Malformed jar URL: " + urlString);
        URI jarUri = URI.create(urlString.substring(0, separator));   // jar:file:/path/to.jar
        String entryPath = urlString.substring(separator + 1);        // /interfaces/

        FileSystem fs;
        try {
            fs = FileSystems.newFileSystem(jarUri, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException existing) {
            fs = FileSystems.getFileSystem(jarUri);
        }
        Path dir = fs.getPath(entryPath);
        return loadFromPath(dir);
    }

    // ------------------------------------------------------------------
    // YAML → record graph
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private InterfaceDefinition parseFile(Path file) {
        Map<String, Object> data;
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            data = (Map<String, Object>) new Yaml().load(r);
        } catch (IOException e) {
            throw new CatalogValidationException("Failed to read catalog file: " + file, e);
        }
        if (data == null)
            throw new CatalogValidationException("Catalog file is empty: " + file);
        try {
            return mapToDefinition(data);
        } catch (CatalogValidationException e) {
            // Re-throw with file context prepended for easier debugging.
            throw new CatalogValidationException(file.getFileName() + ": " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private InterfaceDefinition mapToDefinition(Map<String, Object> data) {
        String iface   = strOrNull(data, "interface");
        String variant = strOrNull(data, "variant");
        String version = strOrDefault(data, "version", "0.0");

        Map<String, Object> matchesMap = (Map<String, Object>) data.get("matches");
        if (matchesMap == null)
            throw new CatalogValidationException("matches is required");
        InterfaceDefinition.HeaderConfirms hc = null;
        Map<String, Object> hcMap = (Map<String, Object>) matchesMap.get("header_confirms");
        if (hcMap != null) {
            hc = new InterfaceDefinition.HeaderConfirms(
                    strOrNull(hcMap, "record"),
                    intOrZero(hcMap, "field"),
                    strOrNull(hcMap, "equals"));
        }
        InterfaceDefinition.Matches matches = new InterfaceDefinition.Matches(
                strOrNull(matchesMap, "filename_pattern"), hc);

        Map<String, Object> parserMap = (Map<String, Object>) data.get("parser");
        if (parserMap == null)
            throw new CatalogValidationException("parser is required");
        InterfaceDefinition.ParserRules parser = new InterfaceDefinition.ParserRules(
                strOrDefault(parserMap, "delimiter", ";"),
                strOrDefault(parserMap, "quote",     "\""),
                strOrDefault(parserMap, "encoding",  "UTF-8"),
                boolOrDefault(parserMap, "trim",            true),
                boolOrDefault(parserMap, "empty_is_null",   true),
                strOrDefault(parserMap,  "decimal_separator", "."));

        List<Object> envelopeList = (List<Object>) data.get("envelope");
        if (envelopeList == null || envelopeList.isEmpty())
            throw new CatalogValidationException("envelope is required and must declare at least one entry");
        List<InterfaceDefinition.EnvelopeSpec> envelope = new ArrayList<>();
        for (Object o : envelopeList) envelope.add(mapToEnvelope((Map<String, Object>) o));

        List<Object> recordList = (List<Object>) data.get("records");
        if (recordList == null || recordList.isEmpty())
            throw new CatalogValidationException("records is required and must declare at least one entry");
        List<InterfaceDefinition.DataRecordSpec> records = new ArrayList<>();
        for (Object o : recordList) records.add(mapToDataRecord((Map<String, Object>) o));

        return new InterfaceDefinition(iface, variant, version, matches, parser, envelope, records);
    }

    @SuppressWarnings("unchecked")
    private InterfaceDefinition.EnvelopeSpec mapToEnvelope(Map<String, Object> m) {
        String tag = strOrNull(m, "tag");
        int fields = intOrZero(m, "fields");

        Map<String, InterfaceDefinition.CaptureSpec> captures = new LinkedHashMap<>();
        Map<String, Object> capMap = (Map<String, Object>) m.getOrDefault("capture", Collections.emptyMap());
        for (Map.Entry<String, Object> e : capMap.entrySet()) {
            Map<String, Object> cm = (Map<String, Object>) e.getValue();
            captures.put(e.getKey(), new InterfaceDefinition.CaptureSpec(
                    intOrZero(cm, "field"),
                    ColumnType.fromYaml(strOrNull(cm, "type")),
                    strOrNull(cm, "format")));
        }

        Map<String, InterfaceDefinition.ValidationSpec> validates = new LinkedHashMap<>();
        Map<String, Object> valMap = (Map<String, Object>) m.getOrDefault("validates", Collections.emptyMap());
        for (Map.Entry<String, Object> e : valMap.entrySet()) {
            Map<String, Object> vm = (Map<String, Object>) e.getValue();
            validates.put(e.getKey(), new InterfaceDefinition.ValidationSpec(intOrZero(vm, "field")));
        }
        return new InterfaceDefinition.EnvelopeSpec(tag, fields, captures, validates);
    }

    @SuppressWarnings("unchecked")
    private InterfaceDefinition.DataRecordSpec mapToDataRecord(Map<String, Object> m) {
        String tag = strOrNull(m, "tag");
        int fields = intOrZero(m, "fields");
        String target = strOrNull(m, "target_table");

        LinkedHashMap<String, InterfaceDefinition.ColumnRule> columns = new LinkedHashMap<>();
        Map<String, Object> colMap = (Map<String, Object>) m.getOrDefault("columns", Collections.emptyMap());
        for (Map.Entry<String, Object> e : colMap.entrySet()) {
            columns.put(e.getKey(), mapToColumnRule(e.getKey(), (Map<String, Object>) e.getValue()));
        }

        List<String> dedupeKeys = (List<String>) m.getOrDefault("dedupe_keys", Collections.emptyList());
        return new InterfaceDefinition.DataRecordSpec(tag, fields, target, columns, dedupeKeys);
    }

    private InterfaceDefinition.ColumnRule mapToColumnRule(String columnName, Map<String, Object> m) {
        InterfaceDefinition.Modifiers mods = extractModifiers(m);
        boolean hasField     = m.containsKey("field");
        boolean hasEnvelope  = m.containsKey("from_envelope");
        boolean hasLiteral   = m.containsKey("literal");
        int sources = (hasField ? 1 : 0) + (hasEnvelope ? 1 : 0) + (hasLiteral ? 1 : 0);
        if (sources == 0)
            throw new CatalogValidationException(
                "column '" + columnName + "' has no source — must declare exactly one of: field, from_envelope, literal");
        if (sources > 1)
            throw new CatalogValidationException(
                "column '" + columnName + "' declares multiple sources — pick exactly one of: field, from_envelope, literal");
        if (hasField) {
            return new InterfaceDefinition.FieldRule(intOrZero(m, "field"), mods);
        }
        if (hasEnvelope) {
            return new InterfaceDefinition.EnvelopeRule(
                    strOrNull(m, "from_envelope"),
                    strOrNull(m, "captured_as"),
                    mods);
        }
        return new InterfaceDefinition.LiteralRule(strOrNull(m, "literal"), mods);
    }

    @SuppressWarnings("unchecked")
    private InterfaceDefinition.Modifiers extractModifiers(Map<String, Object> m) {
        ColumnType type   = ColumnType.fromYaml(strOrNull(m, "type"));
        String format     = strOrNull(m, "format");
        boolean required  = boolOrDefault(m, "required", false);
        Integer maxLength = m.containsKey("max_length") ? intOrZero(m, "max_length") : null;
        String regex      = strOrNull(m, "regex");
        List<String> allowed = (List<String>) m.getOrDefault("allowed_values", Collections.emptyList());
        String defaultValue = strOrNull(m, "default");
        return new InterfaceDefinition.Modifiers(type, format, required, maxLength, regex, allowed, defaultValue);
    }

    // ------------------------------------------------------------------
    // YAML accessor helpers
    // ------------------------------------------------------------------

    private static String strOrNull(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? null : v.toString();
    }

    private static String strOrDefault(Map<String, Object> m, String key, String dflt) {
        Object v = m.get(key);
        return v == null ? dflt : v.toString();
    }

    private static int intOrZero(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); }
        catch (NumberFormatException e) {
            throw new CatalogValidationException(
                "Catalog value for '" + key + "' is not a valid integer: " + v);
        }
    }

    private static boolean boolOrDefault(Map<String, Object> m, String key, boolean dflt) {
        Object v = m.get(key);
        if (v == null) return dflt;
        if (v instanceof Boolean b) return b;
        String s = v.toString().trim();
        if (s.equalsIgnoreCase("true"))  return true;
        if (s.equalsIgnoreCase("false")) return false;
        throw new CatalogValidationException(
            "Catalog value for '" + key + "' is not a valid boolean: " + v);
    }
}
