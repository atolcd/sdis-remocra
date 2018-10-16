#!/bin/bash

# Gestion des erreurs
abort() {
  (RED='\033[0;31m' && NC='\033[0m' && printf "${RED}\n------------------------------------------------------------\n- ABORTED - Le build a été interrompu -\n------------------------------------------------------------\n${NC}" >&2)
  exit 1
}
trap 'abort' ERR
set -e



cd $(dirname $0)
path=$(pwd)

PROJECT=remocra

#GITREPO=${GITREPO:-git://github.com/atolcd/sdis-remocra.git}
GITREPO=${GITREPO:-file:////${HOME}/projets/atolcd/sdis-remocra}
GITBRANCH=${GITBRANCH:-master}

# Répertoire de travail
WORKINGDIR=$path/${PROJECT}'build'


echo ""
echo "---"
echo "Exécution avec le contexte suivant :"
echo "---"
echo "--- Dossier courant          : $path"
echo "--- Répertoire de travail    : $WORKINGDIR"
echo "---"
echo "--- Dépôt GIT                : $GITREPO"
echo "--- Branche GIT              : $GITBRANCH"
echo "---"


echo ""
echo "---"
echo "Extraction des fichiers"

rm -rf $WORKINGDIR \
  && mkdir -p $WORKINGDIR/cloned

cd $WORKINGDIR/cloned \
  && git clone $GITREPO -b master $PROJECT \
  && cd $PROJECT \
  && git checkout $GITBRANCH \
  && cd ..


VERSION=`cat ${PROJECT}/remocra/src/main/java/fr/sdis83/remocra/web/RemocraController.java | grep "String VERSION_NUMBER" | grep -P '".*"' -o | sed s/\"//g`
echo ""
echo "---"
echo "--- Version de l'application : $VERSION"
echo "---"


echo ""
echo "---"
echo "--- Build de l'application"
echo "---"
$WORKINGDIR/cloned/$PROJECT/remocra/package.sh \
  && mv $WORKINGDIR/cloned/$PROJECT/remocra/target/remocra.war ${path} \
  && rm -rf $WORKINGDIR


echo ""
echo "---"
echo "--- Fichier construit        : ${path}/remocra.war"
echo "---"

