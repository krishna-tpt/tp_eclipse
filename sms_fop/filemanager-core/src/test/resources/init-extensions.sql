-- Bootstrap extensions for the test container. Production runs an equivalent
-- step out-of-band on Azure Flexible Postgres before Flyway is allowed to run.
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS pgtap;

CREATE SCHEMA IF NOT EXISTS partman;
CREATE EXTENSION IF NOT EXISTS pg_partman SCHEMA partman;

CREATE EXTENSION IF NOT EXISTS pg_cron;
