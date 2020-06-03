#!/bin/bash
set -e

# Redéfition des accès à la base de données remocra
[ -n "${POSTGRES_DB_HOSTNAME}" ] && find /var/remocra/geoserver_data -type f -name "datastore.xml" -exec sed -i "s/<entry key=\"host\">localhost<\/entry>/<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>/g" {} \;
[ -n "${POSTGRES_DB_USERNAME}" ] && find /var/remocra/geoserver_data -type f -name "datastore.xml" -exec grep -l "<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>" {} \; -exec sed -i "s/<entry key=\"user\">.*<\/entry>/<entry key=\"user\">${POSTGRES_DB_USERNAME}<\/entry>/g" {} \;
[ -n "${POSTGRES_DB_PASSWORD}" ] && find /var/remocra/geoserver_data -type f -name "datastore.xml" -exec grep -l "<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>" {} \; -exec sed -i "s/<entry key=\"passwd\">.*<\/entry>/<entry key=\"passwd\">plain:${POSTGRES_DB_PASSWORD}<\/entry>/g" {} \;

# Credentials geoserver
CLASSPATH=${CLASSPATH:-$GEOSERVER_HOME/webapps/geoserver/WEB-INF/lib/}
make_hash() {
  NEW_PASSWORD=$1
  (echo "digest1:" && java -classpath $(find $CLASSPATH -regex ".*jasypt-[0-9]\.[0-9]\.[0-9].*jar") org.jasypt.intf.cli.JasyptStringDigestCLI digest.sh algorithm=SHA-256 saltSizeBytes=16 iterations=100000 input="$NEW_PASSWORD" verbose=0) | tr -d '\n'
}
[ -n "${GEOSERVER_ADMIN_USER}" ] && sed -i "s/name=\"[^\"]*\"/name=\"${GEOSERVER_ADMIN_USER}\"/g" ${GEOSERVER_DATA_DIR}/security/usergroup/default/users.xml
[ -n "${GEOSERVER_ADMIN_USER}" ] && sed -i "s/username=\"[^\"]*\"/username=\"${GEOSERVER_ADMIN_USER}\"/g" ${GEOSERVER_DATA_DIR}/security/role/default/roles.xml
[ -n "${GEOSERVER_ADMIN_PASSWORD}" ] && PWD_HASH=$(make_hash $GEOSERVER_ADMIN_PASSWORD) && sed -i "s/password=\"[^\"]*\"/password=\"${PWD_HASH//\//\\/}\"/g" ${GEOSERVER_DATA_DIR}/security/usergroup/default/users.xml

# Priorité au paramètre le plus à droite
export GEOSERVER_OPTS=" \
  -Djetty.http.port=${GEOSERVER_PORT:-8090} \
  -Djava.awt.headless=true -server -Xms${GEOSERVER_INITIAL_MEMORY:-1G} -Xmx${GEOSERVER_MAXIMUM_MEMORY:-2G} \
  -Duser.timezone='Europe/Paris' \
  -Dfile.encoding=UTF8 -Djavax.servlet.request.encoding=UTF-8 -Djavax.servlet.response.encoding=UTF-8 \
  -Dorg.geotools.referencing.forceXY=true -Dorg.geotools.shapefile.datetime=true -Dorg.geotools.shapefile.datetime=true"
export JAVA_OPTS="${GEOSERVER_OPTS} ${JAVA_OPTS}"

# Démarrage de GeoServer
cd ${GEOSERVER_HOME}/bin
sh startup.sh $@
