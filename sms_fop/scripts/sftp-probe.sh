#!/usr/bin/env bash
# SFTP connectivity probe against filehub.michelin.net.
# All values inlined — no .env sourcing required. Copy this file anywhere and run.
#
# Usage:
#   scripts/sftp-probe.sh auth          # just verify authentication
#   scripts/sftp-probe.sh list          # auth + list /EU/DEV/BR/C10/Inbound
#   scripts/sftp-probe.sh interactive   # open an interactive sftp> shell

set -eu

# ─── Connection settings (matches what .env / the JAR uses) ──────────────────
SFTP_HOST="filehub.michelin.net"
SFTP_PORT=22
SFTP_USER="DEV-BR-C10-EU"
PRIVATE_KEY="/home/arultpt/.ssh/filehub-michelin/id_rsa"
KNOWN_HOSTS="/home/arultpt/.ssh/filehub-michelin/known_hosts"
REMOTE_DIR="/EU/DEV/BR/C10/Inbound"

# ─── Sanity check — files must exist and be readable ─────────────────────────
[[ -r "$PRIVATE_KEY" ]] || { echo "missing or unreadable: $PRIVATE_KEY" >&2; exit 1; }
[[ -r "$KNOWN_HOSTS" ]] || { echo "missing or unreadable: $KNOWN_HOSTS" >&2; exit 1; }

mode="${1:-auth}"

case "$mode" in
  auth)
    # Auth-only probe. Verbose mode so you can see exactly what fails.
    # Exits 0 on auth success, non-zero on failure. No shell, no file transfer.
    exec ssh -v \
      -i "$PRIVATE_KEY" \
      -o UserKnownHostsFile="$KNOWN_HOSTS" \
      -o StrictHostKeyChecking=yes \
      -o PreferredAuthentications=publickey \
      -o BatchMode=yes \
      -o ConnectTimeout=10 \
      -p "$SFTP_PORT" \
      "$SFTP_USER@$SFTP_HOST" exit
    ;;

  list)
    # Auth + list the Inbound folder, batch mode (no shell).
    echo "ls -la $REMOTE_DIR" | \
    exec sftp \
      -i "$PRIVATE_KEY" \
      -o UserKnownHostsFile="$KNOWN_HOSTS" \
      -o StrictHostKeyChecking=yes \
      -o PreferredAuthentications=publickey \
      -o ConnectTimeout=10 \
      -P "$SFTP_PORT" \
      -b - \
      "$SFTP_USER@$SFTP_HOST"
    ;;

  interactive)
    # Open an interactive sftp> shell. Use `ls`, `cd`, `get`, `exit` at the prompt.
    exec sftp \
      -i "$PRIVATE_KEY" \
      -o UserKnownHostsFile="$KNOWN_HOSTS" \
      -o StrictHostKeyChecking=yes \
      -o ConnectTimeout=10 \
      -P "$SFTP_PORT" \
      "$SFTP_USER@$SFTP_HOST"
    ;;

  *)
    echo "usage: $0 {auth|list|interactive}" >&2
    exit 2
    ;;
esac
