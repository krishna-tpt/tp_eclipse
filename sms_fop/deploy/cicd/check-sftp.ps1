# =============================================================================
#  check-sftp.ps1 - verify Michelin SFTP filehub connectivity from VDI
#
#  Reads the same env vars the JAR uses. If you have already set:
#
#    $env:SFTP_HOST             = 'filehub.michelin.net'
#    $env:SFTP_PORT             = '22'
#    $env:SFTP_USER             = 'DEV-BR-C10-EU'
#    $env:SFTP_PRIVATE_KEY_PATH = 'C:\path\to\id_rsa'
#    $env:SFTP_KNOWN_HOSTS_PATH = 'C:\path\to\known_hosts'
#    $env:SFTP_PICKUP_PATH      = '/EU/DEV/BR/C10/Inbound'
#
#  then simply:
#
#    .\check-sftp.ps1
#
#  Parameters override env vars. Anything still missing is prompted for.
#
#  When SFTP_KNOWN_HOSTS_PATH points at a real file, the script pins the host
#  key against it (strict). Otherwise it warns and uses accept-new (TOFU).
#
#  Requires PowerShell 5.1+ and OpenSSH client (Get-Command sftp.exe).
#  Install if missing (elevated PowerShell):
#    Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
#
#  ASCII only.
# =============================================================================

param(
    [string]$SftpHost       = "",
    [int]   $Port           = 0,
    [string]$User           = "",
    [string]$KeyPath        = "",
    [string]$KnownHostsPath = "",
    [string]$PickupPath     = "",
    [switch]$AcceptHostKey                # ignored when KnownHostsPath is a real file
)

$ErrorActionPreference = "Stop"

function Write-OK   ($msg) { Write-Host "[ OK  ] $msg" -ForegroundColor Green }
function Write-Fail ($msg) { Write-Host "[FAIL ] $msg" -ForegroundColor Red   }
function Write-Info ($msg) { Write-Host "[INFO ] $msg" -ForegroundColor Cyan  }
function Write-Warn ($msg) { Write-Host "[WARN ] $msg" -ForegroundColor Yellow }

function Coalesce {
    param([string]$paramVal, [string]$envName, [string]$prompt)
    if (-not [string]::IsNullOrWhiteSpace($paramVal)) { return $paramVal }
    $envVal = [Environment]::GetEnvironmentVariable($envName)
    if (-not [string]::IsNullOrWhiteSpace($envVal))   { return $envVal }
    if ([string]::IsNullOrWhiteSpace($prompt))        { return "" }
    return Read-Host $prompt
}

Write-Host ""
Write-Host "=================================================================="
Write-Host " Michelin SFTP filehub connectivity check"
Write-Host "=================================================================="

# --- resolve inputs (param > env > prompt) ------------------------------------
$SftpHost       = Coalesce $SftpHost       "SFTP_HOST"             "SFTP host (e.g. filehub.michelin.net)"
$User           = Coalesce $User           "SFTP_USER"             "SFTP username"
$KeyPath        = Coalesce $KeyPath        "SFTP_PRIVATE_KEY_PATH" "Private key path"
$PickupPath     = Coalesce $PickupPath     "SFTP_PICKUP_PATH"      "Pickup folder path"
$KnownHostsPath = Coalesce $KnownHostsPath "SFTP_KNOWN_HOSTS_PATH" ""

if ($Port -le 0) {
    $envPort = [Environment]::GetEnvironmentVariable("SFTP_PORT")
    if ($envPort -and ($envPort -match '^\d+$')) { $Port = [int]$envPort } else { $Port = 22 }
}
if (-not $PickupPath.StartsWith('/')) { $PickupPath = "/" + $PickupPath }

Write-Host ""
Write-Info "Host / port  : $SftpHost : $Port"
Write-Info "User         : $User"
Write-Info "Key file     : $KeyPath"
if ($KnownHostsPath) {
    Write-Info "Known hosts  : $KnownHostsPath"
} else {
    Write-Info "Known hosts  : (none - will use accept-new / TOFU)"
}
Write-Info "Pickup path  : $PickupPath"
Write-Host ""

# --- 0. tools present ---------------------------------------------------------
$sftpExe = Get-Command sftp.exe -ErrorAction SilentlyContinue
if (-not $sftpExe) {
    Write-Fail "sftp.exe not found on PATH. Install OpenSSH client:"
    Write-Warn "  Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0"
    exit 10
}
Write-OK "sftp.exe found at $($sftpExe.Source)"

# --- 1. key file exists + readable + ACL sanity ------------------------------
if (-not (Test-Path -LiteralPath $KeyPath)) {
    Write-Fail "Private key file NOT FOUND at: $KeyPath"
    exit 11
}
try {
    Get-Content -LiteralPath $KeyPath -TotalCount 1 | Out-Null
    Write-OK "Private key file readable"
} catch {
    Write-Fail "Cannot read key file (permissions?): $($_.Exception.Message)"
    exit 12
}
try {
    $acl = Get-Acl -LiteralPath $KeyPath
    $everyone = $acl.Access | Where-Object {
        $_.IdentityReference -match "Everyone|Users|Authenticated Users"
    }
    if ($everyone) {
        Write-Warn "Key file has broad ACL. OpenSSH may reject it with 'UNPROTECTED PRIVATE KEY FILE'."
        Write-Warn "Fix: right-click the key -> Properties -> Security -> remove Users/Everyone; keep only your user."
    }
} catch {
    # non-fatal - some VDI profiles restrict Get-Acl
}

# --- 1b. known_hosts sanity --------------------------------------------------
$useStrict = $false
if ($KnownHostsPath) {
    if (-not (Test-Path -LiteralPath $KnownHostsPath)) {
        Write-Warn "known_hosts file not found at: $KnownHostsPath"
        Write-Warn "Falling back to accept-new (TOFU) for this run."
    } else {
        $hostLine = Select-String -Path $KnownHostsPath -Pattern ([regex]::Escape($SftpHost)) -SimpleMatch -List -ErrorAction SilentlyContinue
        if ($hostLine) {
            Write-OK "known_hosts contains an entry for $SftpHost"
            $useStrict = $true
        } else {
            Write-Warn "known_hosts is present but has no entry for $SftpHost"
            Write-Warn "Falling back to accept-new (TOFU) for this run - entry will be appended."
        }
    }
}

# --- 2. DNS resolution --------------------------------------------------------
try {
    $dns = [Net.Dns]::GetHostAddresses($SftpHost)
    Write-OK ("DNS resolves {0} -> {1}" -f $SftpHost, ($dns[0].IPAddressToString))
} catch {
    Write-Fail "DNS lookup failed for $SftpHost : $($_.Exception.Message)"
    exit 2
}

# --- 3. TCP:$Port reachable ---------------------------------------------------
try {
    $client = New-Object Net.Sockets.TcpClient
    $iar = $client.BeginConnect($SftpHost, $Port, $null, $null)
    if (-not $iar.AsyncWaitHandle.WaitOne(5000)) {
        throw "connect timeout after 5s"
    }
    $client.EndConnect($iar)
    $client.Close()
    Write-OK "TCP :$Port reachable"
} catch {
    Write-Fail "TCP :$Port to $SftpHost failed: $($_.Exception.Message)"
    Write-Warn "Likely: firewall/proxy blocks outbound port $Port. Contact Michelin network team."
    exit 3
}

# --- 4. SFTP session + list pickup folder ------------------------------------
$batchFile = [IO.Path]::GetTempFileName()
$outStdout = "$batchFile.stdout"
$outStderr = "$batchFile.stderr"
try {
    @"
cd $PickupPath
ls -l
bye
"@ | Set-Content -LiteralPath $batchFile -Encoding ASCII

    $sftpArgs = @(
        "-b", $batchFile,
        "-P", $Port,
        "-i", $KeyPath,
        "-o", "BatchMode=yes",
        "-o", "ConnectTimeout=10",
        "-o", "PasswordAuthentication=no",
        "-o", "IdentitiesOnly=yes"
    )

    if ($useStrict) {
        $sftpArgs += @("-o", "StrictHostKeyChecking=yes",
                       "-o", "UserKnownHostsFile=$KnownHostsPath")
    } elseif ($KnownHostsPath) {
        # append to the user's known_hosts on first sight
        $sftpArgs += @("-o", "StrictHostKeyChecking=accept-new",
                       "-o", "UserKnownHostsFile=$KnownHostsPath")
    } elseif ($AcceptHostKey) {
        $sftpArgs += @("-o", "StrictHostKeyChecking=accept-new")
    } else {
        # Neither known_hosts nor -AcceptHostKey. Refuse rather than silently TOFU.
        Write-Fail "No known_hosts and no -AcceptHostKey. Refusing to blindly trust the host."
        Write-Warn "Fix: set `$env:SFTP_KNOWN_HOSTS_PATH, or re-run with -AcceptHostKey."
        exit 30
    }
    $sftpArgs += "$User@$SftpHost"

    Write-Info "sftp -b <batch> -i <key> $User@${SftpHost}:$Port"

    $proc = Start-Process -FilePath "sftp.exe" -ArgumentList $sftpArgs `
                          -NoNewWindow -Wait -PassThru `
                          -RedirectStandardOutput $outStdout `
                          -RedirectStandardError  $outStderr
    $stdout = if (Test-Path $outStdout) { Get-Content -LiteralPath $outStdout -Raw } else { "" }
    $stderr = if (Test-Path $outStderr) { Get-Content -LiteralPath $outStderr -Raw } else { "" }

    if ($proc.ExitCode -ne 0) {
        Write-Fail "sftp exited with code $($proc.ExitCode)"
        if ($stderr) {
            Write-Host ""
            Write-Host "  --- stderr ---" -ForegroundColor Gray
            $stderr.TrimEnd() -split "`n" | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
        }
        Write-Host ""
        if     ($stderr -match "Host key verification failed") { Write-Warn "Fix: known_hosts entry for $SftpHost is stale/wrong, or use -AcceptHostKey once to accept." ; exit 20 }
        elseif ($stderr -match "UNPROTECTED PRIVATE KEY FILE") { Write-Warn "Fix: tighten NTFS ACL on the key file (owner only)." ; exit 21 }
        elseif ($stderr -match "Permission denied")            { Write-Warn "Fix: verify username, key correctness, and that the key is authorised on the server." ; exit 22 }
        elseif ($stderr -match "Could not resolve hostname")   { Write-Warn "Fix: DNS/proxy issue - check VDI network." ; exit 23 }
        elseif ($stderr -match "Connection timed out|refused") { Write-Warn "Fix: firewall/port issue." ; exit 24 }
        elseif ($stderr -match "No such file or directory")    { Write-Warn "Fix: pickup path $PickupPath does not exist on the server." ; exit 25 }
        else                                                    { exit 26 }
    }

    Write-OK "SFTP login + folder listing succeeded"
    if ($stdout) {
        Write-Host ""
        Write-Host "  --- pickup folder listing ---" -ForegroundColor Gray
        $lines = $stdout.TrimEnd() -split "`n" | Where-Object { $_ -and ($_ -notmatch "^sftp>") }
        $fileLines = $lines | Where-Object { $_ -match "^-" }
        $lines | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
        if ($lines.Count -gt 20) { Write-Host ("  ... ({0} more lines)" -f ($lines.Count - 20)) -ForegroundColor Gray }
        Write-Host ""
        Write-OK ("Files visible in pickup folder: {0}" -f $fileLines.Count)
    }
} finally {
    Remove-Item -LiteralPath $batchFile -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $outStdout -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $outStderr -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "=================================================================="
Write-Host " All checks passed" -ForegroundColor Green
Write-Host "=================================================================="
exit 0
