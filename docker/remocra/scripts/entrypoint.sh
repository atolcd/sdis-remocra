#!/bin/bash
set -e

# Port
sed -i "s/port=\".*\" protocol=\"HTTP\/1.1\"/port=\"${REMOCRA_PORT}\" protocol=\"HTTP\/1.1\"/" /usr/local/tomcat/conf/server.xml

# Priorité au paramètre le plus à droite
export REMOCRA_OPTS="\
  -Ddatabase.username=${POSTGRES_DB_USERNAME} \
  -Ddatabase.password=${POSTGRES_DB_PASSWORD} \
  -Ddatabase.url=jdbc\:postgresql\://${POSTGRES_DB_HOSTNAME}\:5432/remocra \
  -Dclient-ng.dir=/usr/local/tomcat/webapps/remocra/static/ \
  \
  -Djava.awt.headless=true -server -Xms${REMOCRA_INITIAL_MEMORY:-1G} -Xmx${REMOCRA_MAXIMUM_MEMORY:-2G} \
  -Duser.timezone='Europe/Paris' \
  -Dfile.encoding=UTF8 -Djavax.servlet.request.encoding=UTF-8 -Djavax.servlet.response.encoding=UTF-8"
export JAVA_OPTS="${REMOCRA_OPTS} ${JAVA_OPTS}"

catalina.sh run