@echo off
REM ============================================================================
REM  check-sftp.bat - VDI-friendly wrapper around check-sftp.ps1
REM
REM  Usage:
REM    check-sftp.bat
REM
REM  If your VDI blocks .ps1 execution, run the equivalent sftp one-liner
REM  from the "MANUAL FALLBACK" comment block below.
REM ============================================================================

setlocal

set "SCRIPT=%~dp0check-sftp.ps1"

if not exist "%SCRIPT%" (
    echo [FAIL ] check-sftp.ps1 not found next to this .bat
    exit /b 1
)

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%" %*
set RC=%ERRORLEVEL%

echo.
if %RC% NEQ 0 (
    echo [FAIL ] check-sftp.ps1 exited with code %RC%
) else (
    echo [ OK  ] SFTP reachable and pickup path listable
)
exit /b %RC%

REM ----------------------------------------------------------------------------
REM  MANUAL FALLBACK (if PowerShell is disabled on your VDI)
REM
REM  Set your values then run from CMD:
REM
REM    set SFTPHOST=sftp.michelin.net
REM    set SFTPUSER=michca
REM    set SFTPKEY=C:\keys\michca_id_rsa
REM    set FOLDER=/EU/SBX/BR/C10/Inbound
REM
REM  1) TCP reach test:
REM
REM    powershell -c "Test-NetConnection %SFTPHOST% -Port 22"
REM
REM  2) Interactive sftp (accepts host key on first run):
REM
REM    sftp -i %SFTPKEY% -o StrictHostKeyChecking=accept-new %SFTPUSER%@%SFTPHOST%
REM    sftp^> cd /EU/SBX/BR/C10/Inbound
REM    sftp^> ls -l
REM    sftp^> bye
REM
REM  3) One-shot non-interactive (needs a batchfile.txt with the cd+ls+bye):
REM
REM    sftp -b batchfile.txt -i %SFTPKEY% %SFTPUSER%@%SFTPHOST%
REM ----------------------------------------------------------------------------
