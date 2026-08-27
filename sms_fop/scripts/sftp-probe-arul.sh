#!/usr/bin/env bash
# Diagnostic SFTP probe against filehub.michelin.net using the
# arul.mani_ext@michelin.com user (a control test to isolate the
# DEV-C10-EU failure: IP-allow-list vs username/key mismatch).
#
# If THIS probe succeeds and the DEV-C10-EU probe fails from the same
# machine → the issue is on the DEV-C10-EU side (wrong username form,
# or public key not registered against that account).
# If THIS probe also fails → IP allow-list is the bottleneck.
#
# Usage:
#   scripts/sftp-probe-arul.sh auth          # verify authentication
#   scripts/sftp-probe-arul.sh list          # auth + list /EU/DEV/BR/C10/Inbound
#   scripts/sftp-probe-arul.sh interactive   # open an interactive sftp> shell

set -eu

# ─── Connection settings (the known-working combo from 2026-06-09) ───────────
SFTP_HOST="filehub.michelin.net"
SFTP_PORT=22
SFTP_USER="arul.mani_ext@michelin.com"
REMOTE_DIR="/EU/DEV/BR/C10/Inbound"

# Where the old private key needs to live. This file was DELETED on 2026-06-10
# during the credential-rotation cleanup — restore it from your backup / re-
# request from Michelin admin before running this script.
PRIVATE_KEY="$HOME/.ssh/filehub-michelin/id_rsa_arul"
KNOWN_HOSTS="$HOME/.ssh/filehub-michelin/known_hosts"

# ─── Sanity check — files must exist and be readable ─────────────────────────
if [[ ! -r "$PRIVATE_KEY" ]]; then
    cat >&2 <<EOF
✗ Old private key not found at: $PRIVATE_KEY

This file was deleted earlier in the credential rotation. To run this
diagnostic, restore it first:

  # If you have the old PEM file:
  tr -d '\r' < /path/to/old/private/key.txt > $PRIVATE_KEY
  chmod 600 $PRIVATE_KEY

  # Then re-run this script.

Public key from the old keypair (so Michelin can confirm the right pair):
  sms-fop-integration-ssh
EOF
    exit 1
fi
[[ -r "$KNOWN_HOSTS" ]] || { echo "missing or unreadable: $KNOWN_HOSTS" >&2; exit 1; }

# ─── Probe ───────────────────────────────────────────────────────────────────
mode="${1:-auth}"

case "$mode" in
  auth)
    # Auth-only probe. Verbose mode so the exact failure reason is visible.
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
    # Auth + list the Inbound folder via sftp batch mode.
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
