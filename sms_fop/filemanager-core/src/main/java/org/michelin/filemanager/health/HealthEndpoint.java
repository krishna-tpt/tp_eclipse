package org.michelin.filemanager.health;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Minimal HTTP server exposing /actuator/health for K8s liveness + readiness
 * probes (ADR-0014). Uses the JDK-built-in {@link HttpServer} so we add no
 * dependency. One context, one handler — there is no other HTTP surface.
 *
 * Response:
 *   200 OK  with JSON body {"status":"UP","components":{...}} when all checks pass
 *   503     with JSON body {"status":"DOWN","components":{...}} when any check fails
 *
 * Checks are registered via {@link #addCheck} before start(). The checks are
 * invoked per request — there is no caching. Cheap checks (alive flag, SELECT 1)
 * make this acceptable; if any check ever becomes expensive, introduce caching
 * at that point — not preemptively.
 */
public final class HealthEndpoint {

    private static final Logger log = LoggerFactory.getLogger(HealthEndpoint.class);
    private static final String PATH = "/actuator/health";

    private final String host;
    private final int port;
    private final Map<String, Supplier<HealthStatus.ComponentResult>> checks = new LinkedHashMap<>();
    private HttpServer server;

    public HealthEndpoint(String host, int port) {
        if (host == null || host.isBlank()) throw new IllegalArgumentException("host is required");
        // port 0 is allowed — JDK HttpServer interprets it as "pick an ephemeral port".
        // Useful for tests; not allowed in Config.HealthConfig (prod config).
        if (port < 0 || port > 65535) throw new IllegalArgumentException("port must be 0..65535");
        this.host = host;
        this.port = port;
    }

    public HealthEndpoint addCheck(String name, Supplier<HealthStatus.ComponentResult> check) {
        Objects.requireNonNull(name,  "name");
        Objects.requireNonNull(check, "check");
        if (server != null) throw new IllegalStateException("cannot add checks after start()");
        checks.put(name, check);
        return this;
    }

    public synchronized void start() throws IOException {
        if (server != null) throw new IllegalStateException("already started");
        if (checks.isEmpty()) throw new IllegalStateException("at least one check is required");
        server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.createContext(PATH, this::handle);
        server.setExecutor(null); // default single-thread executor — fine for probe traffic
        server.start();
        log.info("health endpoint listening on http://{}:{}{}", host, port, PATH);
    }

    public synchronized void stop() {
        HttpServer s = server;
        if (s == null) return;
        s.stop(1);              // wait up to 1s for in-flight requests
        server = null;
        log.info("health endpoint stopped");
    }

    /** Returns the actual bound port — useful for tests that pass port=0. */
    public int boundPort() {
        HttpServer s = server;
        return s == null ? -1 : s.getAddress().getPort();
    }

    private void handle(HttpExchange ex) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                writeResponse(ex, 405, "{\"status\":\"DOWN\",\"error\":\"method-not-allowed\"}");
                return;
            }
            HealthStatus status = evaluate();
            int statusCode = status.up() ? 200 : 503;
            writeResponse(ex, statusCode, status.toJson());
        } catch (RuntimeException re) {
            // Never let an exception escape the handler — K8s sees a hung TCP socket otherwise.
            log.warn("health handler threw: {}", re.getMessage(), re);
            writeResponse(ex, 500, "{\"status\":\"DOWN\",\"error\":\"handler-exception\"}");
        }
    }

    private HealthStatus evaluate() {
        Map<String, HealthStatus.ComponentResult> results = new LinkedHashMap<>();
        for (Map.Entry<String, Supplier<HealthStatus.ComponentResult>> e : checks.entrySet()) {
            try {
                HealthStatus.ComponentResult r = e.getValue().get();
                results.put(e.getKey(), r == null
                        ? HealthStatus.ComponentResult.failing("check returned null")
                        : r);
            } catch (RuntimeException re) {
                results.put(e.getKey(),
                        HealthStatus.ComponentResult.failing(re.getMessage() == null
                                ? re.getClass().getSimpleName() : re.getMessage()));
            }
        }
        return HealthStatus.from(results);
    }

    private static void writeResponse(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
