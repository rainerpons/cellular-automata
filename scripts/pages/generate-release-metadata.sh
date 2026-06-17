#!/usr/bin/env bash

# Generates site/release.json for the GitHub Pages deployment.
#
# Required environment variables:
#   APP_VERSION       - Full release version string (e.g. v1.1.0-test-1)
#   COMMIT_SHA        - Git commit SHA of the deployed revision
#   GITHUB_REPOSITORY - GitHub repository name (e.g. owner/repo)

set -euo pipefail

: "${APP_VERSION:?Missing required environment variable APP_VERSION}"
: "${COMMIT_SHA:?Missing required environment variable COMMIT_SHA}"
: "${GITHUB_REPOSITORY:?Missing required environment variable GITHUB_REPOSITORY}"

# Enforce release tag format
if [[ ! "${APP_VERSION}" =~ ^v[0-9]+\.[0-9]+\.[0-9]+ ]]; then
  echo "Error: APP_VERSION must be a release tag matching 'vX.Y.Z*' (got '${APP_VERSION}')" >&2
  exit 1
fi

# Define asset URLs
WIN_URL="https://github.com/${GITHUB_REPOSITORY}/releases/download/${APP_VERSION}/cellular-automata-windows.exe"
MAC_URL="https://github.com/${GITHUB_REPOSITORY}/releases/download/${APP_VERSION}/cellular-automata-macos.dmg"
LIN_URL="https://github.com/${GITHUB_REPOSITORY}/releases/download/${APP_VERSION}/cellular-automata-linux.AppImage"

# Verify that assets exist for release deployments
echo "Verifying release assets for ${APP_VERSION}..."
for url in "$WIN_URL" "$MAC_URL" "$LIN_URL"; do
  # Follow redirects but fail on HTTP errors (e.g. 404)
  if ! curl -sLfo /dev/null -I "$url"; then
    echo "Error: Required platform asset missing at $url" >&2
    exit 1
  fi
done
echo "All required platform assets verified."

jq -n \
  --arg app "Cellular Automata" \
  --arg version "${APP_VERSION}" \
  --arg commit "${COMMIT_SHA}" \
  --arg win "$WIN_URL" \
  --arg mac "$MAC_URL" \
  --arg lin "$LIN_URL" \
  '{
    "app": {
      "name": $app
    },
    "release": {
      "version": $version,
      "commit": $commit
    },
    "downloads": {
      "windows": $win,
      "macos": $mac,
      "linux": $lin
    }
  }' > site/release.json
