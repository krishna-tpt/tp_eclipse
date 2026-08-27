#!/usr/bin/env bash
# =============================================================================
#  check-sftp.sh - verify Michelin SFTP filehub connectivity from WSL / Linux / macOS
#
#  Same env vars as check-sftp.ps1 and as the JAR:
#
#    export SFTP_HOST=filehub.michelin.net
#    export SFTP_PORT=22
#    export SFTP_USER=DEV-BR-C10-EU
#    export SFTP_PRIVATE_KEY_PATH=$HOME/keys/id_rsa
#    export SFTP_KNOWN_HOSTS_PATH=$HOME/keys/known_hosts
#    export SFTP_PICKUP_PATH=/EU/DEV/BR/C10/Inbound
#
#    ./check-sftp.sh
#
#  Requires: bash, openssh-client (sftp), nc OR /dev/tcp (bash builtin).
#  Exit codes match check-sftp.ps1.
# =============================================================================

set -u
IFS=$'\n\t'

# ---- pretty print ----------------------------------------------------------
if [ -t 1 ]; then
    C_G=$'\033[32m'; C_R=$'\033[31m'; C_C=$'\033[36m'; C_Y=$'\033[33m'; C_0=$'\033[0m'
else
    C_G=''; C_R=''; C_C=''; C_Y=''; C_0=''
fi
ok()   { printf '%b[ OK  ]%b %s\n' "$C_G" "$C_0" "$*"; }
fail() { printf '%b[FAIL ]%b %s\n' "$C_R" "$C_0" "$*"; }
info() { printf '%b[INFO ]%b %s\n' "$C_C" "$C_0" "$*"; }
warn() { printf '%b[WARN ]%b %s\n' "$C_Y" "$C_0" "$*"; }

# ---- resolve inputs (env > interactive prompt) -----------------------------
prompt_if_empty() {
    # $1 varname   $2 prompt   $3 secret (yes/no)
    local var="$1" prm="$2" secret="${3:-no}"
    local cur="${!var:-}"
    if [ -z "$cur" ]; then
        if [ "$secret" = "yes" ]; then
            read -r -s -p "$prm: " cur; echo
        else
            read -r -p "$prm: " cur
        fi
        printf -v "$var" '%s' "$cur"
    fi
}

echo
echo "=================================================================="
echo " Michelin SFTP filehub connectivity check"
echo "=================================================================="

prompt_if_empty SFTP_HOST             "SFTP host (e.g. filehub.michelin.net)"
prompt_if_empty SFTP_USER             "SFTP username"
prompt_if_empty SFTP_PRIVATE_KEY_PATH "Private key path"
prompt_if_empty SFTP_PICKUP_PATH      "Pickup folder path"
: "${SFTP_PORT:=22}"
: "${SFTP_KNOWN_HOSTS_PATH:=}"

case "$SFTP_PICKUP_PATH" in
    /*) : ;;
    *)  SFTP_PICKUP_PATH="/${SFTP_PICKUP_PATH}" ;;
esac

echo
info "Host / port  : $SFTP_HOST : $SFTP_PORT"
info "User         : $SFTP_USER"
info "Key file     : $SFTP_PRIVATE_KEY_PATH"
if [ -n "$SFTP_KNOWN_HOSTS_PATH" ]; then
    info "Known hosts  : $SFTP_KNOWN_HOSTS_PATH"
else
    info "Known hosts  : (none - will use accept-new / TOFU)"
fi
info "Pickup path  : $SFTP_PICKUP_PATH"
echo

# ---- 0. tools ---------------------------------------------------------------
if ! command -v sftp >/dev/null 2>&1; then
    fail "sftp not found on PATH. Install openssh-client:"
    warn "  sudo apt-get install openssh-client   (Debian/Ubuntu/WSL)"
    exit 10
fi
ok "sftp found at $(command -v sftp)"

# ---- 1. key file exists + permissions --------------------------------------
if [ ! -f "$SFTP_PRIVATE_KEY_PATH" ]; then
    fail "Private key file NOT FOUND at: $SFTP_PRIVATE_KEY_PATH"
    exit 11
fi
if [ ! -r "$SFTP_PRIVATE_KEY_PATH" ]; then
    fail "Private key file not readable (permissions?): $SFTP_PRIVATE_KEY_PATH"
    exit 12
fi
ok "Private key file readable"
mode="$(stat -c '%a' "$SFTP_PRIVATE_KEY_PATH" 2>/dev/null || stat -f '%A' "$SFTP_PRIVATE_KEY_PATH" 2>/dev/null || echo '')"
if [ -n "$mode" ] && [ "$mode" != "600" ] && [ "$mode" != "400" ]; then
    warn "Key file permissions are $mode. OpenSSH may reject it."
    warn "Fix: chmod 600 \"$SFTP_PRIVATE_KEY_PATH\""
fi

# ---- 1b. known_hosts sanity -------------------------------------------------
use_strict=0
if [ -n "$SFTP_KNOWN_HOSTS_PATH" ]; then
    if [ ! -f "$SFTP_KNOWN_HOSTS_PATH" ]; then
        warn "known_hosts file not found at: $SFTP_KNOWN_HOSTS_PATH"
        warn "Falling back to accept-new (TOFU) for this run."
    elif grep -q -F "$SFTP_HOST" "$SFTP_KNOWN_HOSTS_PATH" 2>/dev/null; then
        ok "known_hosts contains an entry for $SFTP_HOST"
        use_strict=1
    else
        warn "known_hosts is present but has no entry for $SFTP_HOST"
        warn "Falling back to accept-new (TOFU) for this run - entry will be appended."
    fi
fi

# ---- 2. DNS -----------------------------------------------------------------
if ! ips=$(getent hosts "$SFTP_HOST" 2>/dev/null); then
    if ! ips=$(host "$SFTP_HOST" 2>/dev/null); then
        fail "DNS lookup failed for $SFTP_HOST"
        exit 2
    fi
fi
first_ip="$(printf '%s\n' "$ips" | awk 'NR==1 {print $1}')"
ok "DNS resolves $SFTP_HOST -> $first_ip"

# ---- 3. TCP:port reachable --------------------------------------------------
tcp_ok=0
if command -v nc >/dev/null 2>&1; then
    if nc -z -w 5 "$SFTP_HOST" "$SFTP_PORT" >/dev/null 2>&1; then tcp_ok=1; fi
else
    # bash /dev/tcp fallback (works in bash even without nc)
    if timeout 5 bash -c ": < /dev/tcp/$SFTP_HOST/$SFTP_PORT" >/dev/null 2>&1; then tcp_ok=1; fi
fi
if [ $tcp_ok -eq 1 ]; then
    ok "TCP :$SFTP_PORT reachable"
else
    fail "TCP :$SFTP_PORT to $SFTP_HOST failed"
    warn "Likely: firewall/proxy blocks outbound port $SFTP_PORT."
    exit 3
fi

# ---- 4. SFTP session + list pickup folder -----------------------------------
tmpdir="$(mktemp -d)"
trap 'rm -rf "$tmpdir"' EXIT
batch="$tmpdir/batch.txt"
{
    echo "cd $SFTP_PICKUP_PATH"
    echo "ls -l"
    echo "bye"
} > "$batch"

sftp_args=(
    -b "$batch"
    -P "$SFTP_PORT"
    -i "$SFTP_PRIVATE_KEY_PATH"
    -o BatchMode=yes
    -o ConnectTimeout=10
    -o PasswordAuthentication=no
    -o IdentitiesOnly=yes
)
if [ $use_strict -eq 1 ]; then
    sftp_args+=( -o StrictHostKeyChecking=yes -o UserKnownHostsFile="$SFTP_KNOWN_HOSTS_PATH" )
elif [ -n "$SFTP_KNOWN_HOSTS_PATH" ]; then
    sftp_args+=( -o StrictHostKeyChecking=accept-new -o UserKnownHostsFile="$SFTP_KNOWN_HOSTS_PATH" )
elif [ "${ACCEPT_HOST_KEY:-}" = "yes" ] || [ "${1:-}" = "--accept-host-key" ]; then
    sftp_args+=( -o StrictHostKeyChecking=accept-new )
else
    fail "No known_hosts and no ACCEPT_HOST_KEY=yes. Refusing to blindly trust the host."
    warn "Fix: set SFTP_KNOWN_HOSTS_PATH, or re-run with ACCEPT_HOST_KEY=yes ./check-sftp.sh"
    exit 30
fi

info "sftp -b <batch> -i <key> ${SFTP_USER}@${SFTP_HOST}:${SFTP_PORT}"

out="$tmpdir/out"; err="$tmpdir/err"
if ! sftp "${sftp_args[@]}" "${SFTP_USER}@${SFTP_HOST}" >"$out" 2>"$err"; then
    rc=$?
    fail "sftp exited with code $rc"
    if [ -s "$err" ]; then
        echo
        echo "  --- stderr ---"
        sed 's/^/  /' "$err"
    fi
    echo
    if grep -q "Host key verification failed"   "$err" 2>/dev/null; then warn "Fix: known_hosts entry is stale/wrong, or use ACCEPT_HOST_KEY=yes once."; exit 20; fi
    if grep -q "UNPROTECTED PRIVATE KEY FILE"    "$err" 2>/dev/null; then warn "Fix: chmod 600 the key file."; exit 21; fi
    if grep -q "Permission denied"               "$err" 2>/dev/null; then warn "Fix: verify username + that this key is authorised on the server."; exit 22; fi
    if grep -q "Could not resolve hostname"      "$err" 2>/dev/null; then warn "Fix: DNS/proxy issue."; exit 23; fi
    if grep -qE "Connection (timed out|refused)" "$err" 2>/dev/null; then warn "Fix: firewall/port issue."; exit 24; fi
    if grep -q "No such file or directory"       "$err" 2>/dev/null; then warn "Fix: pickup path $SFTP_PICKUP_PATH does not exist on the server."; exit 25; fi
    exit 26
fi

ok "SFTP login + folder listing succeeded"
if [ -s "$out" ]; then
    echo
    echo "  --- pickup folder listing ---"
    # strip any sftp> prompts, show first 20 lines
    listed_lines="$(grep -v '^sftp>' "$out" | sed '/^$/d')"
    printf '%s\n' "$listed_lines" | head -20 | sed 's/^/  /'
    total="$(printf '%s\n' "$listed_lines" | wc -l | tr -d ' ')"
    files="$(printf '%s\n' "$listed_lines" | grep -c '^-' || true)"
    if [ "$total" -gt 20 ]; then
        echo "  ... ($((total - 20)) more lines)"
    fi
    echo
    ok "Files visible in pickup folder: $files"
fi

echo
echo "=================================================================="
echo " All checks passed"
echo "=================================================================="
exit 0
