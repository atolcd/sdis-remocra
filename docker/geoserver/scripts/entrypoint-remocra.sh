#!/bin/bash
set -e

# Redéfition des accès à la base de données remocra
[ -n "${POSTGRES_DB_HOSTNAME}" ] && find /var/remocra/geoserver_data -type f -name "datastore.xml" -exec sed -i "s/<entry key=\"host\">localhost<\/entry>/<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>/g" {} \;
[ -n "${POSTGRES_DB_USERNAME}" ] && find /var/remocra/geoserver_data -type f -name "datastore.xml" -exec grep -l "<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>" {} \; -exec sed -i "s/<entry key=\"user\">.*<\/entry>/<entry key=\"user\">${POSTGRES_DB_USERNAME}<\/entry>/g" {} \;
[ -n "${POSTGRES_DB_PASSWORD}" ] && find /var/remocra/geoserver_data -type f -name "datastore.xml" -exec grep -l "<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>" {} \; -exec sed -i "s/<entry key=\"passwd\">.*<\/entry>/<entry key=\"passwd\">plain:${POSTGRES_DB_PASSWORD}<\/entry>/g" {} \;

# Pour forcer la mise à jour des credentials geoserver
[ -n "${GEOSERVER_ADMIN_USER}" ] && [ -n "${GEOSERVER_ADMIN_PASSWORD}" ] && rm -f /var/remocra/geoserver_data/.updatepassword.lock

# Entrypoint de kartoza/geoserver
/scripts/entrypoint.sh $@