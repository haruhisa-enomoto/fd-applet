@echo off
setlocal enabledelayedexpansion

:: Set variables for URLs and paths
set "BASE_PATH=%~dp0"
set "VERSION_URL=https://haruhisa-enomoto.github.io/files/fd-applet-version.txt"
set "JAR_URL=https://haruhisa-enomoto.github.io/files/fd-applet-fat.jar"
set "VERSION_FILE_PATH=%BASE_PATH%lib\fd-applet-version.txt"

:: Check for updates
echo Checking for updates...
call :http_request "%VERSION_URL%" latest_version.tmp VERSION_HTTP_STATUS
if not "!VERSION_HTTP_STATUS!"=="200" (
    echo Network error. Skipping update check...
    del latest_version.tmp
) else (
    set /p LATEST_VERSION=<latest_version.tmp
    del latest_version.tmp
    set /p CURRENT_VERSION=<"%VERSION_FILE_PATH%"
    echo Latest version: !LATEST_VERSION!
    echo Current version: !CURRENT_VERSION!

    if not "!LATEST_VERSION!"=="!CURRENT_VERSION!" (
        echo New version found: !LATEST_VERSION!
        echo Updating...
        :: Backup the existing jar file
        move /Y "%BASE_PATH%lib\fd-applet-fat.jar" "%BASE_PATH%lib\fd-applet-fat.jar.bak" > nul
        
        :: Download the new jar file
        call :http_request "%JAR_URL%" "%BASE_PATH%lib\fd-applet-fat.jar" HTTP_STATUS

        if not "!HTTP_STATUS!"=="200" (
            echo Error while downloading the JAR. Restoring the backup...
            :: Restore the backup
            move /Y "%BASE_PATH%lib\fd-applet-fat.jar.bak" "%BASE_PATH%lib\fd-applet-fat.jar" > nul
        ) else (
            echo Update completed. New version: !LATEST_VERSION!
            echo !LATEST_VERSION!> "%VERSION_FILE_PATH%"
            :: Remove the backup
            del "%BASE_PATH%lib\fd-applet-fat.jar.bak"
        )
    ) else (
        echo You are already running the latest version: !CURRENT_VERSION!
    )
)

:: Start the server
echo Starting FD Applet...
start cmd /c "cd /D %BASE_PATH% && java -jar lib\fd-applet-fat.jar"

:: Open the browser
timeout /t 5 /nobreak > nul
start http://localhost:8080

exit /b

:: Function to perform HTTP requests
:http_request
set "URL=%~1"
set "OUTPUT_FILE=%~2"
set "RETURN_VARIABLE=%~3"

curl -L -s -w "%%{http_code}" -o "%OUTPUT_FILE%" "%URL%" > http_status.tmp
set /p %RETURN_VARIABLE%=<http_status.tmp
del http_status.tmp
exit /b
