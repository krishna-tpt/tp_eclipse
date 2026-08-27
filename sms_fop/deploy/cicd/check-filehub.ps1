# =============================================================================
#  check-filehub.ps1 - verify files.com (filehub) connectivity from VDI
#
#  Usage (either):
#    .\check-filehub.ps1
#      (prompts for API key + URL + folder)
#
#    .\check-filehub.ps1 -ApiKey "xxx" -BaseUrl "https://filehub.michelin.net" `
#                        -PickupPath "/EU/SBX/BR/C10/Inbound"
#
#  Requires: PowerShell 5.1+ (built into Windows 10/11). No extra install.
#  ASCII only - no fancy dashes/quotes - so it loads on any Windows codepage.
# =============================================================================

param(
    [string]$ApiKey     = "",
    [string]$BaseUrl    = "https://filehub.michelin.net",
    [string]$PickupPath = "/EU/SBX/BR/C10/Inbound"
)

$ErrorActionPreference = "Stop"

function Write-OK    ($msg) { Write-Host "[ OK  ] $msg" -ForegroundColor Green }
function Write-Fail  ($msg) { Write-Host "[FAIL ] $msg" -ForegroundColor Red   }
function Write-Info  ($msg) { Write-Host "[INFO ] $msg" -ForegroundColor Cyan  }
function Write-Warn  ($msg) { Write-Host "[WARN ] $msg" -ForegroundColor Yellow }

Write-Host ""
Write-Host "=================================================================="
Write-Host " Michelin filehub connectivity check"
Write-Host "=================================================================="

# --- prompt for missing inputs ------------------------------------------------
if ([string]::IsNullOrWhiteSpace($ApiKey)) {
    $secure  = Read-Host -AsSecureString "Files.com API key"
    $ApiKey  = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure))
}
if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = Read-Host "Base URL (default https://filehub.michelin.net)"
    if ([string]::IsNullOrWhiteSpace($BaseUrl)) { $BaseUrl = "https://filehub.michelin.net" }
}
if ([string]::IsNullOrWhiteSpace($PickupPath)) {
    $PickupPath = Read-Host "Pickup path (e.g. /EU/SBX/BR/C10/Inbound)"
}

$BaseUrl = $BaseUrl.TrimEnd('/')
if (-not $PickupPath.StartsWith('/')) { $PickupPath = "/" + $PickupPath }

Write-Host ""
Write-Info "Base URL     : $BaseUrl"
Write-Info "Pickup path  : $PickupPath"
$keyPreview = $ApiKey.Substring(0, [Math]::Min(4, $ApiKey.Length))
Write-Info "API key      : $keyPreview****"
Write-Host ""

# --- 1. DNS resolution --------------------------------------------------------
$hostname = ([Uri]$BaseUrl).Host
try {
    $dns = [Net.Dns]::GetHostAddresses($hostname)
    Write-OK ("DNS resolves {0} -> {1}" -f $hostname, ($dns[0].IPAddressToString))
} catch {
    Write-Fail "DNS lookup failed for $hostname : $($_.Exception.Message)"
    exit 2
}

# --- 2. TCP:443 reachable -----------------------------------------------------
try {
    $client = New-Object Net.Sockets.TcpClient
    $iar = $client.BeginConnect($hostname, 443, $null, $null)
    if (-not $iar.AsyncWaitHandle.WaitOne(5000)) {
        throw "connect timeout after 5s"
    }
    $client.EndConnect($iar)
    $client.Close()
    Write-OK "TCP :443 reachable"
} catch {
    Write-Fail "TCP :443 to $hostname failed: $($_.Exception.Message)"
    Write-Warn "Likely cause: firewall / proxy blocks outbound HTTPS. Contact Michelin network team."
    exit 3
}

# --- 3. Auth check (list root - should not 401) ------------------------------
$headers = @{
    "X-FilesAPI-Key" = $ApiKey
    "Accept"         = "application/json"
    "User-Agent"     = "michca-filehub-smoke/1.0"
}

$rootUrl = "$BaseUrl/api/rest/v1/folders/"
Write-Info "GET $rootUrl"
try {
    $resp = Invoke-WebRequest -Uri $rootUrl -Headers $headers -Method GET -TimeoutSec 15 -UseBasicParsing
    if ($resp.StatusCode -eq 200) {
        Write-OK "Auth accepted (HTTP 200 on root)"
    } else {
        Write-Warn "Unexpected status $($resp.StatusCode) on root"
    }
} catch [Net.WebException] {
    $sc = $null
    if ($_.Exception.Response) { $sc = [int]$_.Exception.Response.StatusCode }
    if ($sc -eq 401) {
        Write-Fail "Auth REJECTED (401 Unauthorized) - API key is wrong or expired"
        exit 4
    } elseif ($sc -eq 403) {
        Write-Fail "Auth ACCEPTED but FORBIDDEN (403) - key valid, no permission"
        exit 5
    } else {
        Write-Fail "HTTP call failed: $($_.Exception.Message)"
        exit 6
    }
}

# --- 4. Pickup folder listable + count files ---------------------------------
$encodedPath = [Uri]::EscapeDataString($PickupPath.TrimStart('/'))
$folderUrl   = "$BaseUrl/api/rest/v1/folders/$encodedPath"
Write-Info "GET $folderUrl"
try {
    $resp = Invoke-WebRequest -Uri $folderUrl -Headers $headers -Method GET -TimeoutSec 30 -UseBasicParsing
    $items = $resp.Content | ConvertFrom-Json
    $files = @($items | Where-Object { $_.type -eq "file" })
    $dirs  = @($items | Where-Object { $_.type -eq "directory" })
    Write-OK ("Pickup folder listable - {0} file(s), {1} subfolder(s)" -f $files.Count, $dirs.Count)

    if ($files.Count -gt 0) {
        Write-Host ""
        Write-Host "  Latest 5 files:" -ForegroundColor Gray
        $files |
            Sort-Object -Property mtime -Descending |
            Select-Object -First 5 |
            ForEach-Object {
                "    {0,-55}  {1,10} bytes  {2}" -f $_.display_name, $_.size, $_.mtime
            } | Write-Host
    }
} catch [Net.WebException] {
    $sc = $null
    if ($_.Exception.Response) { $sc = [int]$_.Exception.Response.StatusCode }
    if ($sc -eq 404) {
        Write-Fail "Pickup path $PickupPath NOT FOUND - check the folder is provisioned"
        exit 7
    } elseif ($sc -eq 403) {
        Write-Fail "No permission to list $PickupPath - API key needs read on this path"
        exit 8
    } else {
        Write-Fail "Folder listing failed: $($_.Exception.Message)"
        exit 9
    }
}

Write-Host ""
Write-Host "=================================================================="
Write-Host " All checks passed" -ForegroundColor Green
Write-Host "=================================================================="
exit 0
