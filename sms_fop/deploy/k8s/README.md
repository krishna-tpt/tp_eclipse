# `deploy/k8s/` — Kubernetes env-injection reference

Ops owns the actual deployment (per [ADR-0008](../../docs/adr/0008-ops-owns-containerization.md)).
This folder is the **handoff contract**: "here is what the JAR expects on its env;
wire it however your cluster norms say."

## Files

| File | Purpose |
|---|---|
| `env.example.yaml` | `Secret` + `ConfigMap` + minimal `Deployment` showing the env wiring |

## How it maps to `.env.example`

The root `.env.example` is the **authoritative list of env vars**. This folder
just demonstrates one way to inject them into a Pod:

```
.env (local dev)                                 K8s (production)
─────────────────                                ──────────────────────────
DB_PASSWORD=...        ← secret      ────────►   Secret.stringData.DB_PASSWORD
FILES_COM_API_KEY=...  ← secret      ────────►   Secret.stringData.FILES_COM_API_KEY
WEBHOOK_URL_PRIMARY=...← contains    ────────►   Secret.stringData.WEBHOOK_URL_PRIMARY
                          auth token

DB_URL=...             ← config      ────────►   ConfigMap.data.DB_URL
APP_PROFILE=prod       ← config      ────────►   ConfigMap.data.APP_PROFILE
FILES_COM_PICKUP_PATH= ← config      ────────►   ConfigMap.data.FILES_COM_PICKUP_PATH
... and so on
```

The Pod's `envFrom: [secretRef, configMapRef]` pulls every key in as an env var —
same names, no translation — and the JAR reads them via `System.getenv()`
through the YAML `${VAR}` placeholders.

## What we deliver vs what ops owns

We deliver:
- The JAR (`filemanager-core/target/filemanager-core.jar`)
- The env-var contract (`.env.example`)
- This reference (`env.example.yaml`)

Ops owns:
- The container image (`Dockerfile`, registry, tag)
- The actual `Secret` (sourced from Vault / AKV / SealedSecrets / ExternalSecrets)
- Namespace, naming, replicas, probes, resources, network policies
- The CronJob or Deployment shape (per ADR-0003 the JAR is short-lived → CronJob fits;
  per the scheduler task #47, that's being revisited)

## Common pitfalls

- **Secret keys must be uppercase env-var names**, not `db-password` style.
  Kubernetes accepts kebab-case in Secret keys but the Pod will then expose
  them as kebab-case env vars and the JAR won't find them.
- **A blank `FILES_COM_API_KEY` in the Secret silently disables auth**, which
  then makes the SDK call fail at the first list. The JAR refuses to start
  with a blank required value — but only because `application.yaml` declares
  `${FILES_COM_API_KEY:}` with an empty default for the non-filescom paths.
  In a `FILE_SOURCE=filescom` deploy, set the Secret value or leave the key
  out entirely (and let the gateway's own validation throw a clear error).
- **`sslmode=require` in `DB_URL` for Azure Flexible Postgres.** Per
  ADR-0006, sslmode=disable will be rejected by the server.
