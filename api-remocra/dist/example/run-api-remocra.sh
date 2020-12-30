#! /bin/sh

cd $(dirname $0)

docker run --network="host" --rm --name api-remocra \
  -v "$(pwd)/config":/app/config -v "$(pwd)/log":/app/log \
  -e JAVA_OPTS="-Dapi-remocra.http.port=${API_REMOCRA_HTTP_PORT:-8883}
  -Dapi-remocra.database.dataSource.user=${POSTGRES_DB_USERNAME:-postgres}
  -Dapi-remocra.database.dataSource.password=${POSTGRES_DB_PASSWORD:-postgres}
  -Dapi-remocra.database.dataSource.serverName=${POSTGRES_DB_SERVERNAME:-localhost}
  -Dapi-remocra.database.dataSource.portNumber=${POSTGRES_DB_PORT:-5432}
  -Dapi-remocra.database.dataSource.databaseName=${POSTGRES_DB_NAME:-remocra}" \
  api-remocra
