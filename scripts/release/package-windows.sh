#!/usr/bin/env bash
set -euo pipefail

mkdir -p target/jpackage-input target/jpackage
JAR_FILE=$(ls target/*.jar | head -n 1)
cp "$JAR_FILE" target/jpackage-input/
VERSION_NUM="${VERSION#v}"
jpackage \
  --type exe \
  --dest target/jpackage \
  --name "${APP_DISPLAY_NAME}" \
  --input target/jpackage-input \
  --main-jar "$(basename "$JAR_FILE")" \
  --main-class "${MAIN_CLASS}" \
  --app-version "$VERSION_NUM" \
  --vendor "Rainer Pons"
mv target/jpackage/*.exe target/jpackage/${APP_NAME}-windows.exe
