#! /bin/sh

cd $(dirname $0)

# TODO see -XX:MaxRAM=...

# Used in log4j2.xml
export LOG_DIR=${LOG_DIR:-/app/log}
mkdir -p "$LOG_DIR"

export GCLOG_DIR=${GCLOG_DIR:-${LOG_DIR}/gc}
mkdir -p "$GCLOG_DIR"

export CONFIG_DIR=${CONFIG_DIR:-/app/config/}
mkdir -p "$CONFIG_DIR"

chown -R remocra:remocra /app
 
GC_OPTS="-XX:+DisableExplicitGC -XX:+ScavengeBeforeFullGC"
GC_LOG_OPTS="-Xlog:gc*:file=${GCLOG_DIR}/api-remocra-gc.log::filecount=10,filesize=1024"
JAVA_OPTS="$JAVA_OPTS -Dorg.jboss.logging.provider=slf4j"

JAVA_OPTS="$JAVA_OPTS -Dapi-remocra.http.port=${API_REMOCRA_HTTP_PORT:-8883}"
JAVA_OPTS="$JAVA_OPTS -Dapi-remocra.database.dataSource.user=${POSTGRES_DB_USERNAME:-postgres}"
JAVA_OPTS="$JAVA_OPTS -Dapi-remocra.database.dataSource.password=${POSTGRES_DB_PASSWORD:-postgres}"
JAVA_OPTS="$JAVA_OPTS -Dapi-remocra.database.dataSource.serverName=${POSTGRES_DB_SERVERNAME:-localhost}"
JAVA_OPTS="$JAVA_OPTS -Dapi-remocra.database.dataSource.portNumber=${POSTGRES_DB_PORT:-5432}"
JAVA_OPTS="$JAVA_OPTS -Dapi-remocra.database.dataSource.databaseName=${POSTGRES_DB_NAME:-remocra}"

JAVA_OPTS="$JAVA_OPTS -Xms${INITIAL_MEMORY:-500m} -Xmx${MAXIMUM_MEMORY:-1g} -XX:MaxRAM=${MAXIMUM_RAM:-2g}"
JAVA_OPTS="$JAVA_OPTS -Xverify:none -XX:+UseCompressedOops -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:+CMSScavengeBeforeRemark"

exec $JAVA_HOME/bin/java  $JAVA_OPTS  $GC_LOG_OPTS $GC_OPTS \
  -cp 'lib/*' fr.sdis83.remocra.WebApp \
  -l ${CONFIG_DIR}/log4j2.xml -c ${CONFIG_DIR}/api-remocra.conf \
  "$@"