#!/usr/bin/env bash
# Generates site/release.json for the GitHub Pages deployment.
#
# Required environment variables:
#   APP_VERSION  - Full release version string (e.g. v1.1.0-test-1)
#   COMMIT_SHA   - Git commit SHA of the deployed revision
set -euo pipefail

jq -n \
  --arg app "Cellular Automata" \
  --arg version "${APP_VERSION}" \
  --arg commit "${COMMIT_SHA}" \
  '{"app": $app, "version": $version, "commit": $commit}' \
  > site/release.json
