#!/usr/bin/env sh
cd "$(dirname "$0")"
if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven n'est pas installé. Ouvrez le projet dans NetBeans ou installez Maven."
  exit 1
fi
mvn clean javafx:run
