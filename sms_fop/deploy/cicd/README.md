# DevOps handover — `psql-inventory-integration-service`

Adapts the Michelin `microservice-pipeline-template.yml` to our repo.
Follows the same conventions middleware team uses for
`stock-on-hand-integration-service` (credentials inline in the
config-repository, not K8s Secrets).

## What's in this folder

| File | Where it goes |
|------|---------------|
| `../../.gitlab-ci.yml` | Already at repo root (moved). |
| `../../Dockerfile` | Already at repo root (moved). |
| `../../.dockerignore` | Already at repo root (added). |
| `psql-inventory-integration-service.yml` | Country `config-repository`, per env branch (sandbox / dev / qa / pre-prod / prod). Same folder as `common-service-configs.yml`. |
| Helm values overrides | Country `config-repository`, path `helm-values/psql-inventory-integration-service-<ns>.yaml`. Sample below. |
| `check-sftp.ps1` / `.bat` / `.sh` | Smoke test — hand to whoever will validate SFTP from VDI. |
| `check-filehub.ps1` / `.bat` | Smoke test — same idea for files.com HTTPS backend. |

## Pipeline shape (matches `account-integration-service`)

Root `.gitlab-ci.yml` follows the same pattern as every other Michelin
integration service: it `include:`s the central template from
`cicd-templates/microservice-pipeline-template.yml`. No overrides — the
service is Gradle-based, same as the template's default build stage.

## GitLab variables

Set in `.gitlab-ci.yml` (already committed):

- `SERVICE = psql-inventory`
- `DEPLOY_COUNTRY = nl-integration-services`

Set as project-level CI variables (DevOps, masked + protected):

- `ARTIFACTORY_USER`
- `ARTIFACTORY_TOKEN`
- `KUBE_CONFIG_NONPROD`
- `KUBE_CONFIG_PROD`

## Credentials — inline in config-repo (no K8s Secret)

Same pattern as `stock-on-hand-integration-service.yml`. Per env branch of the
config-repository, replace the `REPLACE-ME-PER-ENV` placeholders in
`psql-inventory-integration-service.yml`:

- `db.password` — Postgres role password for `inventoryledger_app`
- `file.filescom.api_key` — files.com API key
- `notifier.secret` — HMAC secret for outbound webhooks *(optional)*
- URLs / paths — env-specific (sandbox → `/EU/SBX/BR/...`, prod → `/EU/PRD/BR/...`)

Config-repository access controls are the trust boundary.

## Sample helm-values override

`config-repository/helm-values/psql-inventory-integration-service-qc10gbl0.yaml`:

```yaml
replicaCount: 1
strategy:
  type: Recreate
terminationGracePeriodSeconds: 120

service:
  port: 8080

# Plain Java daemon — probe the raw path, not the Spring actuator base-path.
probes:
  liveness:
    path: /actuator/health
    port: 8080
    initialDelaySeconds: 30
    periodSeconds: 30
  readiness:
    path: /actuator/health
    port: 8080
    initialDelaySeconds: 15
    periodSeconds: 15

# Only non-secret runtime env is set here. All creds live in the mounted
# psql-inventory-integration-service.yml under $CONFIG_DIR.
env:
  APP_PROFILE: prod
  RUN_MODE: scheduled
  TZ: Europe/Amsterdam
  FLYWAY_ENABLED: "false"
  CONFIG_DIR: /opt/app/config

resources:
  requests: { cpu: 100m, memory: 256Mi }
  limits:   { cpu: 500m, memory: 512Mi }
```

## Health check

- URL: `http://<pod>:8080/actuator/health`
- Healthy: `200` + `{"status":"UP","components":{"db":{...},"scheduler":{...}}}`
- Unhealthy: `503` + `{"status":"DOWN",...}` — restart the pod

## Key differences from other integration services

- **Plain Java daemon**, not Spring Boot — no `spring.application.name`, no Actuator prefix, no Kafka. `common-service-configs.yml` Spring / Kafka keys are inert here.
- **No transformation-rules** — Dockerfile does not clone `config-repository` at build time. All runtime config is mounted by helm.
- **JDK 21 bytecode**, JDK 25 base image — works fine (backward compat verified).

## Verify the pipeline before handing over

- Push to `sandbox` → `build` → `dockerize` → `deploy`.
- `kubectl -n qc10gbl0 get pods -l app=psql-inventory-integration-service`.
- `kubectl -n qc10gbl0 port-forward deploy/psql-inventory-integration 8080:8080` → `curl -s localhost:8080/actuator/health | jq`.
- Logs: `kubectl -n qc10gbl0 logs deploy/psql-inventory-integration -f | grep -E 'startup|health|scheduler'`.

## Rollback

- `kubectl -n <ns> rollout undo deploy/psql-inventory-integration` — one revision back.
- Or redeploy a prior Artifactory image tag via `kubectl set image`.
- **Do not** roll back the database — coordinate schema changes with the customer DBA.
