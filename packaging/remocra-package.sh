#!/bin/bash

# Gestion des erreurs
abort() {
  (RED='\033[0;31m' && NC='\033[0m' && printf "${RED}\n------------------------------------------------------------\n- ABORTED - Le packaging a été interrompu -\n------------------------------------------------------------\n${NC}" >&2)
  exit 1
}
trap 'abort' ERR
set -e

# ----------
# Préparation d'un paquetage de livraison et instructions de la suite des événements (par défaut sur git / master)
# ----------

# Dans le répertoire courant
cd $(dirname $0)
path=$(pwd)

PROJECT=remocra

# URL repository et branche
GITREPO=git://github.com/atolcd/sdis-remocra.git
#GITREPO=file:////${HOME}/projets/atolcd/sdis-remocra
GITBRANCH=master

# Répertoire de travail
WORKINGDIR=$path/${PROJECT}'package'



echo ""
echo "---"
echo "Exécution avec le contexte suivant :"
echo "---"
echo "--- Dossier courant          : " $path
echo "--- Répertoire de travail    : $WORKINGDIR"
echo "---"
echo "--- Dépôt GIT                : $GITREPO"
echo "--- Branche GIT              : $GITBRANCH"
echo "---"



echo ""
echo "---"
echo "Extraction des fichiers"

rm -rf $WORKINGDIR
mkdir -p $WORKINGDIR/cloned
mkdir -p $WORKINGDIR/final/server_rootdir

cd $WORKINGDIR/cloned
git clone $GITREPO -b master $PROJECT
cd $PROJECT
git checkout $GITBRANCH
cd ..


VERSION=`cat ${PROJECT}/remocra/src/main/java/fr/sdis83/remocra/web/RemocraController.java | grep "String VERSION_NUMBER" | grep -P '".*"' -o | sed s/\"//g`
echo ""
echo "---"
echo "--- Version de l'application : $VERSION"
echo "---"



echo ""
echo "---"
echo "Copie des fichiers de base"

# Fichiers de base
mv $WORKINGDIR/cloned/$PROJECT/server/sdis-remocra/* $WORKINGDIR/final/server_rootdir



echo ""
echo "---"
echo "Packaging de l'application"

# Application
cd $WORKINGDIR/cloned/$PROJECT/remocra
./package.sh
mkdir -p $WORKINGDIR/final/server_rootdir/var/lib/tomcat6/webapps/
mv $WORKINGDIR/cloned/$PROJECT/remocra/target/remocra.war $WORKINGDIR/final/server_rootdir/var/lib/tomcat6/webapps/


echo ""
echo "---"
echo "Création de l'archive finale"

TODAY=$(date +"%Y%m%d")
REPNAME=${TODAY}_${PROJECT}package${VERSION}
mv $WORKINGDIR/final $WORKINGDIR/$REPNAME
cd $WORKINGDIR
zip -rq $REPNAME.zip $REPNAME

rm -rf $WORKINGDIR/$REPNAME
rm -rf $WORKINGDIR/cloned



echo ""
echo "---"
echo "L'archive finale est créée :"
echo "--- Archive               : $WORKINGDIR/$REPNAME.zip"

