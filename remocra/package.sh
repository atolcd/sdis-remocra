#!/bin/bash
# ----------
# Création du war "de production" (modeinfo-able)
# ----------

cd $(dirname $0)

export MAVEN_OPTS="-Xms512m -Xmx1536m -XX:PermSize=512m -XX:MaxPermSize=1024m"

mvn install:install-file -Dfile=lib/irstv-cts.jar -DgroupId=org.cts -DartifactId=cts -Dversion=1.69 -Dpackaging=jar

REVNUMBER=$(git log -1 --pretty=format:%h | awk '{print substr($1, 1, 5)}')
mvn clean verify -Dmaven.test.failure.ignore=false -P modeinfo-able

