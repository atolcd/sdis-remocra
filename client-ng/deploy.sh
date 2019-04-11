#!/bin/bash

# ------------------------------
# - Packaging et déploiement du client
# ------------------------------

# Dans le répertoire courant
cd $(dirname $0)
here=$(pwd)

SERVER=$1
SERVER=sdis83-remocra-preprod.hosting.priv.atolcd.com

npm run build \
  && cd dist && rm -rf client-ng.zip \
  && zip -r client-ng.zip . \
  && echo "Déploiement" \
  && scp client-ng.zip ${SERVER}:/tmp \
  && ssh ${SERVER} "rm -rf /var/remocra/client-ng/dist/* && unzip -q -o /tmp/client-ng.zip -d /var/remocra/client-ng/dist && rm -f /tmp/client-ng.zip"

