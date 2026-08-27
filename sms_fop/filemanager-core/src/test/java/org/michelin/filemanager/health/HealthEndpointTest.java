package org.michelin.filemanager.health;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-320..TC-326 — HealthEndpoint behavior. Boots a real HttpServer on an
 * ephemeral port (port=0) per test, makes a real HTTP request, then stops.
 * Fast (sub-second) and dep-free.
 */
class HealthEndpointTest {

    private HealthEndpoint endpoint;

    @AfterEach
    void tearDown() {
        if (endpoint != null) endpoint.stop();
    }

    @Test
    @DisplayName("TC-320: all checks UP → 200 with status=UP and component breakdown")
    void allChecksUp_returns200() throws Exception {
        endpoint = new HealthEndpoint("127.0.0.1", 0)
                .addCheck("db",        HealthStatus.ComponentResult::passing)
                .addCheck("scheduler", HealthStatus.ComponentResult::passing);
        endpoint.start();

        HttpResponse<String> resp = get(endpoint.boundPort(), "/actuator/health");

        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body()).contains("\"status\":\"UP\"")
                .contains("\"db\":{\"status\":\"UP\"}")
                .contains("\"scheduler\":{\"status\":\"UP\"}");
    }

    @Test
    @DisplayName("TC-321: one check DOWN → 503 with reason in body")
    void oneCheckDown_returns503() throws Exception {
        endpoint = new HealthEndpoint("127.0.0.1", 0)
                .addCheck("db", () -> HealthStatus.ComponentResult.failing("connection refused"))
                .addCheck("scheduler", HealthStatus.ComponentResult::passing);
        endpoint.start();

        HttpResponse<String> resp = get(endpoint.boundPort(), "/actuator/health");

        assertThat(resp.statusCode()).isEqualTo(503);
        assertThat(resp.body()).contains("\"status\":\"DOWN\"")
                .contains("\"db\":{\"status\":\"DOWN\",\"reason\":\"connection refused\"}")
                .contains("\"scheduler\":{\"status\":\"UP\"}");
    }

    @Test
    @DisplayName("TC-322: check that throws is reported DOWN with the exception message")
    void checkThrows_reportedDown() throws Exception {
        endpoint = new HealthEndpoint("127.0.0.1", 0)
                .addCheck("db", () -> { throw new RuntimeException("kaboom"); });
        endpoint.start();

        HttpResponse<String> resp = get(endpoint.boundPort(), "/actuator/health");

        assertThat(resp.statusCode()).isEqualTo(503);
        assertThat(resp.body()).contains("\"db\":{\"status\":\"DOWN\",\"reason\":\"kaboom\"}");
    }

    @Test
    @DisplayName("TC-323: POST returns 405 (only GET is allowed)")
    void postNotAllowed() throws Exception {
        endpoint = new HealthEndpoint("127.0.0.1", 0)
                .addCheck("db", HealthStatus.ComponentResult::passing);
        endpoint.start();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + endpoint.boundPort() + "/actuator/health"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(2))
                .build();

        HttpResponse<String> resp = HttpClient.newHttpClient()
                .send(req, HttpResponse.BodyHandlers.ofString());

        assertThat(resp.statusCode()).isEqualTo(405);
    }

    @Test
    @DisplayName("TC-324: starting with no checks throws IllegalStateException")
    void noChecks_startThrows() {
        endpoint = new HealthEndpoint("127.0.0.1", 0);
        assertThatThrownBy(() -> endpoint.start()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("TC-325: adding a check after start throws IllegalStateException")
    void addCheckAfterStart_throws() throws Exception {
        endpoint = new HealthEndpoint("127.0.0.1", 0)
                .addCheck("first", HealthStatus.ComponentResult::passing);
        endpoint.start();

        assertThatThrownBy(() ->
                endpoint.addCheck("second", HealthStatus.ComponentResult::passing))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("TC-326: stop is idempotent and re-callable")
    void stop_isIdempotent() throws Exception {
        endpoint = new HealthEndpoint("127.0.0.1", 0)
                .addCheck("ok", HealthStatus.ComponentResult::passing);
        endpoint.start();

        endpoint.stop();
        endpoint.stop();        // second call must not throw
        assertThat(endpoint.boundPort()).isEqualTo(-1);
    }

    private static HttpResponse<String> get(int port, String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .GET()
                .timeout(Duration.ofSeconds(2))
                .build();
        return HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
    }
}
