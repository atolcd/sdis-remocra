#!/bin/bash
# Récupération de la structure de la base du serveur de production

cd $(dirname $0)

export SRC_SDIS=83
export SRC_SERVER=sdis83-remocra-prod.hosting

sh remote_dump.sh
