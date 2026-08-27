package org.michelin.filemanager.integration.support;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared Testcontainers Postgres for the integration suite. Singleton, started
 * on first access, reused across test classes. Uses an image with pg_partman,
 * pg_cron, and pgTAP pre-installed.
 *
 * Image: see test/resources/Dockerfile.pgtest (built by docker before mvn verify)
 * Or override via -Dpg.test.image=org/inventoryledger-pg:test
 *
 * The image must include: pg_partman, pg_cron, pgcrypto, pgTAP.
 */
public final class InventoryLedgerPgContainer {

    private static final String DEFAULT_IMAGE = "ghcr.io/tenthplanet/inventoryledger-pg:test";
    private static volatile PostgreSQLContainer<?> instance;

    private InventoryLedgerPgContainer() {}

    public static PostgreSQLContainer<?> instance() {
        if (instance == null) {
            synchronized (InventoryLedgerPgContainer.class) {
                if (instance == null) {
                    String image = System.getProperty("pg.test.image", DEFAULT_IMAGE);
                    instance = new PostgreSQLContainer<>(DockerImageName.parse(image)
                        .asCompatibleSubstituteFor("postgres"))
                        .withDatabaseName("inventoryledger_test")
                        .withUsername("postgres")
                        .withPassword("postgres")
                        .withUrlParam("sslmode", "disable")
                        .withCommand(
                            "postgres",
                            "-c", "shared_preload_libraries=pg_cron,pg_partman_bgw",
                            "-c", "cron.database_name=inventoryledger_test"
                        )
                        .withReuse(true);
                    instance.start();
                }
            }
        }
        return instance;
    }
}
