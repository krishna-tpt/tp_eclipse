@echo off
REM Windows launcher for the inventory test data generator.
REM Usage: run.bat [extra args]
REM Reads defaults from local.properties (create from sample-config.properties first).

setlocal
set HERE=%~dp0
set JAR=%HERE%target\inventory-test-data-generator.jar
set LIB=%HERE%target\lib\*

if not exist "%JAR%" (
    echo Build first: mvn -q clean package
    exit /b 1
)
if not exist "%HERE%local.properties" (
    echo local.properties not found. Copy sample-config.properties to local.properties first.
    exit /b 1
)

java -cp "%JAR%;%LIB%" com.michelin.inventorytest.InventoryTestDataLoader --config "%HERE%local.properties" %*
endlocal
