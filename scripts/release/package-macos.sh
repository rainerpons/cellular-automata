#!/usr/bin/env bash
set -euo pipefail

mkdir -p target/jpackage-input target/jpackage
JAR_FILE=$(ls target/*.jar | head -n 1)
cp "$JAR_FILE" target/jpackage-input/
VERSION_NUM=$(echo "${VERSION#v}" | sed 's/[-+].*//') # jpackage requires numeric-only version
jpackage \
  --type dmg \
  --dest target/jpackage \
  --name "${APP_DISPLAY_NAME}" \
  --input target/jpackage-input \
  --main-jar "$(basename "$JAR_FILE")" \
  --main-class "${MAIN_CLASS}" \
  --app-version "$VERSION_NUM" \
  --vendor "Rainer Pons"
mv target/jpackage/*.dmg target/jpackage/${APP_NAME}-macos.dmg
