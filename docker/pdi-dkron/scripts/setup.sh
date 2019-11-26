#!/bin/bash
set -e

# Redéfition des accès à la base de données remocra

# kettle.properties
KETTLE_PROPERTIES=/home/pdi/.kettle/kettle.properties
KETTLE_PROPERTIES_TMP=/tmp/kettle.properties
if [ -f "$KETTLE_PROPERTIES" ] && [ -s "$KETTLE_PROPERTIES" ]; then
  echo "Fichier $KETTLE_PROPERTIES non vide : remplacement des variables"
  [ -n "${POSTGRES_DB_HOSTNAME}" ] && sed "s/REMOCRA_POSTGIS_DATABASE_HOST.*/REMOCRA_POSTGIS_DATABASE_HOST = ${POSTGRES_DB_HOSTNAME}/g" ${KETTLE_PROPERTIES} > $KETTLE_PROPERTIES_TMP && cat $KETTLE_PROPERTIES_TMP > ${KETTLE_PROPERTIES} && rm $KETTLE_PROPERTIES_TMP
  [ -n "${POSTGRES_DB_USERNAME}" ] && sed "s/REMOCRA_POSTGIS_DATABASE_USER_NAME.*/REMOCRA_POSTGIS_DATABASE_USER_NAME = ${POSTGRES_DB_USERNAME}/g" ${KETTLE_PROPERTIES} > $KETTLE_PROPERTIES_TMP && cat $KETTLE_PROPERTIES_TMP > ${KETTLE_PROPERTIES} && rm $KETTLE_PROPERTIES_TMP
  [ -n "${POSTGRES_DB_PASSWORD}" ] && sed "s/REMOCRA_POSTGIS_DATABASE_USER_PASSWORD.*/REMOCRA_POSTGIS_DATABASE_USER_PASSWORD = ${POSTGRES_DB_PASSWORD}/g" ${KETTLE_PROPERTIES} > $KETTLE_PROPERTIES_TMP && cat $KETTLE_PROPERTIES_TMP > ${KETTLE_PROPERTIES} && rm $KETTLE_PROPERTIES_TMP
else
  echo "Fichier $KETTLE_PROPERTIES vide ou absent : création / remplacement"
  envsubst << "EOF" > ${KETTLE_PROPERTIES_TMP}
# Connexion à la base de données Postgis
REMOCRA_POSTGIS_DATABASE_HOST = ${POSTGRES_DB_HOSTNAME}
REMOCRA_POSTGIS_DATABASE_NAME = remocra
REMOCRA_POSTGIS_DATABASE_PORT = 5432
REMOCRA_POSTGIS_DATABASE_USER_NAME = ${POSTGRES_DB_USERNAME}
REMOCRA_POSTGIS_DATABASE_USER_PASSWORD = ${POSTGRES_DB_PASSWORD}
EOF
  cat $KETTLE_PROPERTIES_TMP > ${KETTLE_PROPERTIES} && rm $KETTLE_PROPERTIES_TMP
fi

# repositories.xml
REPOSITORIES_XML=/home/pdi/.kettle/repositories.xml
REPOSITORIES_XML_TMP=/tmp/repositories.xml
if [ -f "$REPOSITORIES_XML" ] && [ -s "$REPOSITORIES_XML" ]; then
  echo "Fichier $REPOSITORIES_XML non vide : remplacement des variables"
  [ -n "${POSTGRES_DB_HOSTNAME}" ] && sed "s/<server>.*<\/server>/<server>${POSTGRES_DB_HOSTNAME}<\/server>/g" ${REPOSITORIES_XML} > $REPOSITORIES_XML_TMP && cat $REPOSITORIES_XML_TMP > ${REPOSITORIES_XML} && rm $REPOSITORIES_XML_TMP
  [ -n "${POSTGRES_DB_USERNAME}" ] && sed "s/<username>.*<\/username>/<username>${POSTGRES_DB_USERNAME}<\/username>/g" ${REPOSITORIES_XML} > $REPOSITORIES_XML_TMP && cat $REPOSITORIES_XML_TMP > ${REPOSITORIES_XML} && rm $REPOSITORIES_XML_TMP
  [ -n "${POSTGRES_DB_PASSWORD}" ] && sed "s/<password>.*<\/password>/<password>${POSTGRES_DB_PASSWORD}<\/password>/g" ${REPOSITORIES_XML} > $REPOSITORIES_XML_TMP && cat $REPOSITORIES_XML_TMP > ${REPOSITORIES_XML} && rm $REPOSITORIES_XML_TMP
else
  echo "Fichier $REPOSITORIES_XML vide ou absent : création / remplacement"
  envsubst << "EOF" > ${REPOSITORIES_XML_TMP}
<?xml version="1.0" encoding="UTF-8"?>
<repositories>
  <connection>
    <name>remocra</name>
    <server>${POSTGRES_DB_HOSTNAME}</server>
    <type>POSTGRESQL</type>
    <access>Native</access>
    <database>remocra_ref_pdi</database>
    <port>5432</port>
    <username>${POSTGRES_DB_USERNAME}</username>
    <password>${POSTGRES_DB_PASSWORD}</password>
    <servername/>
    <data_tablespace/>
    <index_tablespace/>
    <attributes>
      <attribute><code>FORCE_IDENTIFIERS_TO_LOWERCASE</code><attribute>N</attribute></attribute>
      <attribute><code>FORCE_IDENTIFIERS_TO_UPPERCASE</code><attribute>N</attribute></attribute>
      <attribute><code>IS_CLUSTERED</code><attribute>N</attribute></attribute>
      <attribute><code>PORT_NUMBER</code><attribute>5432</attribute></attribute>
      <attribute><code>QUOTE_ALL_FIELDS</code><attribute>N</attribute></attribute>
      <attribute><code>SUPPORTS_BOOLEAN_DATA_TYPE</code><attribute>N</attribute></attribute>
      <attribute><code>USE_POOLING</code><attribute>N</attribute></attribute>
    </attributes>
  </connection>
  <repository>
	<id>KettleDatabaseRepository</id>
    <name>ref_pdi_remocra</name>
    <description>ref_pdi_remocra</description>
    <connection>remocra</connection>
  </repository>
</repositories>
EOF
  cat $REPOSITORIES_XML_TMP > ${REPOSITORIES_XML} && rm $REPOSITORIES_XML_TMP
fi

exit 0