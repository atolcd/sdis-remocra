#!/bin/bash

# ------------------------------
# - Packaging et déploiement du client
# ------------------------------

# Dans le répertoire courant
cd $(dirname $0)

if [ $# -lt 1 ]
then
  echo 'usage: deploy.sh servername [port]'
  exit 1
fi


SERVER=$1
port=$2
port=${port:=22}

npm run build \
  && cd dist && rm -rf client-ng.zip \
  && zip -r client-ng.zip . \
  && echo "Déploiement" \
  && scp -P $port client-ng.zip ${SERVER}:/tmp \
  && ssh ${SERVER} -p $port "rm -rf /var/lib/tomcat6/webapps/remocra/static/* && unzip -q -o /tmp/client-ng.zip "remocra/static/*" -d /var/lib/tomcat6/webapps/remocra/static && mv /var/lib/tomcat6/webapps/remocra/static/remocra/static/* /var/lib/tomcat6/webapps/remocra/static && rm -rf /var/lib/tomcat6/webapps/remocra/static/remocra"

