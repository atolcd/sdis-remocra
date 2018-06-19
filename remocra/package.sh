#!/bin/bash

# Gestion des erreurs
set -e

# ----------
# Création du war "de production" (modeinfo-able)
# ----------

cd $(dirname $0)

export MAVEN_OPTS="-Xms512m -Xmx1536m -XX:PermSize=512m -XX:MaxPermSize=1024m"

mvn install:install-file -Dfile=lib/irstv-cts.jar -DgroupId=org.cts -DartifactId=cts -Dversion=1.69 -Dpackaging=jar

REVNUMBER=$(git log -1 --pretty=format:%h | awk '{print substr($1, 1, 5)}')
# Java 7 : TLS 1.2 désactivé par défaut dans les versions avant 1.7.0_131-b31. Activation avec "-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"
mvn -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 clean verify -Dmaven.test.failure.ignore=false -P modeinfo-able

