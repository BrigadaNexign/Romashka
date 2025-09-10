#!/usr/bin/env sh
set -eu

# Fail fast if connection details are missing
: "${LIQUIBASE_URL:?Set LIQUIBASE_URL, e.g. jdbc:postgresql://db:5432/appdb}"
: "${LIQUIBASE_USERNAME:?Set LIQUIBASE_USERNAME}"
: "${LIQUIBASE_PASSWORD:?Set LIQUIBASE_PASSWORD}"

# Allow overriding the changelog path and command via env
CHANGELOG_FILE="${CHANGELOG_FILE:-/liquibase/changelog/changelog-master.xml}"
CMD="${LIQUIBASE_COMMAND:-update}"

echo "Liquibase will run '${CMD}' against ${LIQUIBASE_URL} using ${CHANGELOG_FILE}"
exec liquibase \
  --changelog-file="${CHANGELOG_FILE}" \
  --url="${LIQUIBASE_URL}" \
  --username="${LIQUIBASE_USERNAME}" \
  --password="${LIQUIBASE_PASSWORD}" \
  ${CMD}
