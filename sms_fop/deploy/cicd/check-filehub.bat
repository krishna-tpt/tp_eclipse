@echo off
REM ============================================================================
REM  check-filehub.bat - VDI-friendly wrapper around check-filehub.ps1
REM
REM  Usage:
REM    check-filehub.bat
REM
REM  If your VDI blocks .ps1 execution, run the equivalent curl one-liner
REM  from the "MANUAL FALLBACK" comment block below.
REM ============================================================================

setlocal

set "SCRIPT=%~dp0check-filehub.ps1"

if not exist "%SCRIPT%" (
    echo [FAIL ] check-filehub.ps1 not found next to this .bat
    exit /b 1
)

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%" %*
set RC=%ERRORLEVEL%

echo.
if %RC% NEQ 0 (
    echo [FAIL ] check-filehub.ps1 exited with code %RC%
) else (
    echo [ OK  ] filehub reachable and pickup path listable
)
exit /b %RC%

REM ----------------------------------------------------------------------------
REM  MANUAL FALLBACK (if PowerShell is disabled on your VDI)
REM
REM  Set your values then run these two curl commands from CMD:
REM
REM    set BASE=https://filehub.michelin.net
REM    set KEY=your-files-com-api-key
REM    set FOLDER=/EU/SBX/BR/C10/Inbound
REM
REM  1) Auth check (should return HTTP 200):
REM
REM    curl -sS -o NUL -w "HTTP %%{http_code}\n" ^
REM         -H "X-FilesAPI-Key: %KEY%" ^
REM         "%BASE%/api/rest/v1/folders/"
REM
REM  2) Pickup folder listable (should return a JSON array):
REM
REM    curl -sS -H "X-FilesAPI-Key: %KEY%" ^
REM         "%BASE%/api/rest/v1/folders/EU/SBX/BR/C10/Inbound"
REM ----------------------------------------------------------------------------
