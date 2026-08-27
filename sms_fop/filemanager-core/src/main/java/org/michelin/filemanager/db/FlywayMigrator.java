package org.michelin.filemanager.db;

import org.flywaydb.core.Flyway;
import org.michelin.filemanager.config.Config;

public class FlywayMigrator {

    private final Config.DbConfig db;
    private final Config.FlywayConfig flyway;

    public FlywayMigrator(Config.DbConfig db, Config.FlywayConfig flyway) {
        this.db = db;
        this.flyway = flyway;
    }

    public void migrate() {
        Flyway fly = Flyway.configure()
                .dataSource(db.url(), db.user(), db.password())
                .locations(flyway.locations())
                .baselineOnMigrate(flyway.baselineOnMigrate())
                .validateOnMigrate(flyway.validateOnMigrate())
                // Migration SQL uses ${...} inside plpgsql bodies (e.g. format()).
                // Disable Flyway placeholder substitution so it does not rewrite them.
                .placeholderReplacement(false)
                // Flyway.clean() drops every object. Never expose that in prod.
                .cleanDisabled(true)
                .load();
        fly.migrate();
    }
}
