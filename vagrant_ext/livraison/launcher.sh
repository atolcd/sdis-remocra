#!/bin/bash

# Gestion des erreurs
abort() {
  (RED='\033[0;31m' && NC='\033[0m' && printf "${RED}\n------------------------------------------------------------\n- ABORTED - L'installation / mise à jour a été interrompue -\n------------------------------------------------------------\n${NC}" >&2)
  exit 1
}
trap 'abort' ERR
set -e

# ------------------------------
# - INSTALLATION DE REMOCRA
# ------------------------------

# Dans le répertoire courant
cd $(dirname $0)

# ------------------------------
# - Préparation
# ----------

echo
echo "-------------------------------------------------"
echo "- Installation / mise à jour du serveur REMOcRA -"
echo "-------------------------------------------------"

if ( ! (whoami | grep root > /dev/null) ); then
  echo && echo "not root : sudo su"
  sudo su
fi



### URL du site
URL_SITE=={URL_SITE:-"http://localhost:8080/remocra/"}

### SDIS
export SRC_SDIS=${SRC_SDIS:-83}

### Base de données
export POSTGRES_DB_PASSWORD=${POSTGRES_DB_PASSWORD:-5f5145fsdht}
export REMOCRA_DB_PASSWORD=${REMOCRA_DB_PASSWORD:-4sc4dsf86}

### Tomcat
export TOMCAT_ADMIN_USERNAME=${TOMCAT_ADMIN_USERNAME:=remocraadm}
export TOMCAT_ADMIN_PASSWORD=${TOMCAT_ADMIN_PASSWORD:-remocraadmpwd1sd}
export GEOSERVER_ADMIN_PASSWORD=${GEOSERVER_ADMIN_PASSWORD:-de4df84ezf}

### Utilisateurs
export APP_PASSWORD=${APP_PASSWORD:-remocrad41sq}

### Courriels (par PDI)
export TECH_EMAIL=${TECH_EMAIL:-cva+remocraXX@atolcd.com}

### Divers
export APP_IGNKEYS=${APP_IGNKEYS:-"fjwf53vbh2ikn9q009g6mi7f"}

export GEOSERVER_URL_CIBLE=${GEOSERVER_URL_CIBLE:-localhost}

### Paramètres cachés réservés à un usage interne dans un contexte Box Vagrant par exemple
# (REMOCRA_FORCE_CREATE_DB_IF_ABSENT : true/false)
export REMOCRA_FORCE_CREATE_DB_IF_ABSENT=${REMOCRA_FORCE_CREATE_DB_IF_ABSENT:=false}



# Savoir si un paquet est installé. Retour 1 si installé, autre sinon. Ex : isPackageAbsent nomdupaquet
function isPackagePresent {
  yum list installed "$1" > /dev/null 2>&1
  if [ $? -eq 0 ]; then true; else false; fi
}
export -f isPackagePresent
# Installe le packet si nécessaire. Retour 0 si OK, autre sinon. Ex: yuminstallifneeded nomdupaquet
function assumePackageIsPresent {
  packagename=$1
  if isPackagePresent $packagename; then
    return 0
  else
    echo "Installation de $packagename..."
    yum -y install $packagename
    return $?
  fi
}
export -f assumePackageIsPresent



# Exemple filename extrait : 20160217_remocrapackage0.9.9
[ ! $( find /livraison -name "*_remocrapackage*.zip" | wc -l; ) -eq 1 ] && ( echo && echo "ERREUR : remocrapackage zip (un et un seul fichier attendu)"; exit 1; )
filebasename=$(basename /livraison/*_remocrapackage*.zip)
filebasename=${filebasename%.*}

# Extraction des fichiers livrés (à la racine)
assumePackageIsPresent unzip

unzip -q /livraison/${filebasename}.zip -d /livraison
cp -R /livraison/${filebasename}/server_rootdir/* /
rm -rf /livraison/${filebasename}
find /livraison/ -type f -name '*.sh' -exec chmod u+x '{}' \+

# Base + remocra
/livraison/install_base.sh
/livraison/install_remocra.sh

