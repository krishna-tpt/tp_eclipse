# psql-inventory-integration-service

Michelin on-premises inventory ledger. Long-running Java 21 service that polls
files.com or SFTP for `MICH_INV_STOCKLEVEL_*` files, decodes them against a
YAML interface catalog, and lands them in the customer's Postgres 16 schema
(`staging` → `processed`). A companion PostgreSQL project owns the live
ledger, the ATP calculation, the reservation cascade, and the read API.

## Full documentation

The authoritative documentation is published as a static site under
[`docs-site/`](docs-site/). Sections:

- **Overview** — what this service is, spec, scope
- **Architecture** — pod topology, flow diagrams, ingest pipeline
- **Database** — schemas, ER, functions, evolution
- **Operations** — deployment, configuration, health, troubleshooting
- **Support** — FAQ, manual ops, failure recovery, log inspection, audit review
- **Quality** — test plan, test results, coverage, vulnerabilities, performance
- **Delivery** — status, changelog, tasks, customer releases
- **Reference** — interface catalog, file formats, env vars, event log
- **ADRs** — 14 architecture decision records

To view locally: `cd docs-site && npm install && npm run dev`
To rebuild: `cd docs-site && npm run build` → static output at `docs-site/dist/`

## Quick build (Gradle)

```bash
./gradlew :filemanager-core:fatJar
ls filemanager-core/build/libs/psql-inventory-integration-service-1.0.0.jar
```

Requires JDK 21+. Gradle 9.3 is fetched by the wrapper on first run.

## CI/CD

`.gitlab-ci.yml` includes the central Michelin pipeline template. See
[`deploy/cicd/README.md`](deploy/cicd/README.md) for the DevOps handover kit
(service config for the config-repository, sample helm-values, and VDI smoke
test scripts for files.com + SFTP connectivity).

## Modules

- [`filemanager-core/`](filemanager-core/) — production JAR. All runtime code lives here.
- [`filemanager-data-tools/`](filemanager-data-tools/) — dev-only synthetic data + inventory simulator (`InventorySimulator`, TC1..TC22). Never shipped.
- [`db/`](db/) — Flyway migrations + companion SQL used by DBServices.
- [`deploy/`](deploy/) — frozen customer-deployment packages (v1..v6) + Kubernetes samples + the DevOps CI/CD handover kit.

## License / ownership

Internal — TenthPlanet Technologies. Confidential.
