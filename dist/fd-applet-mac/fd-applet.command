#!/bin/bash

# Function to perform HTTP requests
http_request() {
    URL="$1"
    OUTPUT_FILE="$2"
    RETURN_VARIABLE="$3"

    HTTP_STATUS=$(curl -L -s -w "%{http_code}" -o "${OUTPUT_FILE}" "${URL}")
    printf -v "${RETURN_VARIABLE}" "%s" "${HTTP_STATUS}"
}

# Set variables for URLs and paths
BASE_PATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
VERSION_URL="https://haruhisa-enomoto.github.io/files/fd-applet-version.txt"
JAR_URL="https://haruhisa-enomoto.github.io/files/fd-applet-fat.jar"
VERSION_FILE_PATH="${BASE_PATH}/lib/fd-applet-version.txt"

# Check for updates
echo "Checking for updates..."
http_request "${VERSION_URL}" "latest_version.tmp" "VERSION_HTTP_STATUS"

if [ ! "${VERSION_HTTP_STATUS}" = "200" ]; then
    echo "Network error. Skipping update check..."
    rm latest_version.tmp
else
    LATEST_VERSION=$(cat latest_version.tmp)
    rm latest_version.tmp
    CURRENT_VERSION=$(cat "${VERSION_FILE_PATH}")
    echo "Latest version: ${LATEST_VERSION}"
    echo "Current version: ${CURRENT_VERSION}"

    if [ ! "${LATEST_VERSION}" = "${CURRENT_VERSION}" ]; then
        echo "New version found: ${LATEST_VERSION}"
        echo "Updating..."
        # Backup the existing jar file
        mv "${BASE_PATH}/lib/fd-applet-fat.jar" "${BASE_PATH}/lib/fd-applet-fat.jar.bak"
        
        # Download the new jar file
        http_request "${JAR_URL}" "${BASE_PATH}/lib/fd-applet-fat.jar" "HTTP_STATUS"

        if [ ! "${HTTP_STATUS}" = "200" ]; then
            echo "Error while downloading the JAR. Restoring the backup..."
            # Restore the backup
            mv "${BASE_PATH}/lib/fd-applet-fat.jar.bak" "${BASE_PATH}/lib/fd-applet-fat.jar"
        else
            echo "Update completed. New version: ${LATEST_VERSION}"
            echo "${LATEST_VERSION}" > "${VERSION_FILE_PATH}"
            # Remove the backup
            rm "${BASE_PATH}/lib/fd-applet-fat.jar.bak"
        fi
    else
        echo "You are already running the latest version: ${CURRENT_VERSION}"
    fi
fi

# Start the server
echo "Starting FD Applet..."
osascript -e "tell app \"Terminal\" to do script \"cd '$BASE_PATH' && java -jar lib/fd-applet-fat.jar\""


# Open the browser
sleep 5
open http://localhost:8080
