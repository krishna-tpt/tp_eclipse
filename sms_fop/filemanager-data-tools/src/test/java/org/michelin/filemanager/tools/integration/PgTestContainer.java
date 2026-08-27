package org.michelin.filemanager.tools.integration;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Shared Postgres for data-tools integration tests. Same image override convention as core. */
final class PgTestContainer {

    private static final String DEFAULT_IMAGE = "ghcr.io/tenthplanet/inventoryledger-pg:test";
    private static volatile PostgreSQLContainer<?> instance;

    private PgTestContainer() {}

    static PostgreSQLContainer<?> instance() {
        if (instance == null) {
            synchronized (PgTestContainer.class) {
                if (instance == null) {
                    String image = System.getProperty("pg.test.image", DEFAULT_IMAGE);
                    instance = new PostgreSQLContainer<>(DockerImageName.parse(image)
                            .asCompatibleSubstituteFor("postgres"))
                            .withDatabaseName("inventoryledger_tools")
                            .withUsername("postgres")
                            .withPassword("postgres")
                            .withCommand(
                                "postgres",
                                "-c", "shared_preload_libraries=pg_cron,pg_partman_bgw",
                                "-c", "cron.database_name=inventoryledger_tools"
                            )
                            .withReuse(true);
                    instance.start();
                }
            }
        }
        return instance;
    }
}
