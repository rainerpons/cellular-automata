#!/usr/bin/env bash
set -euo pipefail

mkdir -p target/jpackage-input target/jpackage
JAR_FILE=$(ls target/*.jar | head -n 1)
cp "$JAR_FILE" target/jpackage-input/
VERSION_NUM=$(echo "${VERSION#v}" | sed 's/[-+].*//') # jpackage requires numeric-only version
jpackage \
  --type app-image \
  --dest target/jpackage \
  --name "${APP_DISPLAY_NAME}" \
  --input target/jpackage-input \
  --main-jar "$(basename "$JAR_FILE")" \
  --main-class "${MAIN_CLASS}" \
  --app-version "$VERSION_NUM" \
  --vendor "Rainer Pons"

APP_IMAGE_DIR="target/jpackage/${APP_DISPLAY_NAME}"

# Create minimal PNG icon (1x1 pixel white dot)
printf '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x02\x00\x00\x00\x90wS\xde\x00\x00\x00\x0cIDATx\x9cc\xf8\x0f\x00\x00\x01\x01\x00\x05\x18\r\xc2\x00\x00\x00\x00IEND\xaeB`\x82' > "$APP_IMAGE_DIR/${APP_NAME}.png"

# Create .desktop file for AppImage at root of AppDir
DESKTOP_FILE="$APP_IMAGE_DIR/${APP_NAME}.desktop"
echo "[Desktop Entry]" > "$DESKTOP_FILE"
echo "Type=Application" >> "$DESKTOP_FILE"
echo "Name=${APP_DISPLAY_NAME}" >> "$DESKTOP_FILE"
echo "Exec=${APP_NAME}" >> "$DESKTOP_FILE"
echo "Icon=${APP_NAME}" >> "$DESKTOP_FILE"
echo "Categories=Utility;" >> "$DESKTOP_FILE"

./appimagetool.AppImage "$APP_IMAGE_DIR" "target/jpackage/${APP_NAME}-linux.AppImage"
